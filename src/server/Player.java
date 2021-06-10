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
    private boolean isReady;
    private int hearts;
    private Role role;
    private int vote;
    private int inactivity;

    public Player(String name) {
        isActive = isAlive = true;
        isSilent = isReady = false;
        hearts = 1;
        username = name;
        vote = 0;
        inactivity = 0;
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

    /**
     * setter of the isReady field
     * @param ready the new boolean
     */
    public void setReady(boolean ready) {
        isReady = ready;
    }

    /**
     * getter of the isReady field
     * @return the value of the isReady field
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * getter of the isSilent field
     * @return the value of the isSilent field
     */
    public boolean isSilent() {
        return isSilent;
    }

    /**
     * getter of the isAlive field
     * @return the value of the isAlive field
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * setter of the isAlive field
     * @param alive the new value of the field
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * setter of the vote field
     * @param vote the new vote
     */
    public void setVote(int vote) {
        this.vote = vote;
    }

    /**
     * getter of the vote field
     * @return the vote field
     */
    public int getVote() {
        return vote;
    }

    /**
     * setter of the inactivity field
     * @param inactivity inactivity field
     */
    public void setInactivity(int inactivity) {
        this.inactivity = inactivity;
    }

    /**
     * getter of the inactivity field
     * @return the inactivity field
     */
    public int getInactivity() {
        return inactivity;
    }

    /**
     * getter of the role field
     * @return the role field
     */
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
