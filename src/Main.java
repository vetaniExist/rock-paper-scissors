public class Main {
    public static void main(String[] args) {
        try {
            RPS game = new RPS(args);
            game.playGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
