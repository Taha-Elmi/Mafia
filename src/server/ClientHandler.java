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

    /**
     * It will close the socket
     */
    public void close() {
        write("exit");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Couldn't close the socket.");
        }
    }

    /**
     * This thread will always read data from the client.
     *
     * @author Taha Elmi
     */
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

                if (text.equals("exit")) {
                    player.setAlive(false);
                    close();
                    Game.getInstance().chat(ConsoleColors.ANSI_RED + player.getUsername() + " has left the game." + ConsoleColors.ANSI_RESET);
                    break;
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

            if (Game.getInstance().getState().equals("mayor-time")) {
                if (player.getRole() instanceof Role.Mayor)
                    mayorTime(text);
                else
                    write("Wait for the mayor to decide for the voting.");
            }

            if (Game.getInstance().getState().equals("night")) {
                String role = player.getRole().toString();
                switch (role) {
                    case "Godfather", "a Simple Mafia" -> mafiaNightAct(text);
                    case "Dr.Lecter" -> drLecterNightAct(text);
                    case "Doctor" -> doctorNightAct(text);
                    case "Detective" -> detectiveNightAct(text);
                    case "Professional" -> professionalNightAct(text);
                    case "Psychiatrist" -> psychiatristNightAct(text);
                    case "Die Hard" -> dieHardNightAct(text);
                    default -> write("It's night...");
                }
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
                player.getRole().setTarget(Game.getInstance().findAlivePlayerByIndex(target));
                Game.getInstance().mafiaChat(ConsoleColors.ANSI_PURPLE + player.getUsername() + " recommends: " +
                        Game.getInstance().findAlivePlayerByIndex(target).getUsername() + ConsoleColors.ANSI_RESET);
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

                    role.setSurvivor(Game.getInstance().findAliveMafiaByIndex(survivor));
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

        private void doctorNightAct(String text) {
            Role.Doctor role = (Role.Doctor) player.getRole();

            if (role.isDoneJob()) {
                write("You've done your job. Try to sleep.");
                return;
            }

            try {
                int target = Integer.parseInt(text);
                if (target <= 0 || target > Game.getInstance().countAlivePlayers())
                    throw new IndexOutOfBoundsException();

                //check if the doctor is reviving himself for more than one time or not
                if (Game.getInstance().findAlivePlayerByIndex(target) == player) {
                    if (role.getNumberOfRevivingHimself() == 0) {
                        write("You can't revive yourself because you've already done it once. Try to revive another mafia.");
                        return;
                    }
                    role.setNumberOfRevivingHimself(0);
                }

                role.setTarget(Game.getInstance().findAlivePlayerByIndex(target));
                write("Done. Now try to sleep.");
                role.setDoneJob(true);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                write("Invalid input. Enter the index of one of the players.");
            }
        }

        private void detectiveNightAct(String text) {
            Role.Detective role = (Role.Detective) player.getRole();

            if (role.isDoneJob()) {
                write("You've done your job. Try to sleep.");
                return;
            }

            try {
                int target = Integer.parseInt(text);
                if (target <= 0 || target > Game.getInstance().countAlivePlayers())
                    throw new IndexOutOfBoundsException();

                Player suspect = Game.getInstance().findAlivePlayerByIndex(target);
                if ((suspect.getRole() instanceof Role.Mafia) && !(suspect.getRole() instanceof Role.GodFather))
                    write("Good job. Yes, he is one of the mafias.");
                else
                    write("Nope. He is one of the citizens.");

                role.setDoneJob(true);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                write("Invalid input. Enter the index of one of the players.");
            }
        }

        private void professionalNightAct(String text) {
            Role.Professional role = (Role.Professional) player.getRole();

            if (role.isDoneJob()) {
                write("You've done your job. Try to sleep.");
                return;
            }

            try {
                int target = Integer.parseInt(text);
                if (target < 0 || target > Game.getInstance().countAlivePlayers())
                    throw new IndexOutOfBoundsException();

                write("Done. You'll see the result of your shot tomorrow :). You can sleep now.");
                role.setTarget(Game.getInstance().findAlivePlayerByIndex(target));
                role.setDoneJob(true);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                write("Invalid input. Enter the index of one of the players.");
            }
        }

        private void psychiatristNightAct(String text) {
            Role.Psychiatrist role = (Role.Psychiatrist) player.getRole();

            if (role.isDoneJob()) {
                write("You've done your job. Try to sleep.");
                return;
            }

            try {
                int target = Integer.parseInt(text);
                if (target <= 0 || target > Game.getInstance().countAlivePlayers())
                    throw new IndexOutOfBoundsException();

                Player psycho = Game.getInstance().findAlivePlayerByIndex(target);
                role.setTarget(psycho);
                write("Done, " + psycho.getUsername() + " will be silent tomorrow =]");
                role.setDoneJob(true);
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                write("Invalid input. Enter the index of one of the players.");
            }
        }

        private void dieHardNightAct(String text) {
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

        private void mayorTime(String text) {
            Role.Mayor role = (Role.Mayor) player.getRole();

            if (role.isDoneJob()) {
                write("We've got your answer, dear mayor. It's not mutable.");
                return;
            }

            try {
                int choice = Integer.parseInt(text);
                if (choice != 0 && choice != 1)
                    throw new IllegalArgumentException();

                switch (choice) {
                    case 1:
                        role.setCancel(true);
                        role.setDoneJob(true);
                        write("OK. The voting will be canceled.");
                        return;
                    case 0:
                        role.setDoneJob(true);
                        write("OK. The voting won't be canceled.");
                }
            } catch (IllegalArgumentException e) {
                write("Invalid input. Enter 1 if you want inquiry or 0 otherwise.");
            }
        }
    }
}
