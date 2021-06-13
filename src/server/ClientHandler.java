package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class is designed to run the server in multi-thread mode.
 * Every object of this class will be a thread which will handle a client.
 *
 * @author Taha Emli
 * @version 1
 */
public class ClientHandler extends Thread{
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Player player;

    public ClientHandler(Socket socket, Player player) {
        this.socket = socket;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Couldn't get the streams from the socket.");
        }
        this.player = player;
    }

    @Override
    public void run() {
        setName();
        setReady();

        ClientReader clientReader = new ClientReader();
        clientReader.start();
    }

    /**
     * This method will set a name for the player.
     * It also checks if the name is duplicate or not, using checkName method of the Game class
     */
    private void setName() {
        String response = "";
        String name = "";
        do {
            try {
                name = dataInputStream.readUTF();
            } catch (IOException e) {
                System.err.println("Couldn't get data from the socket.");
            }

            response = (Game.getInstance().checkName(name) ? "No" : "OK");
            player.setUsername(name);

            try {
                dataOutputStream.writeUTF(response);
            } catch (IOException e) {
                System.err.println("Couldn't send data to the client.");
            }
        } while (!response.equals("OK"));
        System.out.println(name + " is connected.");
    }

    /**
     * It will tell the server that the client is ready for the game.
     */
    private void setReady() {
        try {
            dataInputStream.readUTF();
        } catch (IOException e) {
            System.err.println("Couldn't get the ready sign from the client.");
        }
        player.setReady(true);
        System.out.println("+ " + player.getUsername() + " is ready!");
    }

    /**
     * getter of the player field
     * @return the player field
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * sends a text to the client
     * @param text the text which will be sent to the client
     */
    public void write(String text) {
        try {
            dataOutputStream.writeUTF(text);
        } catch (IOException e) {
            System.err.println("Couldn't send data to the client.");
        }
    }

    private class ClientReader extends Thread {

        @Override
        public void run() {
            String text = "";
            while (true) {
                try {
                    text = dataInputStream.readUTF();
                } catch (IOException e) {
                    System.err.println("There was an error in getting data from the client.");
                }
                processText(text);
            }
        }

        /**
         * It will process that what is the right reaction to the input string
         * @param text input string received from the client
         */
        private void processText(String text) {
            if (!player.isAlive()) {
                write("You're dead. You can't talk or do anything. You can just watch the rest of the game.");
                return;
            }

            if (Game.getInstance().getState().equals("beginning")) {
                write("Just wait for other players to get ready...");
                return;
            }

            if (Game.getInstance().getState().equals("day")) {
                if (player.isSilent()) {
                    write("The psychiatrist has made you silent. You can't talk today.");
                } else {
                    Game.getInstance().chat(ConsoleColors.ANSI_GREEN + player.getUsername() + ": " +
                            ConsoleColors.ANSI_RESET + text);
                }
                return;
            }

            if (Game.getInstance().getState().equals("voting")) {
                try {
                    int vote = Integer.parseInt(text);
                    if (vote <= 0 || vote > Game.getInstance().countAlivePlayers())
                        throw new IndexOutOfBoundsException();
                    player.setVote(vote);
                    write("Your vote has been submitted. You can change it until the end of the voting phrase.");
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    write("Invalid input. Enter the index of one of the players.");
                }
                return;
            }

            if (Game.getInstance().getState().equals("night-lecter") && player.getRole() instanceof Role.DrLecter) {
                try {
                    int survivor = Integer.parseInt(text);
                    if (survivor <= 0 || survivor > Game.getInstance().countMafias())
                        throw new IndexOutOfBoundsException();
                    ((Role.DrLecter) player.getRole()).setSurvivor(survivor);

                    write("OK, now you can recommend a player to the godfather by their index: ");
                    int index = 1;
                    for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                        if (ch.getPlayer().isAlive()) {
                            write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                            index++;
                        }
                    }

                    Game.getInstance().setState("night");
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    write("Invalid input. Enter the index of one of the players.");
                }
                return;
            }

            if ((Game.getInstance().getState().equals("night") || Game.getInstance().getState().equals("night-lecter"))
                && (player.getRole().canSetTarget())) {
                try {
                    int target = Integer.parseInt(text);
                    if (target <= 0 || target > Game.getInstance().countAlivePlayers())
                        throw new IndexOutOfBoundsException();
                    player.getRole().setTarget(target);
                    write("Your vote has been submitted. You can change it until the end of the night.");
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    write("Invalid input. Enter the index of one of the players.");
                }
                return;
            }

            if ((Game.getInstance().getState().equals("night-lecter") || Game.getInstance().getState().equals("nigth"))
                    && player.getRole() instanceof Role.DieHard) {
                dieHardNightAct(text);
                return;
            }

            if (Game.getInstance().getState().equals("night-lecter") || Game.getInstance().getState().equals("night")) {
                write("It's night...");
                return;
            }

        }
        private void mafiaNightAct(String text) {
            Role role = player.getRole();

            if (role.isDoneJob()) {
                write("You've done your job. Try to sleep.");
                return;
            }

            try {
                int target = Integer.parseInt(text);
                if (target <= 0 || target > Game.getInstance().countAlivePlayers())
                    throw new IndexOutOfBoundsException();
                player.getRole().setTarget(target);
                write("Done. Now try to sleep.");
                role.setDoneJob(true);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                write("Invalid input. Enter the index of one of the players.");
            }
        }

        private void drLecterNightAct(String text) {
            Role.DrLecter role = (Role.DrLecter) player.getRole();

            if (role.isDoneJob()) {
                write("You've done your job. Try to sleep.");
            }

            if (!role.isDoneReviving()) {
                try {
                    int survivor = Integer.parseInt(text);
                    if (survivor <= 0 || survivor > Game.getInstance().countMafias())
                        throw new IndexOutOfBoundsException();

                    //check if Dr.Lecter is reviving himself for more than one time or not
                    if (Game.getInstance().findAliveMafiaByIndex(survivor) == player) {
                        if (role.getNumberOfRevivingHimself() == 0) {
                            write("You can't revive yourself because you've already done it once. Try to revive another mafia.");
                            return;
                        }
                        role.setNumberOfRevivingHimself(0);
                    }

                    role.setSurvivor(survivor);
                    role.setDoneReviving(true);
                    write("OK.");

                    //finding the clientHandler of Dr.Lecter to pass it to the nightAct method to do its second task
                    ClientHandler ch = null;
                    for (ClientHandler clientHandler : Game.getInstance().getClientHandlers()) {
                        if (clientHandler.getPlayer().getRole() instanceof Role.DrLecter) {
                            ch = clientHandler;
                            break;
                        }
                    }

                    role.nightAct(ch);
                } catch (IllegalArgumentException e) {
                    write("Invalid input. Enter the index of one of the players.");
                }
            } else {
                mafiaNightAct(text);
            }
        }

        private void dieHardNightAct(String text) {
            Player player = Game.getInstance().findRole(new Role.DieHard());
            Role.DieHard role = (Role.DieHard) player.getRole();

            if (role.getInquiry() < 1) {
                write("You're out of limit. You can't do anything. Try to sleep.");
                return;
            }

            if (role.isDoneJob()) {
                write("You've done your work. Try to sleep.");
                return;
            }

            try {
                int choice = Integer.parseInt(text);
                if (choice != 0 && choice != 1)
                    throw new IllegalArgumentException();

                switch (choice) {
                    case 1:
                        role.setInquiry(role.getInquiry() - 1);
                        role.setWantInquiry(true);
                        role.setDoneJob(true);
                        write("OK. We will have an inquiry tomorrow.");
                        return;
                    case 0:
                        role.setDoneJob(true);
                        write("OK.");
                }
            } catch (IllegalArgumentException e) {
                write("Invalid input. Enter 1 if you want inquiry or 0 otherwise.");
            }
        }
    }
}
