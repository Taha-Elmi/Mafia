package server;

import java.util.ArrayList;

/**
 * This class is the main class of the server, which will handle the logic of the game and move it on.
 * It uses the SingleTon pattern, so that every client has access to one instance of the server and its data.
 *
 * @author Taha Elmi
 * @version 1
 */
public class GameServer {
    private static GameServer instance = null;
    private ArrayList<Player> players;

    private GameServer() {
        players = new ArrayList<>();
    }

    /**
     * It will give the single instance of the class GameServer, due to the use of SingleTon pattern
     * @return the instance
     */
    public static GameServer getInstance() {
        if (instance == null)
            instance = new GameServer();
        return instance;
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
}
