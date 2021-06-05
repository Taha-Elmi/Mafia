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
    }

    /**
     * This method will set a name for the player.
     * It also checks if the name is duplicate or not, using checkName method of the GameServer class
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

            response = (GameServer.getInstance().checkName(name) ? "No" : "OK");
            player.setUsername(name);

            try {
                dataOutputStream.writeUTF(response);
            } catch (IOException e) {
                System.err.println("Couldn't send data to the client.");
            }
        } while (!response.equals("OK"));
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
        System.out.println(player.getUsername() + " is ready!");
    }
}
