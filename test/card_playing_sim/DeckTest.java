package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeckTest {

    /**
     * Utility to create a deck with the given card values
     * @param values Array of values to set each card to in-order given
     * @param deckValue Unique value to assign deck (e.g. Deck 1)
     * @return The created deck with it's created cards
     */
    private Deck createDeckWithCardValues(int[] values, int deckValue) {
        Deck deck = new Deck(deckValue);

        // Create and discard cards to deck in order, using values
        for (int val : values) {
            deck.discardToDeck(new Card(val));
        }
        return deck;
    }

    /**
     * Utility to create a deck with the given card values
     * Sets default deck value of 1
     * @param values Array of values to set each card to in-order given
     * @return TThe created deck with it's created cards
     */
    private Deck createDeckWithCardValues(int[] values) {
        return createDeckWithCardValues(values, 1);
    }

    @DisplayName("Discarding and taking from deck in FIFO order")
    @Test
    void takeCardsReturnsInOrderDiscarded() {
        // Create 3 cards of different values (1,3,2)
        Card card1 = new Card(3);
        Card card2 = new Card(2);
        Card card3 = new Card(5);

        // Create an empty deck
        Deck deck = new Deck(1);

        // Discard 2 cards to deck
        deck.discardToDeck(card2);
        deck.discardToDeck(card3);

        // Take 1 card from deck and check is value of first card discarded
        assertEquals(card2.value(), deck.takeFromDeck().value());

        // Discard the last card to deck
        deck.discardToDeck(card1);

        // Take both remaining cards from deck and check values are in FIFO order
        assertEquals(card3.value(), deck.takeFromDeck().value());
        assertEquals(card1.value(), deck.takeFromDeck().value());
    }

    @DisplayName("Taking from an empty deck throws")
    @Test
    void takeFromEmptyDeckThrows() {
        // Create an empty deck
        Deck emptyDeck = new Deck(1);

        //Take from empty deck and check if errors InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> emptyDeck.takeFromDeck() );
    }

    @DisplayName("Discarding to a full deck throws")
    @Test
    void discardToFullDeckThrows() {
        // Create a full deck with an initial 5 cards
        int[] initialVals = {3,2,5,2,1};
        Deck deck = createDeckWithCardValues(initialVals);

        // Create 6th cards (deck has max size 5)
        Card extraCard = new Card(5);

        //Discard 6th card to full deck and check if errors InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> deck.discardToDeck(extraCard) );
    }

    // TODO: takeFromDeckBoundaries
    // TODO: discardToDeckBoundaries
    // TODO: finalContentsLogFormat (needs tempDir)
    // TODO: any other tests?
}
