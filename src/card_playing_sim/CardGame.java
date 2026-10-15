package card_playing_sim;

// Used for numOfPlayers, packFilePath, and pack file input
import java.io.IOException;
import java.util.Scanner;
import java.util.InputMismatchException;

// Used to read the pack file
import java.io.File;
import java.io.FileNotFoundException;

// Used to create log file directory
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class CardGame {
    // Represents the number of players, equivalent of n in documentation
    private int numOfPlayers;

    // Stores the number of cards in a player's hand
    static final int NUM_CARDS_IN_HAND = 4;

    // Stores the number of cards in a player's hand
    static final int NUM_CARDS_IN_DECK = 4;

    // Stores the string directory name of where log files are stored to
    static final String LOG_FILE_DIRECTORY = "card_playing_sim_log_files/";

    // Holds the pack of cards read from file
    private Pack pack;

    // Holds all the players of the game
    private Player[] players;

    // Holds all the threads used to run each player on
    private Thread[] threads;

    // Holds all the decks in the game
    private Deck[] decks;

    // Flag to keep track of whether a player has won
    private boolean anyPlayersWon = false;

    public static void main(String[] args) {
        // Create a new simulation instance
        CardGame cardSim = new CardGame();

        System.out.println("=== ECM2414 Software Development Coursework ===\nCard Playing Sim");

        // Used to input numOfPlayers and packFilePath
        Scanner sc = new Scanner(System.in);

        boolean validNumOfPlayers = false;
        while (!validNumOfPlayers) {
            // Input number of players
            try {
                // Ask user for number of players
                System.out.print("\nPlease enter the number of players: ");

                // Read the user's input
                int input = sc.nextInt();

                // Ensure there are adequate players
                if (input < 2) {
                    throw new InputMismatchException();
                }

                // Set number of players
                cardSim.numOfPlayers = input;

                // Ignore whitespace and go to next line
                sc.nextLine();

                // Update flag to indicate a valid number of players has been entered
                validNumOfPlayers = true;
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Number of players must be an Integer greater than 1.");
                // Clear input stream ready for next input
                sc.nextLine();
            }
        }

        // Holds the path to the pack file
        String packFilePath = "";

        // Input pack file name
        while(cardSim.pack == null) {
            // Ask user for the path to the pack file
            System.out.print("\nPlease enter location of pack to load: ");

            // Read the user's input
            packFilePath = sc.nextLine();

            // Read and validate the provided pack file
            cardSim.pack = cardSim.readPackFile(packFilePath);
        }

        // Close the input stream
        sc.close();

        // Set up the log file directory
        cardSim.setUpLogFileDirectory();

        // Create an array of players
        cardSim.players = new Player[cardSim.numOfPlayers];
        for (int i = 0; i < cardSim.numOfPlayers; i++) {
            cardSim.players[i] = new Player(i+1, cardSim);
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
            // Construct a descriptive thread name
            String customThreadName = String.format("player%d-thread", i+1);

            // Create a new Thread
            cardSim.threads[i] = new Thread(cardSim.players[i], customThreadName);

            // Log the current player's initial hand
            cardSim.players[i].logInitialHand();
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

    // TODO: Jake, can you check functionality and Javadoc?
    /**
     * Informs all others players that the calling player has won the game. Synchronization is used
     * to ensure no other thread can declare that they have won once one player has.
     * @param playerValue The value of the player that has won the game.
     */
    public synchronized void stopAllOtherThreads(int playerValue) {
        // Check if another player has already declared it has won the game
        if (this.anyPlayersWon) {
            return;
        }

        // Convert playerValue to index of player in array of players
        int indexOfWinningPlayer = playerValue - 1;

        // Inform all other players that this player has won
        for (int i = 0; i < this.numOfPlayers; i++) {
            // Skip the calling player
            if (i != indexOfWinningPlayer) {
                this.players[i].acknowledgeOtherPlayerWon(playerValue);
            }
        }

        // Output to the terminal which player has won
        System.out.printf("player %d wins\n", playerValue);

        // Log the contents of each deck to its corresponding log file
        for (int i = 0; i < this.numOfPlayers; i++) {
            this.decks[i].logFinalDeckContents(LOG_FILE_DIRECTORY);
        }

        this.anyPlayersWon = true;
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

    // TODO: Jake, can you check the Javadoc and my modifications?
    /**
     * Called when an exception is thrown and the problem cannot be resolved via normal means.
     * @param reason A helpful message to inform the user of why the program quit.
     */
    private static void endProgram(String reason) {
        System.out.printf("\n-----------------\n\n%s\nEnding program...\n\n", reason);
        System.exit(1);
    }

    // TODO: Update Javadoc once functionality is confirmed
    /**
     * Creates the directory to store the player and deck log files if it doesn't
     * already exist.
     */
    private void setUpLogFileDirectory() {
        // TODO: May want to delete directory if it already exists for a fresh start
        try {
            // Create a path object for the log files
            Path path = Paths.get(String.format("%splayer1_output.txt", LOG_FILE_DIRECTORY));

            if (path.getParent() != null) {
                // Create the log file directory
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            System.out.println("ERROR: Cannot create directory for log files.");
        }
    }
}
