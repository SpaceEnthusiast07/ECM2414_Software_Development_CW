package card_playing_sim;

public class Deck {
    // Array of cards in deck
    private Card[] cards;

    // Holds this decks value
    private int deckValue;

    // Holds the max number of cards in a deck
    private final int MAX_CARDS_IN_DECK = 5;

    // Points to the next available space in the deck
    private int back;

    /**
     * Initialises a new deck with a capacity of 5 cards by default.
     */
    public Deck(int deckValue) {
        this.cards = new Card[MAX_CARDS_IN_DECK];
        this.deckValue = deckValue;
    }

    /**
     * Adds a new card to the bottom (back) of the deck and simply returns if the deck is full.
     * Synchronization is used here to ensure only one thread can manipulate this deck at a time.
     * @param newCard The new Card to add to the deck.
     * @throws InvalidOperationException When discarding to a full deck.
     */
    public synchronized void discardToDeck(Card newCard) throws InvalidOperationException {
        // Check if the deck is full
        if (back == cards.length) {
            throw new InvalidOperationException("Cannot discard to a full deck.");
        }

        // Add the new card
        cards[back] = newCard;

        // Increment the back pointer to point to the next available space
        back++;
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
        if (back == 0) {
            throw new InvalidOperationException("Cannot take from an empty deck.");
        }

        // Store the reference to the card to remove
        Card removedCard = cards[0];

        // Shift all other cards left one space in the array
        int indexOfCardToShift = 1;
        while(indexOfCardToShift < MAX_CARDS_IN_DECK && cards[indexOfCardToShift] != null) {
            cards[indexOfCardToShift-1] = cards[indexOfCardToShift];
            indexOfCardToShift++;
        }
        cards[indexOfCardToShift-1] = null;

        // Update back pointer to reflect shifted cards
        back--;

        // Return the removed card
        return removedCard;
    }

    // TODO: Write Javadoc
    public int getDeckValue() {
        return this.deckValue;
    }
}
