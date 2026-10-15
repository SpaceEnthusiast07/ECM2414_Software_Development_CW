package card_playing_sim;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Contains all the logic for a player's game strategy as well as the logging of a player's every move and
 * which player won at the end of the game.
 * <br>
 *  - To create a new Player, the player's value and a reference to the main program object must be passed in.
 * <br>
 *  - {@link #run()} is called automatically once {@link Thread#start()} is called and contains the player's card logic.
 * <br>
 *  - During {@link Card} distribution at the start of each game, the {@link #giveCard(Card newCard)} method is called
 *  to explicitly give a player a card.
 * <br>
 *  - The player takes Cards from its left deck, which is set using {@link #setLeftDeck(Deck leftDeck)}, and Cards are
 *  discarded to its right deck, which is set using {@link #setRightDeck(Deck rightDeck)}.
 */
class Player implements Runnable {
    // Each player has a hand of 4 cards - THREAD CONFINED
    private Card[] hand;

    // The value of this player, i.e. 1 for player1, 2 for player2, etc
    private final int PLAYER_VALUE;

    // This player's log file name
    private final String PLAYER_LOG_FILE_NAME;

    // Reference to main program object so this player can ask main program to write
    // all the deck contents to their corresponding log files
    private final CardGame MAIN_PROGRAM;

    // Flag to track whether this player has won the game
    private boolean thisPlayerWon;

    // Flag to track whether another player has won the game
    private static volatile boolean otherPlayerWon;

    // Holds the winning player's value
    private static volatile int winningPlayerValue;

    // Cards are taken from the left deck
    private Deck leftDeck;

    // Cards are discarded to the right deck
    private Deck rightDeck;

    // Used to know where to place the card when using giveCard()
    private int freeCardIndex;

    /**
     * Initialises a new Player.
     * @param playerValue An Integer value for this player, i.e. 1 for player1, 2 for player2, etc.
     * @param mainProgram Reference to main program object, used for game termination.
     */
    Player(int playerValue, CardGame mainProgram) {
        this.PLAYER_VALUE = playerValue;
        this.MAIN_PROGRAM = mainProgram;

        // Set flags
        otherPlayerWon = false;
        this.thisPlayerWon = false;

        // Initialise the hand of this player
        this.hand = new Card[CardGame.NUM_CARDS_IN_HAND];

        // Construct this player's log file name
        this.PLAYER_LOG_FILE_NAME = String.format("%splayer%d_output.txt", CardGame.LOG_FILE_DIRECTORY, this.PLAYER_VALUE);
    }

    /**
     * Main Player logic for taking and discarding cards, and determining if they are a winner, and
     * hence game termination.
     * <br>
     * During game termination, this player will write to its log file stating which player won,
     * and this player's final hand.
     */
    @Override
    public void run() {
        while (!otherPlayerWon && !thisPlayerWon) {
            // Check whether this player has won
            if (isPlayerWon()) {
                // Mark this player as won
                this.thisPlayerWon = true;

                // Indicate to all players that this player has won the game
                winningPlayerValue = this.PLAYER_VALUE;
                otherPlayerWon = true;

                // Output to the terminal which player has won
                System.out.printf("player %d wins\n", this.PLAYER_VALUE);

                MAIN_PROGRAM.logAllDecksContents();
            }
            // Check whether this player can take and discard a card to its left and right deck respectively
            else if (this.leftDeck.canTakeFromDeck() && this.rightDeck.canDiscardToDeck()) {
                // Output everything this player does to its own log file
                // Append flag is true because we want to add more information to the log file rather than overwrite it
                try (FileWriter fw = new FileWriter(this.PLAYER_LOG_FILE_NAME, true)) {
                    // Take card from left deck
                    Card takenCard = this.leftDeck.takeFromDeck();

                    // Write to log file which card this player just took and from which deck
                    fw.write(String.format("player %d draws a %d from deck %d\n", this.PLAYER_VALUE,
                            takenCard.getValue(), this.leftDeck.getDeckValue()));

                    // Choose card to discard
                    int indexOfCardToDiscard = getRandomDiscardableCardIndex();

                    // Discard chosen card to right deck
                    this.rightDeck.discardToDeck(this.hand[indexOfCardToDiscard]);

                    // Write to log file which card this player just discarded and to which deck
                    fw.write(String.format("player %d discards a %d to deck %d\n", this.PLAYER_VALUE,
                           this.hand[indexOfCardToDiscard].getValue(), this.rightDeck.getDeckValue()));

                    // Insert taken card into discarded card's location
                    this.hand[indexOfCardToDiscard] = takenCard;

                    // Convert this player's hand to a string
                    String thisPlayersHand = convertHandToString();

                    // Write this player's hand to the log file
                    fw.write(String.format("player %d current hand is %s\n", this.PLAYER_VALUE, thisPlayersHand));
                } catch (IOException e) {
                    System.out.printf("ERROR: Failed to write winning player stats to log file \"%s\" for winning player%d\n",
                            this.PLAYER_LOG_FILE_NAME, winningPlayerValue);
                } catch (InvalidOperationException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        // Write to log file this player's hand and which player won
        this.logWinningPlayer();
    }

    /**
     * Allows the client to manually give this player a card. Used during pack
     * distribution at the start of each game.
     * @param card The new card to give to this player.
     * @throws InvalidOperationException When this player's hand is already full.
     */
    void giveCard(Card card) throws InvalidOperationException {
        // Check if the hand is full
        if (this.freeCardIndex == this.hand.length) {
            throw new InvalidOperationException("ERROR: Cannot give a card to a player with a full hand.");
        }

        // Add the new card to the hand
        this.hand[this.freeCardIndex] = card;

        // Increment the pointer
        this.freeCardIndex++;
    }

    /**
     * Determines if this player has won the game.
     * @return True if this player has won the game, false otherwise.
     */
    private boolean isPlayerWon() {
        boolean isWon = true;
        int cardIndexInHand = 0;

        while (isWon && cardIndexInHand < CardGame.NUM_CARDS_IN_HAND) {
            if (this.hand[cardIndexInHand].getValue() != PLAYER_VALUE) {
                isWon = false;
            }
            cardIndexInHand++;
        }

        return isWon;
    }

    /**
     * Allows the client to set this player's left deck.
     * @param leftDeck Deck i for player i.
     */
    void setLeftDeck(Deck leftDeck) {
        this.leftDeck = leftDeck;
    }

    /**
     * Allows the client to set this player's right deck.
     * @param rightDeck Deck i+1 for player i.
     */
    void setRightDeck(Deck rightDeck) {
        this.rightDeck = rightDeck;
    }

    /**
     * Converts this player's hand to a String so it can be written to this player's log file.
     * @return This player's hand represented as a String.
     */
    private String convertHandToString() {
        String thisPlayersHand = "";
        for (int i = 0; i < CardGame.NUM_CARDS_IN_HAND; i++) {
            thisPlayersHand = thisPlayersHand.concat(this.hand[i].getValue() + " ");
        }
        return thisPlayersHand;
    }

    /**
     * Invoked at the start of each game and logs a player's initial hand.
     */
    void logInitialHand() {
        // Append flag is false, by default, because we want the output file to be fresh each time we play the game
        try (FileWriter fw = new FileWriter(this.PLAYER_LOG_FILE_NAME)) {
            // Convert the players hand to a string
            String thisPlayersHand = convertHandToString();

            // FIXME: Spec doesn't include ":" in initial log file output, but does for all other log file outputs!!
            // Log this player's initial hand to their log file
            fw.write(String.format("player %d initial hand: %s\n", this.PLAYER_VALUE, thisPlayersHand));
        } catch (IOException e) {
            System.out.printf("ERROR: Failed to write initial hand to log file \"%s\" for player%d\n",
                    this.PLAYER_LOG_FILE_NAME, this.PLAYER_VALUE);
        }
    }

    /**
     * Writes which player won the game, the fact that this player has exited the game, and this
     * player's final hand to this player's log file.
     */
    private void logWinningPlayer() {
        // Append flag is true because we want to add more information to the log file rather than overwrite it
        try (FileWriter fw = new FileWriter(this.PLAYER_LOG_FILE_NAME, true)) {
            // Write which player has won to log file
            fw.write(String.format("player %d wins\n", winningPlayerValue));

            // Log that this player has exited the game; e.g., "player 1 exits"
            fw.write(String.format("player %d exits\n", this.PLAYER_VALUE));

            // Convert this player's hand to a string
            String thisPlayersHand = convertHandToString();

            // Log this player's final hand
            fw.write(String.format("player %d final hand: %s\n", this.PLAYER_VALUE, thisPlayersHand));
        } catch (IOException e) {
            System.out.printf("ERROR: Failed to write winning player stats to log file \"%s\" for winning player%d\n",
                    this.PLAYER_LOG_FILE_NAME, winningPlayerValue);
        }
    }

    /**
     * Randomly chooses a card to discard that is not the value of this player.
     * @return The randomly chosen card index to discard.
     */
    private int getRandomDiscardableCardIndex() {
        // Create array to hold potential discardable indices
        int[] potentialDiscardableIndices = new int[CardGame.NUM_CARDS_IN_HAND];

        // Marks next slot to insert potential index into tracker array
        int nextFreeSlot = 0;

        // Tracks the quantity of potential indices
        int numOfPotentialIndices = 0;

        // Determine potential card to discard and note index in tracker array
        for (int i = 0; i < CardGame.NUM_CARDS_IN_HAND; i++) {
            if (this.hand[i].getValue() != this.PLAYER_VALUE) {
                potentialDiscardableIndices[nextFreeSlot] = i;
                nextFreeSlot++;
                numOfPotentialIndices++;
            }
        }

        // Set up random number generator
        Random rand = new Random();

        // Choose random index to discard
        return potentialDiscardableIndices[rand.nextInt(0, numOfPotentialIndices)];
    }
}
