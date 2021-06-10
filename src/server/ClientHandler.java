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
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    write("Invalid input. Enter the index of one of the players.");
                }
                return;
            }

            if (Game.getInstance().getState().equals("night")) {
                write("It's night...");
                return;
            }
        }
    }
}
