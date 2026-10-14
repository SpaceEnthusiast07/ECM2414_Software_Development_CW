package card_playing_sim;

// TODO: Jake, can you check Deck class Javadoc?

/**
 * Holds a fixed amount of {@link Card}s and utilises a queue structure for taking and discarding cards.
 * <br>
 *  - To create a new Deck, simply pass in its value.
 * <br>
 *  - To discard a card to the bottom of the deck, call {@link #discardToDeck(Card newCard)}.
 * <br>
 *  - To take the top card, call {@link #takeFromDeck()}, which returns the top card as a {@link Card} object.
 * <br>
 *  - To determine this deck's value, call {@link #getDeckValue()}.
 */
public class Deck {
    // Array of cards in deck
    private Card[] cards;

    // Holds this deck's value
    private final int deckValue;

    // Holds the max number of cards in a deck
    private final int MAX_CARDS_IN_DECK = 5;

    // Points to the next AVAILABLE space in the deck
    private int back;

    /**
     * Initialises a new deck with a capacity of 5 cards by default.
     */
    public Deck(int deckValue) {
        this.cards = new Card[MAX_CARDS_IN_DECK];
        this.deckValue = deckValue;
    }

    /**
     * Adds a new card to the bottom (back) of the deck and throws an exception if the deck is full.
     * Synchronization is used here to ensure only one thread can manipulate this deck at a time.
     * @param newCard The new Card to add to the deck.
     * @throws InvalidOperationException When discarding to a full deck.
     */
    public synchronized void discardToDeck(Card newCard) throws InvalidOperationException {
        // Check if the deck is full
        if (this.back == this.cards.length) {
            throw new InvalidOperationException("ERROR: Cannot discard to a full deck.");
        }

        // Add the new card
        this.cards[this.back] = newCard;

        // Increment the back pointer to point to the next available space
        this.back++;
    }

    /**
     * Removes and returns the card at the top of the deck, and shifts all other cards
     * in the array one space to the left.
     * Synchronization is used here to ensure only one thread can manipulate this deck at a time.
     * @return The card off the top of the deck.
     * @throws InvalidOperationException When taking from an empty deck.
     */
    public synchronized Card takeFromDeck() throws InvalidOperationException {
        // Check if the deck contains a card to remove
        if (this.back == 0) {
            throw new InvalidOperationException("ERROR: Cannot take from an empty deck.");
        }

        // Store the reference to the card to remove
        Card removedCard = this.cards[0];

        // Shift all other cards left one space in the array
        int indexOfCardToShift = 1;
        while(indexOfCardToShift < MAX_CARDS_IN_DECK && this.cards[indexOfCardToShift] != null) {
            this.cards[indexOfCardToShift-1] = this.cards[indexOfCardToShift];
            indexOfCardToShift++;
        }
        this.cards[indexOfCardToShift-1] = null;

        // Update back pointer to reflect shifted cards
        this.back--;

        // Return the removed card
        return removedCard;
    }

    /**
     * Allows the client to retrieve this deck's value.
     * @return This deck's value as an Integer.
     */
    public int getDeckValue() {
        return this.deckValue;
    }
}
