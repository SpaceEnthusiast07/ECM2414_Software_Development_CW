package card_playing_sim;

public class Deck {
    // Array of cards in deck
    private Card[] cards;
    // Points to first card in deck
    private int front;
    // Points to the next available space in the deck
    private int back;

    public Deck() {
        this.cards = new Card[5];
    }

    public void addCard(Card newCard) {
        // Check if the deck is full
        if (back == cards.length) {
            return;
        }

        // Increment the back pointer
        back++;

        // Add the new card
        cards[back] = newCard;
    }

    public Card removeCard() {
        // Check if the deck contains a card to remove
        if (front == back) {
            return null;
        }

        // Store the reference to the card to remove
        Card removedCard = cards[front];

        // Shift all other cards up one space
        int cardToShift = 1;
        while(cards[cardToShift] != null) {
            cards[cardToShift-1] = cards[cardToShift];
            cardToShift++;
        }
        cards[cardToShift] = null;

        // Update back pointers to reflect shifted cards
        back--;

        // Return the removed card
        return removedCard;
    }
}
