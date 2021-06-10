package server;

public class Server {
    public static void main(String[] args) {
        Game game = Game.getInstance();
        game.run();
    }
}
