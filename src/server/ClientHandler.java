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
public class ClientHandler implements Runnable{
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

            try {
                dataOutputStream.writeUTF(response);
            } catch (IOException e) {
                System.err.println("Couldn't send data to the client.");
            }
        } while (!response.equals("OK"));
    }
}
