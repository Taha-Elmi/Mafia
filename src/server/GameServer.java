package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class is the main class of the server, which will handle the logic of the game and move it on.
 * It uses the SingleTon pattern, so that every client has access to one instance of the server and its data.
 *
 * @author Taha Elmi
 * @version 1
 */
public class GameServer {
    private static final GameServer instance = new GameServer(3);
    private static int port = 2000;
    private static int numberOfPlayers;
    private static ArrayList<Player> players;
    private static String state;

    private GameServer(int numberOfPlayers) {
        GameServer.numberOfPlayers = numberOfPlayers;
        players = new ArrayList<>();
        state = "beginning";
    }

    public static void main(String[] args) {
        waitForClients();
        while (!checkIfReady());
        randomizePlayers();
    }

    /**
     * It will give the single instance of the class GameServer, due to the use of SingleTon pattern
     * @return the instance
     */
    public static GameServer getInstance() {
        return instance;
    }

    public static String getState() {
        return state;
    }

    /**
     * This method will create connections with the clients
     */
    private static void waitForClients() {
        ServerSocket welcomingSocket = null;
        try {
            welcomingSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("The port " + port + " was not free. Trying the port " + (port + 1) + "...");
            port++;
            waitForClients();
            return;
        }

        for (int i = 0; i < numberOfPlayers; i++) {
            Socket socket = null;
            try {
                socket = welcomingSocket.accept();
            } catch (IOException e) {
                System.err.println("There was an error in connecting to the client.");
            }

            Player player = new Player("PleaseDontChooseThisName");
            players.add(player);
            ClientHandler clientHandler = new ClientHandler(socket, player);
            clientHandler.start();
        }
    }

    /**
     * checks is a name is already used as a username for any user or not
     * @param name the name that will be search in the server
     * @return true if there was any similar name in the server, and false otherwise
     */
    protected boolean checkName(String name) {
        for (Player player : players) {
            if (player.getUsername().equals(name))
                return true;
        }
        return false;
    }

    /**
     * It checks if all players are ready or not
     * @return true if all of the players are ready, and false otherwise
     */
    private static boolean checkIfReady() {
        for (Player player : players) {
            //Here we have a mysterious bug. If we don't put the print below, it won't exit the loop :|
            System.out.print("");
            if (!player.isReady())
                return false;
        }
        return true;
    }

    /**
     * It will randomize the sequence of the players, so that the roles will be given randomly to the players.
     */
    private static void randomizePlayers() {
        ArrayList<Player> temp = new ArrayList<>();
        Random random = new Random();
        while (players.size() != 0)
            temp.add(players.remove(random.nextInt(players.size())));
        players = temp;
    }
}
