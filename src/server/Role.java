package server;

/**
 * This abstract class is extended to different roles of the game.
 *
 * @author Taha Elmi
 */
public abstract class Role {
    private Player target;
    private boolean doneJob;

    /**
     * getter of the target field.
     * @return the target field
     */
    public Player getTarget() {
        return target;
    }

    /**
     * setter of the target field.
     * @param target the new value of the target field.
     */
    public void setTarget(Player target) {
        this.target = target;
    }

    /**
     * setter of the doneJob field
     * @param doneJob the new value of the doneJob field
     */
    public void setDoneJob(boolean doneJob) {
        this.doneJob = doneJob;
    }

    /**
     * getter of the doneJob field
     * @return the value of the doneJob field
     */
    public boolean isDoneJob() {
        return doneJob;
    }

    /**
     * This is a helping interface so that we can determine that a role is a part of mafia or not.
     *
     * @author Taha Elmi
     */
    public interface Mafia {}

    /**
     * a method which will be executed every night and print needed things on players screens.
     * @param clientHandler the clientHandler
     */
    public abstract void nightAct(ClientHandler clientHandler);

    /**
     * implements the Godfather role
     *
     * @author Taha Elmi
     */
    public static class GodFather extends Role implements Mafia{

        public GodFather() {
            setDoneJob(false);
        }

        @Override
        public String toString() {
            return "Godfather";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            clientHandler.write("You can see other mafia's recommendations and kill a player by choosing the appropriate index.");
            int index = 1;
            for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                if (ch.getPlayer().isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }
        }
    }

    /**
     * implements the Simple Mafia role
     *
     * @author Taha Elmi
     */
    public static class SimpleMafia extends Role implements Mafia{

        public SimpleMafia() {
            setDoneJob(false);
        }

        @Override
        public String toString() {
            return "a Simple Mafia";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            if (Game.getInstance().findRole(new GodFather()) != null)
                clientHandler.write("You can recommend a player to the godfather by choosing the appropriate index: ");
            else if (Game.getInstance().findRole(new DrLecter()) != null)
                clientHandler.write("You can recommend a player to Dr.Lecter by choosing the appropriate index: ");
            else if (Game.getInstance().countMafias() == 1)
                clientHandler.write("You can kill a player by choosing the appropriate index: ");
            else if (Game.getInstance().findRole(new SimpleMafia()) == clientHandler.getPlayer())
                clientHandler.write("You can see other mafia's recommendations and kill a player by choosing the appropriate index.");
            else
                clientHandler.write("You can recommend a player to " + Game.getInstance().findRole(new SimpleMafia()).getUsername() +" by choosing the appropriate index: ");


            int index = 1;
            for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                if (ch.getPlayer().isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }
        }
    }

    /**
     * implements Dr.Lecter role
     *
     * @author Taha Elmi
     */
    public static class DrLecter extends Role implements Mafia{
        private Player survivor;
        private int numberOfRevivingHimself;
        private boolean doneReviving;

        public DrLecter() {
            setDoneJob(false);
            setDoneReviving(false);
            numberOfRevivingHimself = 1;
        }

        public void setSurvivor(Player survivor) {
            this.survivor = survivor;
        }

        public Player getSurvivor() {
            return survivor;
        }

        public void setNumberOfRevivingHimself(int numberOfRevivingHimself) {
            this.numberOfRevivingHimself = numberOfRevivingHimself;
        }

        public int getNumberOfRevivingHimself() {
            return numberOfRevivingHimself;
        }

        public boolean isDoneReviving() {
            return doneReviving;
        }

        public void setDoneReviving(boolean doneReviving) {
            this.doneReviving = doneReviving;
        }

