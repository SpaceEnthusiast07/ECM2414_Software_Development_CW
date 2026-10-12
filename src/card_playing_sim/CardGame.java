package card_playing_sim;

// Used to receive the number of players from the user
import java.util.Scanner;
import java.util.InputMismatchException;
// Used to read the pack file
import java.io.File;
import java.io.FileNotFoundException;

class CardGame {
    // Represents the number of players, equivalent of n in documentation
    private static int numOfPlayers;
    // Holds the path to the pack file
    private static String packFilePath;
    private static File packFile;
    // Holds the pack of cards read from file
    private static Pack pack;

    public static void main(String[] args) {
        System.out.println("=== ECM2414 Software Development Coursework ===\nCard Playing Sim");

        // Used to input numOfPlayers and packFilePath
        Scanner sc = new Scanner(System.in);

        // Input number of players
        try {
            // Ask user for number of players
            System.out.print("Please enter the number of players: ");
            // Read the user's input
            numOfPlayers = sc.nextInt();
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("ERROR: Number of players must be an Integer.");
            System.exit(1);
        }

        while(pack == null) {
            // Input pack file name
            try {
                // Ask user for the path to the pack file
                System.out.print("\nPlease enter location of pack to load: ");
                // Read the user's input
                packFilePath = sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Invalid location of pack file.");
                System.exit(1);
            }

            // Read and validate the provided pack file
            pack = readPackFile(packFilePath);
        }

        // Close the input stream
        sc.close();

        System.out.println(numOfPlayers);
        System.out.println(packFilePath);
    }

    /**
     * Reads the data from the provided pack file and validates it based on whether
     * there are 8n values, each value is a positive integer, and if there are 4 values for each
     * corresponding player.
     * @param packFilePath The file path to the pack file to read and validate.
     * @return Parsed pack file as a Pack object.
     */
    static Pack readPackFile(String packFilePath) {
        // Create a new File object
        File packFile = new File(packFilePath);
        // Declare variable to hold card value
        int cardValue;
        // Tracks number of cards for each player
        int[] playerCardCountTracker = new int[numOfPlayers];
        // Keeps track of the number of cards in the pack file
        int numOfCards = 0;
        // Initialise a new pack of the required length
        Pack pack = new Pack(numOfPlayers);

        try (Scanner sc = new Scanner(packFile)) {
            // Loop through each line in the pack file
            while (sc.hasNextLine()) {
                cardValue = sc.nextInt();
                // Ensure card value is positive
                if (cardValue < 1) {
                    System.out.println("ERROR: Pack file must only contain positive card values.");
                    return null;
                }
                // If within range of number of players, update card tracker
                if (cardValue <= numOfPlayers) {
                    playerCardCountTracker[cardValue-1]++;
                }
                // Add this card to the pack
                pack.addCard(new Card(cardValue));
                // Increase number of cards tracker
                numOfCards++;
            }

            // Ensure there are 8n cards
            if (numOfCards != 8*numOfPlayers) {
                System.out.println("ERROR: Pack file must contain exactly 8n cards.");
                return null;
            }

            // Ensure there are at least 4 cards of each player's index
            for (int i = 0; i < playerCardCountTracker.length; i++) {
                if (playerCardCountTracker[i] < 4) {
                    System.out.println("ERROR: Pack file must contain at least 4 cards for each player.");
                    return null;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Pack file does not exist.");
            return null;
        } catch (InputMismatchException e) {
            System.out.println("ERROR: At least 1 card value in the provided pack is not an Integer.");
            return null;
        }

        // Now that the pack file is valid, return it
        return pack;
    }
}
