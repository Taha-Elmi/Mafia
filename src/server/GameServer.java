package server;

public class GameServer {
    private static GameServer instance = null;

    public static GameServer getInstance() {
        if (instance == null)
            instance = new GameServer();
        return instance;
    }
}
