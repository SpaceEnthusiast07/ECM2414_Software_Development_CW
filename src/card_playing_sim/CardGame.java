package card_playing_sim;

// Used for numOfPlayers, packFilePath, and pack file input
import java.util.Scanner;
import java.util.InputMismatchException;

// Used to read the pack file
import java.io.File;
import java.io.FileNotFoundException;

class CardGame {
    // Represents the number of players, equivalent of n in documentation
    private int numOfPlayers;

    // Holds the pack of cards read from file
    private Pack pack;

    // Holds all the players (threads) of the game
    private Player[] players;

    // Holds all the decks in the game
    private Deck[] decks;

    // Flag to keep track of whether a player has won
    private boolean playerWon;

    public static void main(String[] args) {
        // Create a new simulation instance
        CardGame cardSim = new CardGame();

        System.out.println("=== ECM2414 Software Development Coursework ===\nCard Playing Sim");

        // Used to input numOfPlayers and packFilePath
        Scanner sc = new Scanner(System.in);

        // Input number of players
        try {
            // Ask user for number of players
            System.out.print("Please enter the number of players: ");
            // Read the user's input
            int input = sc.nextInt();
            // Ensure there are adequate players
            if (input < 2) {
                throw new InputMismatchException();
            }
            cardSim.numOfPlayers = input;
            // Ignore whitespace and go to next line
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("ERROR: Number of players must be an Integer greater than 1.");
            System.exit(1);
        }

        // Holds the path to the pack file
        String packFilePath = "";

        while(cardSim.pack == null) {
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
            cardSim.pack = cardSim.readPackFile(packFilePath);
        }

        // Close the input stream
        sc.close();

        // TODO: Create an array of players (threads)
        cardSim.players = new Player[cardSim.numOfPlayers];
        for (int i = 0; i < cardSim.numOfPlayers; i++) {
            cardSim.players[i] = new Player(i+1,cardSim);
        }

        // TODO: Use round-robin to distribute cards to players
        cardSim.distributeCardsToPlayers();

        // TODO: Create an array of decks
        cardSim.decks = new Deck[cardSim.numOfPlayers];
        for (int i = 0; i < cardSim.numOfPlayers; i++) {
            cardSim.decks[i] = new Deck();
        }

        // TODO: Use round-robin to distribute remaining cards to decks

        // TODO: Assign left and right decks to players

        // TODO: Start all threads (players)

        // FIXME: Test code; must be removed before submission
        System.out.println(cardSim.numOfPlayers);
        System.out.println(packFilePath);
    }

    /**
     * Reads the data from the provided pack file and validates it based on whether
     * there are 8n values, each value is a positive integer, and if there are 4 values for each
     * corresponding player.
     * @param packFilePath The file path to the pack file to read and validate.
     * @return Parsed pack file as a Pack object.
     */
    private Pack readPackFile(String packFilePath) {
        // Create a new File object
        File packFile = new File(packFilePath);

        // Declare variable to hold card value
        int cardValue;

        // Tracks number of cards for each player
        int[] playerCardCountTracker = new int[this.numOfPlayers];

        // Keeps track of the number of cards in the pack file
        int numOfCards = 0;

        // Initialise a new pack of the required length
        Pack pack = new Pack(this.numOfPlayers);

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
                if (cardValue <= this.numOfPlayers) {
                    playerCardCountTracker[cardValue-1]++;
                }
                // Add this card to the pack
                pack.addCard(new Card(cardValue));
                // Increase number of cards tracker
                numOfCards++;
            }

            // Ensure there are 8n cards
            if (numOfCards != 8*this.numOfPlayers) {
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

    // TODO: Ensure Javadoc is updated once method functionality is confirmed
    /**
     * Informs all others threads that the calling thread has won the game. Synchronization is used
     * to ensure no other thread can declare that they have won once one thread has.
     * @param playerValue The value of the player (thread) that has won the game.
     */
    public synchronized void stopAllOtherThreads(int playerValue) {
        // TODO: Sets flag of all other threads to indicate argument thread has won the game

        // Check if another player has already declared it has won the game
        if (playerWon) {
            return;
        }

        // Convert player value to index of thread in array of threads
        int indexOfWinningThread = playerValue - 1;

        // TODO: Iterate through all other threads and set their "otherThreadWon" flag to "true"

        // TODO: Also, tell all threads which player won; i.e., pass in the winning player's value
    }

    // TODO: Add Javadoc for this method
    private void distributeCardsToPlayers() {
        // TODO: Use round-robin to distribute cards to players
    }
}
