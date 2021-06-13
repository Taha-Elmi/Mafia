package server;

public abstract class Role {
    private int target;
    private boolean canSetTarget;
    private boolean doneJob;

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

    public void setDoneJob(boolean doneJob) {
        this.doneJob = doneJob;
    }

    public boolean isDoneJob() {
        return doneJob;
    }

    public interface Mafia {}

    public abstract void nightAct(ClientHandler clientHandler);

    public static class GodFather extends Role implements Mafia{

        public GodFather() {
            setTarget(0);
            setCanSetTarget(true);
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
    public static class SimpleMafia extends Role implements Mafia{

        public SimpleMafia() {
            setTarget(0);
            setCanSetTarget(true);
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
    public static class DrLecter extends Role implements Mafia{
        private int survivor;
        private int numberOfRevivingHimself;

        public DrLecter() {
            setTarget(0);
            setCanSetTarget(true);
            setDoneJob(false);
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
            if (Game.getInstance().getState().equals("night-lecter")) {
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
                    clientHandler.write("You can recommend a player to the godfather by choosing the appropriate index: ");
                else
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

    }

    public static class Doctor extends Role {
        private int numberOfRevivingHimself;

        public Doctor() {
            setTarget(0);
            setCanSetTarget(true);
            setDoneJob(false);
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
    public static class Professional extends Role {

        public Professional() {
            setTarget(0);
            setCanSetTarget(true);
            setDoneJob(false);
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
            setDoneJob(false);
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
            setDoneJob(false);
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
    public static class DieHard extends Role {
        private boolean wantInquiry;
        private int inquiry;

        public DieHard() {
            setCanSetTarget(false);
            setDoneJob(false);
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
            if (inquiry < 1) {
                clientHandler.write("You're out of limit. Have a good night :)");
                return;
            }
            clientHandler.write("Do you want an inquiry? 1- yes / 2- no");
        }
    }
}
