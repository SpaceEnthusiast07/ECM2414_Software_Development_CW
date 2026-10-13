package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckTest {

    @DisplayName("TEST: discarding and taking from deck")
    @Test
    void deckDiscardAndTake() {
        // Create 3 cards of different values (1,3,2)
        Card card1 = new Card(3);
        Card card2 = new Card(2);
        Card card3 = new Card(5);

        // Create an empty deck
        Deck myDeck = new Deck();

        // Discard 2 cards to deck
        myDeck.discardToDeck(card2);
        myDeck.discardToDeck(card3);

        // Take 1 card from deck and check is value of first card discarded
        assertEquals(card2.getValue(), myDeck.takeFromDeck().getValue());

        // Discard the last card to deck
        myDeck.discardToDeck(card1);

        // Take both remaining cards from deck and check values are in FIFO order
        assertEquals(card3.getValue(), myDeck.takeFromDeck().getValue());
        assertEquals(card1.getValue(), myDeck.takeFromDeck().getValue());
    }
}
