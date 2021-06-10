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
public class Game {
    private static final Game instance = new Game(6);
    private int numberOfPlayers;
    private ArrayList<ClientHandler> clientHandlers;
    private ArrayList<Player> players;
    private ArrayList<Player> mafias;
    private String state;

    private Game(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        players = new ArrayList<>();
        clientHandlers = new ArrayList<>();
        mafias = new ArrayList<>();
        state = "beginning";
    }

    public void run() {
        waitForClients(2000);
        while (!checkIfReady());
        randomizePlayers();
        giveRoles();
        introduce();
    }

    /**
     * It will give the single instance of the class Game, due to the use of SingleTon pattern
     * @return the instance
     */
    public static Game getInstance() {
        return instance;
    }

    public String getState() {
        return state;
    }

    /**
     * This method will create connections with the clients
     */
    private void waitForClients(int port) {
        ServerSocket welcomingSocket;
        try {
            welcomingSocket = new ServerSocket(port);
            System.out.println("The server is on the port " + port);
        } catch (IOException e) {
            System.err.println("The port " + port + " was not free. Trying the port " + (port + 1) + "...");
            waitForClients(port + 1);
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
            clientHandlers.add(clientHandler);
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
    private boolean checkIfReady() {
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
    private void randomizePlayers() {
        ArrayList<Player> temp = new ArrayList<>();
        Random random = new Random();
        while (players.size() != 0)
            temp.add(players.remove(random.nextInt(players.size())));
        players = temp;
    }

    /**
     * It will give appropriate roles to every player, according to the number of players.
     */
    private void giveRoles() {
        int numberOfMafia = numberOfPlayers / 3;
        int numberOfCitizens = numberOfPlayers - numberOfMafia;

        for (Player player : players) {
            if (numberOfMafia == 1) {
                player.setRole(new Role.GodFather());
                mafias.add(player);
                numberOfMafia--;
            } else if (numberOfMafia == 2) {
                player.setRole(new Role.SimpleMafia());
                mafias.add(player);
                numberOfMafia--;
            } else if (numberOfMafia == 3) {
                player.setRole(new Role.DrLecter());
                mafias.add(player);
                numberOfMafia--;
            } else if (numberOfMafia > 3) {
                player.setRole(new Role.SimpleMafia());
                mafias.add(player);
                numberOfMafia--;
            } else if (numberOfCitizens == 1) {
                player.setRole(new Role.SimpleCitizen());
                numberOfCitizens--;
            } else if (numberOfCitizens == 2) {
                player.setRole(new Role.Doctor());
                numberOfCitizens--;
            } else if (numberOfCitizens == 3) {
                player.setRole(new Role.Detective());
                numberOfCitizens--;
            } else if (numberOfCitizens == 4) {
                player.setRole(new Role.Psychiatrist());
                numberOfCitizens--;
            } else if (numberOfCitizens == 5) {
                player.setRole(new Role.Professional());
                numberOfCitizens--;
            } else if (numberOfCitizens == 6) {
                player.setRole(new Role.DieHard());
                numberOfCitizens--;
            } else if (numberOfCitizens == 7) {
                player.setRole(new Role.Mayor());
                numberOfCitizens--;
            } else {
                player.setRole(new Role.SimpleCitizen());
                numberOfCitizens--;
            }

        }
    }

    /**
     * It will tell everybody their roles.
     */
    private void introduce() {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.write("\nYou're " + clientHandler.getPlayer().getRole().toString() + "!\n");

            if (clientHandler.getPlayer().getRole() instanceof Role.Mafia) {
                clientHandler.write("The Mafia Team:");
                for (Player player : mafias) {
                    clientHandler.write("- " + player.getUsername() + " as " + player.getRole().toString());
                }
            }

            if (clientHandler.getPlayer().getRole() instanceof Role.Mayor) {
                for (Player player : players) {
                    if (player.getRole() instanceof Role.Doctor) {
                        clientHandler.write("The doctor is: " + player.getUsername());
                        break;
                    }
                }
            }

        }
    }
}
