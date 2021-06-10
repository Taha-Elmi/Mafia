package server;

public class Role {
    public interface Mafia {

    }
    public static class GodFather extends Role implements Mafia{
        @Override
        public String toString() {
            return "God Father";
        }
    }
    public static class SimpleMafia extends Role implements Mafia{
        @Override
        public String toString() {
            return "a Simple Mafia";
        }
    }
    public static class DrLecter extends Role implements Mafia{
        @Override
        public String toString() {
            return "Dr.Lecter";
        }
    }

    public static class Doctor extends Role {
        @Override
        public String toString() {
            return "Doctor";
        }
    }
    public static class Detective extends Role {
        @Override
        public String toString() {
            return "Detective";
        }
    }
    public static class Professional extends Role {
        @Override
        public String toString() {
            return "Professional";
        }
    }
    public static class SimpleCitizen extends Role {
        @Override
        public String toString() {
            return "a Simple Citizen";
        }
    }
    public static class Mayor extends Role {
        @Override
        public String toString() {
            return "Mayor";
        }
    }
    public static class Psychiatrist extends Role {
        @Override
        public String toString() {
            return "Psychiatrist";
        }
    }
    public static class DieHard extends Role {
        @Override
        public String toString() {
            return "Die Hard";
        }
    }
}
