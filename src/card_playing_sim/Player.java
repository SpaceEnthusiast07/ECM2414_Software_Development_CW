package card_playing_sim;

import java.io.FileWriter;
import java.io.IOException;

public class Player implements Runnable {
    // Each player has a hand of 4 cards - THREAD CONFINED
    private Card[] hand;

    // Holds the number of cards in a player's hand
    private final int NUM_CARDS_IN_HAND = 4;

    // The value of this player, i.e. 1 for player1, 2 for player2, etc
    private final int playerValue;

    // Reference to main program thread so this thread can tell all
    // other threads to stop once this thread has won
    private final CardGame mainProgram;

    // Flag to track whether another player (thread) has won the game
    private boolean otherPlayerWon;

    // Flag to track whether this player (thread) has won the game
    private boolean thisPlayerWon;

    // Holds the winning player's value
    private int winningPlayerValue;

    // Cards are taken from the left deck
    private Deck leftDeck;

    // Cards are discarded to the right deck
    private Deck rightDeck;

    // Used to know where to place the card when using giveCard()
    int freeCardIndex;

    /**
     * Initialises a new Player.
     * @param playerValue An Integer value for this player, i.e. 1 for player1, 2 for player2, etc.
     * @param mainProgram Reference to main program thread, used for game termination.
     */
    public Player(int playerValue, CardGame mainProgram) {
        this.playerValue = playerValue;
        this.mainProgram = mainProgram;

        // Initialise the hand of this player
        hand = new Card[NUM_CARDS_IN_HAND];
    }

    // TODO: Check method functionality
    /**
     * Main Player logic for taking and discarding cards, and determining if they are a winner, and
     * hence game termination.
     */
    @Override
    public void run() {
        while (!otherPlayerWon && !thisPlayerWon) {
            // Check whether this player has won
            if (isPlayerWon()) {
                // Mark this player as won
                this.thisPlayerWon = true;

                // TODO: Inform all other players (threads) that this player (thread) has won the game
                mainProgram.stopAllOtherThreads(this.playerValue);

                // TODO: Indicate which player has won the game
                this.winningPlayerValue = this.playerValue;

                // TODO: Update log file to indicate that this thread has won
                this.logWinningPlayer();
            } else {
                // TODO: Output everything this player does to its own log file
                String playerLogFileName = String.format("player%d_output.txt", this.playerValue);

                try (FileWriter fw = new FileWriter(playerLogFileName)) {
                    // Take card from left deck
                    Card takenCard = leftDeck.takeFromDeck();

                    // Write to log file which card this player just took and from which deck
                    fw.write(String.format("player %d draws a %d from deck %d", this.playerValue, takenCard.getValue(), leftDeck.getDeckValue()));

                    // Choose card to discard
                    int indexOfCardToDiscard = -1;
                    for (int i = 0; i < NUM_CARDS_IN_HAND; i++) {
                        if (hand[i].getValue() != playerValue) {
                            indexOfCardToDiscard = i;
                            break;
                        }
                    }

                    // Discard chosen card to right deck
                    rightDeck.discardToDeck(hand[indexOfCardToDiscard]);

                    // Write to log file which card this player just discarded and to which deck
                    fw.write(String.format("player %d discards a %d to deck %d", this.playerValue, hand[indexOfCardToDiscard].getValue(), rightDeck.getDeckValue()));

                    // Insert taken card into discarded card's location
                    hand[indexOfCardToDiscard] = takenCard;

                    // Write this player's hand to the log file
                    String thisPlayersHand = "";
                    for (int i = 0; i < NUM_CARDS_IN_HAND; i++) {
                        thisPlayersHand = thisPlayersHand.concat(hand[i].getValue() + " ");
                    }
                    fw.write(String.format("player %d current hand is %s", this.playerValue, thisPlayersHand));
                } catch (IOException e) {
                    System.out.printf("ERROR: Failed to write winning player stats to log file \"%s\" for winning player%d\n", playerLogFileName, this.winningPlayerValue);
                }
            }
        }
    }

    /**
     * Allows the client to manually give this player a card. Used during pack
     * distribution at the start of each game.
     * @param card The new card to give to this player.
     * @throws InvalidOperation When player already has a full hand.
     */
    public void giveCard(Card card) throws InvalidOperation {
        // Check if the hand is full
        if (freeCardIndex == hand.length) {
            throw new InvalidOperation("Cannot give card to player with full hand.");
        }

        // Add the new card to the hand
        hand[freeCardIndex] = card;

        // Increment the pointer
        freeCardIndex++;
    }

    /**
     * Determines if this player has won the game.
     * @return True if this player has won the game, false otherwise.
     */
    private boolean isPlayerWon() {
        boolean isWon = true;
        int cardIndexInHand = 0;

        while (isWon && cardIndexInHand < NUM_CARDS_IN_HAND) {
            if (hand[cardIndexInHand].getValue() != playerValue) {
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
    public void setLeftDeck(Deck leftDeck) {
        this.leftDeck = leftDeck;
    }

    /**
     * Allows the client to set this player's right deck.
     * @param rightDeck Deck i+1 for player i.
     */
    public void setRightDeck(Deck rightDeck) {
        this.rightDeck = rightDeck;
    }

    // TODO: Write Javadoc
    private void logWinningPlayer() {
        // TODO: Set up file writing
        String playerLogFileName = String.format("player%d_output.txt", this.playerValue);
        try (FileWriter fw = new FileWriter(playerLogFileName)) {
            // TODO: Write which player has won (use string concatenation and winningPlayerValue variable)
            fw.write(String.format("player %d wins", this.winningPlayerValue));

            // TODO: Log that this player has exited the game; e.g., "player 1 exits"
            fw.write(String.format("player %d exits", this.playerValue));

            // TODO: Log this player's final hand
            String thisPlayersHand = "";
            for (int i = 0; i < NUM_CARDS_IN_HAND; i++) {
                thisPlayersHand = thisPlayersHand.concat(hand[i].getValue() + " ");
            }
            fw.write(String.format("player %d final hand: %s", this.playerValue, thisPlayersHand));
        } catch (IOException e) {
            System.out.printf("ERROR: Failed to write winning player stats to log file \"%s\" for winning player%d\n", playerLogFileName, this.winningPlayerValue);
        }
    }

    // TODO: Write Javadoc
    public void acknowledgeOtherPlayerWon(int winningPlayerValue) {
        // TODO: Update flag to indicate that another player has won
        this.otherPlayerWon = true;

        // TODO: Set the winning player value
        this.winningPlayerValue = winningPlayerValue;

        // TODO: Write to log file this player's hand and which player won
        this.logWinningPlayer();
    }
}
