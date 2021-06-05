package server;

import java.util.ArrayList;

public class GameServer {
    private static GameServer instance = null;
    private ArrayList<Player> players;

    private GameServer() {
        players = new ArrayList<>();
    }

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
