package card_playing_sim;

public class Player implements Runnable {
    // Each player has a hand of 4 cards - THREAD CONFINED
    private Card[] hand;

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
                thisPlayerWon = true;

                // TODO: Inform all other players (threads) that this player (thread) has won the game

                // TODO: Output to the terminal that this player has won

                // TODO: Write this players hand to its corresponding log file

                // TODO: Note in this log file that this player has won
            } else {
                // Take card from left deck
                Card takenCard = leftDeck.takeFromDeck();

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

                // Insert taken card into discarded card's location
                hand[indexOfCardToDiscard] = takenCard;
            }

            // TODO: Output everything this player does to its own log file
        }

        // TODO: If other player has won, output this player's stats into its log file

        // TODO: Note in this log file which player won the game
    }

    /**
     * Allows the client to manually give this player a card. Used during pack
     * distribution at the start of each game.
     * @param card The new card to give to this player.
     */
    public void giveCard(Card card) {
        // Check if the hand is full
        if (freeCardIndex == hand.length) {
            return;
        }

        // Add the new card to the hand
        hand[freeCardIndex] = card;

        // Increment the pointer
        freeCardIndex++;
    }

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
}
