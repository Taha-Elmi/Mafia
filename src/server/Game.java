package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This class is the main class of the server, which will handle the logic of the game and move it on.
 * It uses the SingleTon pattern, so that every client has access to one instance of the server and its data.
 *
 * @author Taha Elmi
 * @version 1
 */
public class Game {
    private static final Game instance = new Game(4);
    private int numberOfPlayers;
    private ArrayList<ClientHandler> clientHandlers;
    private ArrayList<Player> players;
    private ArrayList<Player> mafias;
    private String state;
    private int day;
    private boolean hasMayor;

    private Game(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        players = new ArrayList<>();
        clientHandlers = new ArrayList<>();
        mafias = new ArrayList<>();
        state = "beginning";
        day = 0;
        hasMayor = false;
    }

    public void run() {
        waitForClients(2000);
        while (!checkIfReady());
        randomizePlayers();
        giveRoles();
        introduce();
        while (true){
            try {
                dawn();
                Thread.sleep(30 * 1000);
                declareCandidates();
                Thread.sleep(30 * 1000);
                processVotes();
                if (checkIfFinished())
                    break;
                dusk();
                Thread.sleep(30 * 1000);
                if (checkIfFinished())
                    break;
            } catch (InterruptedException e) {
                System.err.println("An error occurred.");
            }
        }
        finish();
    }

    /**
     * It will give the single instance of the class Game, due to the use of SingleTon pattern
     * @return the instance
     */
    public static Game getInstance() {
        return instance;
    }

    /**
     * getter of the state field
     * @return the state field
     */
    public String getState() {
        return state;
    }

    /**
     * getter of the clientHandlers list
     * @return the clientHandlers list
     */
    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    /**
     * getter of the mafias field
     * @return the mafias field
     */
    public ArrayList<Player> getMafias() {
        return mafias;
    }

