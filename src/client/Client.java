package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static DataInputStream dataInputStream;
    private static DataOutputStream dataOutputStream;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Hi, welcome to our Mafia Banquet :)))");
        while (connectToServer());
    }

    private static boolean connectToServer() {
        System.out.println("Enter the ip of the server:");
        String ip = scanner.nextLine();
        System.out.println("Now Enter the port of the server:");
        int port = scanner.nextInt();

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

}