        @Override
        public String toString() {
            return "Dr.Lecter";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            if (!doneReviving) {
                //check if there is any player that he can save or not
                if (Game.getInstance().countMafias() == 1 && numberOfRevivingHimself == 0) {
                    clientHandler.write("Seems that you can't revive anyone :)\nSo do your mafia stuff.");
                    setDoneReviving(true);
                    nightAct(clientHandler);
                    return;
                }

                clientHandler.write("Save a mafia by choosing the appropriate index.");
                int index = 1;
                for (Player player : Game.getInstance().getMafias()) {
                    if (player.isAlive()) {
                        clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + player.getUsername() + ConsoleColors.ANSI_RESET);
                        index++;
                    }
                }
            } else {
                if (Game.getInstance().findRole(new GodFather()) != null)
                    clientHandler.write("Now you can recommend a player to the godfather by choosing the appropriate index: ");
                else
                    clientHandler.write("Now you can see other mafia's recommendations and kill a player by choosing the appropriate index.");

                int index = 1;
                for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                    if (ch.getPlayer().isAlive()) {
                        clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                        index++;
                    }
                }
            }
        }

    }


    /**
     * implements the Doctor role
     *
     * @author Taha Elmi
     */
    public static class Doctor extends Role {
        private int numberOfRevivingHimself;

        public Doctor() {
            setDoneJob(false);
            numberOfRevivingHimself = 1;
        }

        public int getNumberOfRevivingHimself() {
            return numberOfRevivingHimself;
        }

        public void setNumberOfRevivingHimself(int numberOfRevivingHimself) {
            this.numberOfRevivingHimself = numberOfRevivingHimself;
        }

        @Override
        public String toString() {
            return "Doctor";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            //check if there is any player that he can save or not
            if (Game.getInstance().countCitizens() == 1 && numberOfRevivingHimself == 0) {
                clientHandler.write("Seems that you can't revive anyone :) Try to sleep.");
                return;
            }

            clientHandler.write("Save a person by choosing the appropriate index:");
            int index = 1;
            for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                if (ch.getPlayer().isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }
        }
    }

    /**
     * implements the Detective role
     *
     * @author Taha Elmi
     */
    public static class Detective extends Role {
        int numberOfDetects;

        public Detective() {
            setDoneJob(false);
            numberOfDetects = 1;
        }

        public int getNumberOfDetects() {
            return numberOfDetects;
        }

        public void setNumberOfDetects(int numberOfDetects) {
            this.numberOfDetects = numberOfDetects;
        }

        @Override
        public String toString() {
            return "Detective";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            clientHandler.write("You can detect a player by choosing appropriate index:");
            int index = 1;
            for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                if (ch.getPlayer().isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }
        }
    }

    /**
     * implements the Professional role
     *
     * @author Taha Elmi
     */
    public static class Professional extends Role {

        public Professional() {
            setDoneJob(false);
        }

        @Override
        public String toString() {
            return "Professional";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            clientHandler.write("you can snipe a player by choosing the appropriate index, or refuse shooting with '0':");
            int index = 1;
            for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                if (ch.getPlayer().isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }
        }
    }

    /**
     * implements the Simple Citizen role
     *
     * @author Taha Elmi
     */
    public static class SimpleCitizen extends Role {

        public SimpleCitizen() {
            setDoneJob(false);
        }

        @Override
        public String toString() {
            return "a Simple Citizen";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {}
    }

    /**
     * implements the Mayor role
     *
     * @author Taha Elmi
     */
    public static class Mayor extends Role {
        private boolean cancel;

        public Mayor() {
            setDoneJob(false);
            cancel = false;
        }

        public boolean isCancel() {
            return cancel;
        }

        public void setCancel(boolean cancel) {
            this.cancel = cancel;
        }

        @Override
        public String toString() {
            return "Mayor";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {}
    }

    /**
     * implements the Psychiatrist role
     *
     * @author Taha Elmi
     */
    public static class Psychiatrist extends Role {

        public Psychiatrist() {
            setDoneJob(false);
        }

        @Override
        public String toString() {
            return "Psychiatrist";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            clientHandler.write("You can make a player silent by choosing the appropriate index:");
            int index = 1;
            for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                if (ch.getPlayer().isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }
        }
    }

    /**
     * implements the DieHard role
     *
     * @author Taha Elmi
     */
    public static class DieHard extends Role {
        private boolean wantInquiry;
        private int inquiry;
        private int hearts;

        public DieHard() {
            setDoneJob(false);
            wantInquiry = false;
            inquiry = 2;
            hearts = 2;
        }

        public int getHearts() {
            return hearts;
        }

        public void setHearts(int hearts) {
            this.hearts = hearts;
        }

        public void setWantInquiry(boolean wantInquiry) {
            this.wantInquiry = wantInquiry;
        }

        public boolean wantInquiry() {
            return wantInquiry;
        }

        public int getInquiry() {
            return inquiry;
        }

        public void setInquiry(int inquiry) {
            this.inquiry = inquiry;
        }

        @Override
        public String toString() {
            return "Die Hard";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            if (inquiry < 1) {
                clientHandler.write("You're out of limit. Have a good night :)");
                return;
            }
            clientHandler.write("Do you want an inquiry? 1- yes / 0- no");
        }
    }
}
