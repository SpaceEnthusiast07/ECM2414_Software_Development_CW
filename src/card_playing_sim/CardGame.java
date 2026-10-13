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

    // Stores the number of cards in a player's hand
    private final int NUM_CARDS_IN_HAND = 4;

    // Stores the number of cards in a player's hand
    private final int NUM_CARDS_IN_DECK = 4;

    // Holds the pack of cards read from file
    private Pack pack;

    // Holds all the players of the game
    private Player[] players;

    // Holds all the threads used to run each player on
    private Thread[] threads;

    // Holds all the decks in the game
    private Deck[] decks;

    // Flag to keep track of whether a player has won
    private boolean playerWon;

    public static void main(String[] args) {
        // Create a new simulation instance
        CardGame cardSim = new CardGame();

        System.out.println("=== ECM2414 Software Development Coursework ===\nCard Playing Sim\n");

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
            endProgram("ERROR: Number of players must be an Integer greater than 1.");
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
                endProgram("ERROR: Invalid location of pack file.");
            }

            // Read and validate the provided pack file
            cardSim.pack = cardSim.readPackFile(packFilePath);
        }

        // Close the input stream
        sc.close();

        // Create an array of players
        cardSim.players = new Player[cardSim.numOfPlayers];
        for (int i = 0; i < cardSim.numOfPlayers; i++) {
            cardSim.players[i] = new Player(i+1,cardSim);
        }

        // Use round-robin to distribute cards to players
        try {
            cardSim.distributeCardsToPlayers();
        } catch (InvalidOperationException e) {
            endProgram(e.getMessage());
        }

        // Create an array of decks
        cardSim.decks = new Deck[cardSim.numOfPlayers];
        for (int i = 0; i < cardSim.numOfPlayers; i++) {
            cardSim.decks[i] = new Deck(i+1);
        }

        // Use round-robin to distribute remaining cards to decks
        try {
            cardSim.distributeCardsToDecks();
        } catch (InvalidOperationException e) {
            endProgram(e.getMessage());
        }

        // Assign left and right decks to players
        cardSim.assignDecksToPlayers();

        // Create a list of all threads
        cardSim.threads = new Thread[cardSim.numOfPlayers];
        for (int i = 0; i < cardSim.numOfPlayers; i++) {
            cardSim.threads[i] = new Thread(cardSim.players[i]);
        }

        // Start all threads (players)
        for (int i = 0; i < cardSim.numOfPlayers; i++) {
            cardSim.threads[i].start();
        }
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
                    throw new InvalidPackFileException("ERROR: Pack file must only contain positive card values.");
                }
                // If within range of number of players, update card tracker
                if (cardValue <= this.numOfPlayers) {
                    playerCardCountTracker[cardValue-1]++;
                }
                // Add this card to the pack
                pack.addCardToBottom(new Card(cardValue));
                // Increase number of cards tracker
                numOfCards++;
            }

            // Ensure there are 8n cards
            if (numOfCards != 8*this.numOfPlayers) {
                throw new InvalidPackFileException("ERROR: Pack file must contain exactly 8n cards.");
            }

            // Ensure there are at least 4 cards of each player's index
            for (int i = 0; i < playerCardCountTracker.length; i++) {
                if (playerCardCountTracker[i] < 4) {
                    throw new InvalidPackFileException("ERROR: Pack file must contain at least 4 cards for each player.");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Pack file does not exist.");
            return null;
        } catch (InputMismatchException e) {
            System.out.println("ERROR: At least 1 card value in the provided pack is not an Integer.");
            return null;
        } catch (InvalidPackFileException e) {
            System.out.println(e.getMessage());
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

        // TODO: Output to the terminal which player has won
    }

    /**
     * Uses round-robin to distribute cards from the pack to each player.
     * Done before distributing cards to decks.
     */
    private void distributeCardsToPlayers() throws InvalidOperationException {
        // Iterate through the number of cards to give to each player
        for (int i = 0; i < NUM_CARDS_IN_HAND; i++) {
            // Iterate through each player
            for (int j = 0; j < this.numOfPlayers; j++) {
                // Take the top card from the pack and give it to this player
                players[j].giveCard(pack.takeTopCard());
            }
        }
    }

    /**
     * Uses round-robin to distribute cards from the pack to each deck.
     * Done after distributing cards to players.
     */
    private void distributeCardsToDecks() throws InvalidOperationException {
        // Iterate through the number of cards to give to each deck
        for (int i = 0; i < NUM_CARDS_IN_DECK; i++) {
            // Iterate through each deck
            for (int j = 0; j < this.numOfPlayers; j++) {
                // Take the top card from the pack and give it to this deck
                decks[j].discardToDeck(pack.takeTopCard());
            }
        }
    }

    /**
     * Assigns each player their corresponding left and right deck. For player i, their left
     * deck is deck i and their right deck is deck i+1.
     */
    private void assignDecksToPlayers() {
        // Iterate through each player
        for (int i = 0; i < numOfPlayers; i++) {
            // Left deck is deck i for player i
            players[i].setLeftDeck(decks[i]);
            // Right deck is i+1 for player i
            if (i+1 == decks.length) {
                players[i].setRightDeck(decks[0]);
            } else {
                players[i].setRightDeck(decks[i+1]);
            }
        }
    }

    private static void endProgram(String reason) {
        System.out.println("\n-----------------\n");
        System.out.println(reason);
        System.out.println("\nEnding program...\n");
        System.exit(1);
    }
}
