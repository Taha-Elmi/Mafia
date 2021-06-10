package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class will handle everything that a client does on its system
 *
 * @author Taha Elmi
 * @version 1
 */
public class Client {
    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Hi, welcome to our Mafia Banquet :)))");
        while (!connectToServer());
        setName();
        setReady();

        UserReader userReader = new UserReader();
        userReader.start();

        ServerReader serverReader = new ServerReader();
        serverReader.start();
    }

    /**
     * This method will try to connect the client to a game server
     * @return true if it could connect successfully and false otherwise
     */
    private static boolean connectToServer() {
        System.out.println("Enter the ip of the server:");
        String ip = scanner.nextLine();
        System.out.println("Now Enter the port of the server:");
        int port = Integer.parseInt(scanner.nextLine());

        try {
            socket = new Socket(ip, port);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException e) {
            System.err.println("Couldn't connect to the server.");
            return false;
        }
    }

    /**
     * This method will set a name for the player.
     * It also checks if the name is duplicate or not, using checkName method of the Game class
     */
    private static void setName() {
        System.out.println("Enter a username for yourself:");

        String name;
        String response = "";

        while (true) {
            name = scanner.nextLine();
            try {
                dataOutputStream.writeUTF(name);
                response = dataInputStream.readUTF();
            } catch (IOException e) {
                System.err.println("Couldn't send to the Server");
            }

            if (!response.equals("OK"))
                System.out.println("There is already another user with this username.\n" +
                        "Please submit another name for yourself:");
            else
                break;
        }

        System.out.println("You have been successfully entered the game with the username: " + name);
    }

    /**
     * It will tell the server that the client is ready for the game.
     */
    private static void setReady() {
        String input;
        do {
            System.out.println("Type \"ok\" when you get ready.");
            input = scanner.nextLine();
        } while (!input.equalsIgnoreCase("ok"));

        try {
            dataOutputStream.writeUTF("ok");
        } catch (IOException e) {
            System.err.println("Couldn't send data to the server");
        }

        System.out.println("Nice! Now wait for other players to get ready and then the game will begin...");
    }

    private static class UserReader extends Thread {

        @Override
        public void run() {
            String text;
            while (true) {
                text = scanner.nextLine();
                try {
                    dataOutputStream.writeUTF(text);
                } catch (IOException e) {
                    System.err.println("Couldn't send data to the server.");
                }
            }
        }
    }

    private static class ServerReader extends Thread {

        @Override
        public void run() {
            String text = "";
            while (true) {
                try {
                    text = dataInputStream.readUTF();
                } catch (IOException e) {
                    System.err.println("There was an error in getting data from the server.");
                }
                System.out.println(text);
            }
        }
    }
}
