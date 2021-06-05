package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Couldn't get the streams from the socket.");
        }

    }

    @Override
    public void run() {
        checkName();
    }

    private void checkName() {

    }
}
