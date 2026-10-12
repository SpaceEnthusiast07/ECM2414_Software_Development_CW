package card_playing_sim;

public class Deck {
    // Array of cards in deck
    private Card[] cards;

    // Points to the next available space in the deck
    private int back;

    /**
     * Initialises a new deck with a capacity of 5 cards.
     */
    public Deck() {
        this.cards = new Card[5];
    }

    /**
     * Adds a new card to the bottom (back) of the deck and simply returns if the deck is full.
     * Synchronization is used here to ensure only one thread can manipulate this deck at a time.
     * @param newCard The new Card to add to the deck.
     */
    public synchronized void addCard(Card newCard) {
        // Check if the deck is full
        if (back == cards.length) {
            return;
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
     */
    public synchronized Card removeCard() {
        // Check if the deck contains a card to remove
        if (back == 0) {
            return null;
        }

        // Store the reference to the card to remove
        Card removedCard = cards[0];

        // Shift all other cards up one space
        int indexOfCardToShift = 1;
        while(cards[indexOfCardToShift] != null) {
            cards[indexOfCardToShift-1] = cards[indexOfCardToShift];
            indexOfCardToShift++;
        }
        cards[indexOfCardToShift-1] = null;

        // Update back pointer to reflect shifted cards
        back--;

        // Return the removed card
        return removedCard;
    }
}
