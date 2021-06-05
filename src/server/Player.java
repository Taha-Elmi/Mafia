package server;

/**
 * This class represents a player in the server.
 *
 * @author Taha Elmi
 * @version 1
 */
public class Player {
    private String username;
    private boolean isActive;
    private boolean isAlive;
    private boolean isSilent;
    private int hearts;

    public Player(String name) {
        isActive = isAlive = true;
        isSilent = false;
        hearts = 1;
        username = name;
    }

    /**
     * getter of the username field
     * @return the username field
     */
    public String getUsername() {
        return username;
    }

    /**
     * setter of the username field
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
