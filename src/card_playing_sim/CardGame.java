package card_playing_sim;

// Used to receive the number of players from the user
import java.util.Scanner;

class CardGame {
    // Represents the number of players, equivalent of n in documentation
    private static int numOfPlayers;

    public static void main(String[] args) {
        System.out.println("=== ECM2414 Software Development Coursework ===\nCard Playing Sim.");

        Scanner sc = new Scanner(System.in);

        // Determine the number of players
        numOfPlayers = sc.nextInt();

        System.out.println(numOfPlayers);
    }
}