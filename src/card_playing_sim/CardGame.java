package card_playing_sim;

// Used for numOfPlayers, packFilePath, and pack file input
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.InputMismatchException;

// Used to create log file directory
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CardGame {
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

        // Start the sim
        cardSim.startSim();
    }

    /**
     * Sets up and starts a new multithreaded card playing simulation.
     */
    public void startSim() {
        System.out.println("=== ECM2414 Software Development Coursework ===\nMultithreaded Card Playing Sim");

        // Used to input numOfPlayers and packFilePath
        Scanner sc = new Scanner(System.in);

        // Ask the user to input the number of players
        this.inputNumberOfPlayers(sc);

        // Ack the user to input the name for the pack file and validates it
        this.inputPackFileName(sc);

        // Close the input stream
        sc.close();

        // Set up the log file directory
        this.setUpLogFileDirectory();

        // Create an array of players
        this.players = new Player[this.numOfPlayers];
        for (int i = 0; i < this.numOfPlayers; i++) {
            this.players[i] = new Player(i+1, this);
        }

        // Use round-robin to distribute cards to players
        try {
            this.distributeCardsToPlayers();
        } catch (InvalidOperationException e) {
            endProgram(e.getMessage());
        }

        // Create an array of decks
        this.decks = new Deck[this.numOfPlayers];
        for (int i = 0; i < this.numOfPlayers; i++) {
            this.decks[i] = new Deck(i+1);
        }

        // Use round-robin to distribute remaining cards to decks
        try {
            this.distributeCardsToDecks();
        } catch (InvalidOperationException e) {
            endProgram(e.getMessage());
        }

        // Assign left and right decks to players
        this.assignDecksToPlayers();

        // Create a list of all threads
        this.threads = new Thread[this.numOfPlayers];
        for (int i = 0; i < this.numOfPlayers; i++) {
            // Construct a descriptive thread name
            String customThreadName = String.format("player%d-thread", i+1);

            // Create a new Thread
            this.threads[i] = new Thread(this.players[i], customThreadName);

            // Log the current player's initial hand
            this.players[i].logInitialHand();
        }

        // Start all threads (players)
        for (int i = 0; i < this.numOfPlayers; i++) {
            this.threads[i].start();
        }
    }

    /**
     * Repeatedly asks the user for the number of players of the game until a valid input is provided.
     * @param sc The {@link Scanner} object used for input.
     */
    private void inputNumberOfPlayers(Scanner sc) {
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
                this.numOfPlayers = input;

                // Ignore whitespace and go to next line
                sc.nextLine();

                // Update flag to indicate a valid number of players has been entered
                validNumOfPlayers = true;
            } catch (InputMismatchException e) {
                System.out.println("ERROR: Number of players must be an Integer greater than 1.");
                // Clear input stream ready for next input
                sc.nextLine();
            } catch (NoSuchElementException e) {
                System.out.println("ERROR: Please enter a number of players.");
                // Clear input stream ready for next input
                sc.nextLine();
            }
        }
    }

    /**
     * Repeatedly asks the user for the path to a pack file to load until a valid pack file is given.
     * @param sc The {@link Scanner} object used for input.
     */
    private void inputPackFileName(Scanner sc) {
        // Holds the path to the pack file
        String packFilePath;

        // Input pack file name
        while(this.pack == null) {
            // Ask user for the path to the pack file
            System.out.print("\nPlease enter location of pack to load: ");

            // Read the user's input
            packFilePath = sc.nextLine();

            // Read and validate the provided pack file
            this.pack = Pack.readPackFile(packFilePath, this.numOfPlayers);
        }
    }

    /**
     * Logs each deck's contents to their corresponding log file once a player has declared they have won. Synchronization is used
     * to ensure no other thread can log deck contents at the same time as another thread.
     */
    public synchronized void logAllDecksContents() {
        // Check if another player has already declared it has won the game
        if (anyPlayersWon) {
            return;
        }
        anyPlayersWon = true;

        // Log the contents of each deck to its corresponding log file
        for (int i = 0; i < this.numOfPlayers; i++) {
            this.decks[i].logFinalDeckContents();
        }

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

    /**
     * Called when an exception is thrown and the problem cannot be resolved via normal means.
     * @param reason A helpful message to inform the user of why the program quit.
     */
    private static void endProgram(String reason) {
        System.out.printf("\n-----------------\n\n%s\nEnding program...\n\n", reason);
        System.exit(1);
    }

    /**
     * Creates the directory to store the player and deck log files if it doesn't
     * already exist.
     */
    private void setUpLogFileDirectory() {
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
