package server;

public abstract class Role {
    private int target;
    private boolean canSetTarget;

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public boolean canSetTarget() {
        return canSetTarget;
    }

    public void setCanSetTarget(boolean canSetTarget) {
        this.canSetTarget = canSetTarget;
    }

    public interface Mafia {}

    public abstract void nightAct(ClientHandler clientHandler);

    public static class GodFather extends Role implements Mafia{

        public GodFather() {
            setTarget(0);
            setCanSetTarget(true);
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
    public static class SimpleMafia extends Role implements Mafia{

        public SimpleMafia() {
            setTarget(0);
            setCanSetTarget(true);
        }

        @Override
        public String toString() {
            return "a Simple Mafia";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            clientHandler.write("You can recommend a player to the godfather by their index: ");
            int index = 1;
            for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                if (ch.getPlayer().isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }
        }
    }
    public static class DrLecter extends Role implements Mafia{
        private int survivor;
        private int numberOfRevivingHimself;

        public DrLecter() {
            setTarget(0);
            setCanSetTarget(true);
            survivor = 0;
            numberOfRevivingHimself = 1;
        }

        public void setSurvivor(int survivor) {
            this.survivor = survivor;
        }

        public void setNumberOfRevivingHimself(int numberOfRevivingHimself) {
            this.numberOfRevivingHimself = numberOfRevivingHimself;
        }

        public int getNumberOfRevivingHimself() {
            return numberOfRevivingHimself;
        }

        @Override
        public String toString() {
            return "Dr.Lecter";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            clientHandler.write("Save a mafia by choosing the appropriate index.");
            int index = 1;
            for (Player player : Game.getInstance().getMafias()) {
                if (player.isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + player.getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }

        }

    }

    public static class Doctor extends Role {
        private int numberOfRevivingHimself;

        public Doctor() {
            setTarget(0);
            setCanSetTarget(true);
            numberOfRevivingHimself = 1;
        }

        @Override
        public String toString() {
            return "Doctor";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
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
    public static class Detective extends Role {
        int numberOfDetects;

        public Detective() {
            setTarget(0);
            setCanSetTarget(true);
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
    public static class Professional extends Role {

        public Professional() {
            setTarget(0);
            setCanSetTarget(true);
        }

        @Override
        public String toString() {
            return "Professional";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {
            clientHandler.write("you can detect a player by choosing the appropriate index:");
            int index = 1;
            for (ClientHandler ch : Game.getInstance().getClientHandlers()) {
                if (ch.getPlayer().isAlive()) {
                    clientHandler.write(index + "- " + ConsoleColors.ANSI_GREEN + ch.getPlayer().getUsername() + ConsoleColors.ANSI_RESET);
                    index++;
                }
            }
        }
    }
    public static class SimpleCitizen extends Role {

        public SimpleCitizen() {
            setCanSetTarget(false);
        }

        @Override
        public String toString() {
            return "a Simple Citizen";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {}
    }
    public static class Mayor extends Role {

        public Mayor() {
            setCanSetTarget(false);
        }

        @Override
        public String toString() {
            return "Mayor";
        }

        @Override
        public void nightAct(ClientHandler clientHandler) {}
    }
    public static class Psychiatrist extends Role {

        public Psychiatrist() {
            setTarget(0);
            setCanSetTarget(true);
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
    public static class DieHard extends Role {
        private boolean wantInquiry;
        private int inquiry;

        public DieHard() {
            setCanSetTarget(false);
            wantInquiry = false;
            inquiry = 2;
        }

        public void setWantInquiry(boolean wantInquiry) {
            this.wantInquiry = wantInquiry;
        }

        public boolean WantInquiry() {
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
            clientHandler.write("Do you want an inquiry? 1- yes / 2- no");
        }
    }
}