    /**
     * setter of the state field
     * @param state the new state
     */
    public void setState(String state) {
        this.state = state;
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
                hasMayor = true;
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
            clientHandler.write(ConsoleColors.ANSI_RED + "\nYou're " + clientHandler.getPlayer().getRole() + "!\n" + ConsoleColors.ANSI_RESET);

            if (clientHandler.getPlayer().getRole() instanceof Role.Mafia) {
                clientHandler.write(ConsoleColors.ANSI_PURPLE + "The Mafia Team:");
                for (Player player : mafias) {
                    clientHandler.write("- " + player.getUsername() + " as " + player.getRole().toString());
                }
                clientHandler.write(ConsoleColors.ANSI_RESET);
            }

            if (clientHandler.getPlayer().getRole() instanceof Role.Mayor) {
                for (Player player : players) {
                    if (player.getRole() instanceof Role.Doctor) {
                        clientHandler.write(ConsoleColors.ANSI_PURPLE + "The doctor is: " + player.getUsername() + ConsoleColors.ANSI_RESET);
                        break;
                    }
                }
            }

        }
    }

    private void explainLastNight() {
        ArrayList<Player> deadPlayers = new ArrayList<>();

        processMafiaAction(deadPlayers);
        processProfessionalAction(deadPlayers);
    }

    private void processMafiaAction(ArrayList<Player> deadPlayers) {
        Player mainMafia = findRole(new Role.GodFather());
        if (players == null)
            mainMafia = findRole(new Role.DrLecter());
        if (mainMafia == null)
            mainMafia = findRole(new Role.SimpleMafia());

        //checks if the mafia have done their job or not
        if (mainMafia.getRole().getTarget() == 0)
            return;

        Player doctor = findRole(new Role.Doctor());

        if (doctor == null || doctor.getRole().getTarget() != mainMafia.getRole().getTarget()) {
            Player victim = findAlivePlayerByIndex(mainMafia.getRole().getTarget());
            kill(victim);
            deadPlayers.add(victim);
        }
    }

    private void processProfessionalAction(ArrayList<Player> deadPlayers) {
        Player professional = findRole(new Role.Professional());
        Player drLecter = findRole(new Role.DrLecter());

        //check if we have professional and if he has done his job
        if (professional == null || professional.getRole().getTarget() == 0)
            return;

        Player victim = findAlivePlayerByIndex(professional.getRole().getTarget());
        if (victim.getRole() instanceof Role.Mafia) {
            if (drLecter == null || drLecter.getRole().getTarget() == 0 || findAliveMafiaByIndex(drLecter.getRole().getTarget()) != victim) {
                kill(victim);
                deadPlayers.add(victim);
            }
        } else {
            kill(professional);
            deadPlayers.add(professional);
        }

    }

    private void processPsychiatristAction() {

    }

    /**
     * It will change the game state to "day" and do necessary things that have to be done in the beginning of a day.
     */
    private void dawn() {
        day++;
        chat(ConsoleColors.ANSI_YELLOW + "\n===============\nDay" + day + "\n===============" + ConsoleColors.ANSI_RESET);
        chat(ConsoleColors.ANSI_BLUE + "GOD: Wake Up. You can discuss now for 5 minute..." + ConsoleColors.ANSI_RESET);
        setState("day");
    }

    /**
     * It will change the game state to "night" and do necessary things that have to be done at night.
     */
    private void dusk() {
        setState("night");
        chat(ConsoleColors.ANSI_BLUE + "GOD: It's night, time to sleep..." + ConsoleColors.ANSI_RESET);
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getPlayer().isAlive())
                clientHandler.getPlayer().getRole().nightAct(clientHandler);
        }
    }

    /**
     * implements chatting. Sending texts to every player.
     * @param text the message
     */
    protected void chat(String text) {
        for (ClientHandler clientHandler : clientHandlers)
            clientHandler.write(text);
    }

    /**
     * it will count the number of alive mafias
     * @return number of alive mafias
     */
    public int countMafias() {
        int answer = 0;
        for (Player player : players) {
            if (player.getRole() instanceof Role.Mafia && player.isAlive())
                answer++;
        }
        return answer;
    }

    /**
     * it will count the number of alive citizens
     * @return number of alive citizens
     */
    public int countCitizens() {
        int answer = 0;
        for (Player player : players) {
            if (!(player.getRole() instanceof Role.Mafia) && player.isAlive())
                answer++;
        }
        return answer;
    }

    /**
     * It count alive players.
     * @return number of alive players
     */
    protected int countAlivePlayers() {
        return countMafias() + countCitizens();
    }

    /**
     * It declare the candidates of the voting of the end of the day
     */
    public void declareCandidates() {
        chat(ConsoleColors.ANSI_BLUE + "GOD: Now vote for your suspect." + ConsoleColors.ANSI_RESET);
        chat("Choose the index of the person you want to vote for their removal.");
        int index = 1;
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getPlayer().isAlive()) {
                chat(index + "- " + ConsoleColors.ANSI_GREEN + clientHandler.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                index++;
            }
        }
        setState("voting");
    }

    /**
     * It gets votes from everybody, and process the votes
     */
    private Player getVotes() {
        ArrayList<Player> alivePlayers = new ArrayList<>();
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getPlayer().isAlive())
                alivePlayers.add(clientHandler.getPlayer());
        }

        HashMap<Integer, Integer> votes = new HashMap<>();
        for (int i = 1; i <= countAlivePlayers(); i++)
            votes.put(i, 0);

        for (Player player : alivePlayers) {
            if (player.getVote() == 0) {
                player.setInactivity(player.getInactivity() + 1);
                continue;
            }
            player.setInactivity(0);
            votes.put(player.getVote(), votes.get(player.getVote()) + 1);
        }

        int maxIndex = 1;
        boolean isUnique = true;
        for (Integer integer : votes.keySet()) {
            if (votes.get(integer) > votes.get(maxIndex)) {
                maxIndex = integer;
                isUnique = true;
            } else if (votes.get(integer).equals(votes.get(maxIndex))) {
                isUnique = false;
            }
        }

        //declaring the results to everyone
        chat(ConsoleColors.ANSI_CYAN + "Voting Results: " + ConsoleColors.ANSI_RESET);
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getPlayer().isAlive()) {
                String target = (clientHandler.getPlayer().getVote() == 0 ? "-No one-" :
                        alivePlayers.get(clientHandler.getPlayer().getVote() - 1).getUsername());
                chat(ConsoleColors.ANSI_CYAN + clientHandler.getPlayer().getUsername() + " has voted to: " + ConsoleColors.ANSI_RESET + target);
            }
        }

        return (isUnique ? alivePlayers.get(maxIndex - 1) : null);
    }

    /**
     * It will check if there is any inactive player.
     */
    public void checkInactivity() {
        for (Player player : players) {
            if (player.getInactivity() >= 3) {
                kill(player);
                chat(ConsoleColors.ANSI_RED + player.getUsername() + " has been kicked due to inactivity." + ConsoleColors.ANSI_RESET);
            }
        }
    }

    /**
     * It resets the vote field of every player
     */
    private void resetVotes() {
        for (Player player : players)
            player.setVote(0);
    }

    private boolean askMayor() {
        return true;
    }

    /**
     * It kills a player.
     * @param victim the player which will be killed
     */
    private void kill(Player victim) {
        victim.setAlive(false);
    }

    /**
     * It will do all the things which is necessary after the voting phrase.
     * Getting the votes,
     * asking the mayor,
     * declaring the victim
     * and checking the inactivity of players during the votings.
     */
    private void processVotes() {
        Player target = getVotes();
        if (target == null) {
            chat("No one exits the game.");
        } else {
            if (hasMayor) {
                chat("waiting for mayor's decision...");
                boolean cancel = askMayor();
                if (!cancel) {
                    kill(target);
                    chat(ConsoleColors.ANSI_RED + "Mayor accepted. " + target.getUsername() + " has been kicked due to the voting result." + ConsoleColors.ANSI_RESET);
                } else
                    chat(ConsoleColors.ANSI_RED + "The mayor has cancelled the voting." + ConsoleColors.ANSI_RESET);
            } else {
                kill(target);
                chat(ConsoleColors.ANSI_RED + target.getUsername() + " has been kicked due to the voting result." + ConsoleColors.ANSI_RESET);
            }
        }
        checkInactivity();
        resetVotes();
    }

    /**
     * This method will give the player with a specific role given in the parameter
     * @param wanted the role of the player we want
     * @return the player with the role
     */
    public Player findRole(Role wanted) {
        for (Player player : players) {
            if (player.getRole().toString().equals(wanted.toString()))
                return player;
        }
        return null;
    }

    /**
     * It will search for the appropriate player, according to its index in the clientHandlers list.
     * It's useful when the players are doing their night act by choosing the index of the players.
     * @param index the index of the player the client wants
     * @return the player which was wanted
     */
    public Player findAlivePlayerByIndex(int index) {
        if (index < 1 || index > countAlivePlayers())
            throw new IllegalArgumentException();

        int iterator = 1;
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getPlayer().isAlive()) {
                if (iterator == index)
                    return clientHandler.getPlayer();
                index++;
            }
        }

        return null;
    }

    /**
     * It will search for the appropriate mafia, according to its index.
     * It's useful when we want to find out which mafia has Dr.Lecter revived.
     * @param index the index of the mafia the client wants
     * @return the mafia which was wanted
     */
    public Player findAliveMafiaByIndex(int index) {
        if (index < 1 || index > countMafias())
            throw new IllegalArgumentException();

        int iterator = 1;
        for (Player player : getMafias()) {
            if (player.isAlive()) {
                if (iterator == index)
                    return player;
                index++;
            }
        }

        return null;
    }

    /**
     * It checks if the game is finished or not
     * @return true if finished, and false otherwise
     */
    private boolean checkIfFinished() {
        int numberOfMafias = countMafias();
        int numberOfCitizens = countCitizens();

        return numberOfMafias == numberOfCitizens || numberOfMafias == 0;
    }

    /**
     * It will declare the winners of the game, the roles, and finish the game.
     */
    private void finish() {
        chat(ConsoleColors.ANSI_BLUE + "\n\nGOD: The game is finished !!!");
        if (countMafias() == 0)
            chat(ConsoleColors.ANSI_RED + "The Citizens" + ConsoleColors.ANSI_BLUE + " won the game. Congrats citizens :)))))\n");
        else
            chat(ConsoleColors.ANSI_RED + "The Mafias" + ConsoleColors.ANSI_BLUE + " won the game. Congrats mafias :)))))\n");

        chat(ConsoleColors.ANSI_PURPLE + "The Mafia Team:");
        for (Player mafia : mafias)
            chat("- " + mafia.getUsername() + " as " + mafia.getRole().toString());

        chat(ConsoleColors.ANSI_CYAN + "\nThe Citizen Team:");
        for (Player player : players) {
            if (!(player.getRole() instanceof Role.Mafia))
                chat("- " + player.getUsername() + " as " + player.getRole().toString());
        }

        chat(ConsoleColors.ANSI_BLUE + "\n\nGOD: It was a nice game =))\nHave fun :))))" + ConsoleColors.ANSI_RESET);
    }

}
