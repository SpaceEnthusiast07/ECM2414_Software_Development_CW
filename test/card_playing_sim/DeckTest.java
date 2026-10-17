package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeckTest {

    @DisplayName("TEST: Get deck value")
    @Test
    void getCardValue() {
        // Create an empty deck
        int myNumber = 1;
        Deck myDeck = new Deck(myNumber);

        // Call getter and check returned value is 5
        assertEquals(myNumber, myDeck.getDeckValue());
    }

    @DisplayName("TEST: Discarding and taking from deck")
    @Test
    void deckDiscardAndTake() {
        // Create 3 cards of different values (1,3,2)
        Card card1 = new Card(3);
        Card card2 = new Card(2);
        Card card3 = new Card(5);

        // Create an empty deck
        Deck myDeck = new Deck(1);

        // Discard 2 cards to deck
        myDeck.discardToDeck(card2);
        myDeck.discardToDeck(card3);

        // Take 1 card from deck and check is value of first card discarded
        assertEquals(card2.value(), myDeck.takeFromDeck().value());

        // Discard the last card to deck
        myDeck.discardToDeck(card1);

        // Take both remaining cards from deck and check values are in FIFO order
        assertEquals(card3.value(), myDeck.takeFromDeck().value());
        assertEquals(card1.value(), myDeck.takeFromDeck().value());
    }

    @DisplayName("TEST: Taking from an empty deck")
    @Test
    void takeFromEmptyDeck() {
        // Create an empty deck
        Deck emptyDeck = new Deck(1);

        //Take from empty deck and check if errors InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> emptyDeck.takeFromDeck() );
    }

    @DisplayName("TEST: Discarding to a full deck")
    @Test
    void discardToFullDeck() {
        // Create an empty deck
        Deck myDeck = new Deck(1);


        // Create 6 cards (deck has max size 5)
        Card card1 = new Card(3);
        Card card2 = new Card(2);
        Card card3 = new Card(5);
        Card card4 = new Card(2);
        Card card5 = new Card(1);
        Card card6 = new Card(5);

        // Discard 5 cards to deck
        myDeck.discardToDeck(card1);
        myDeck.discardToDeck(card2);
        myDeck.discardToDeck(card3);
        myDeck.discardToDeck(card4);
        myDeck.discardToDeck(card5);

        //Discard 6th card to full deck and check if errors InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> myDeck.discardToDeck(card6) );
    }
}
