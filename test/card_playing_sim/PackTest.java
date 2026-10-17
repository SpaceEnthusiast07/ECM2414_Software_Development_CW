package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackTest {

    /**
     * Calculate the number of players, n, needed by a pack (size 8n)
     * @param cardCount A positive number of cards that should be in the pack
     * @return The number of players that pack needs (rounded up)
     */
    private int numPlayersNeededForCards(int cardCount) {
        // Add 7 to get into next 8n interval and perform integer division by 8
        return (cardCount + 7) / 8;
    }

    /**
     * Create a pack full of cards with the given values
     * @param values The numbers that should be on each card, in order
     * @return The created pack with all cards added
     */
    private Pack createPackWithCards(int[] values) {
        // Create empty cards array of values' length
        Card[] cards = new Card[values.length];

        // For each card value, make a card and put in cards array
        for (int i = 0; i < values.length; i++) {
            cards[i] = new Card(values[i]);
        }

        return new Pack(cards);
    }

    @DisplayName("TEST: Adding and taking from pack")
    @Test
    void packAddAndTakeInOrder() {
        // Create empty pack of fixed-length 8 (8*1)
        Pack myPack = new Pack(1);

        // Create 3 cards of different values (4,9,7) - added manually for clearer reading
        Card card1 = new Card(4);
        Card card2 = new Card(9);
        Card card3 = new Card(7);

        // Insert cards sequentially at bottom of pack
        myPack.addCardToBottom(card2);
        myPack.addCardToBottom(card3);
        myPack.addCardToBottom(card1);

        // Take the cards off the top and check they are same order added in
        assertEquals(9, myPack.takeTopCard().value());
        assertEquals(7, myPack.takeTopCard().value());
        assertEquals(4, myPack.takeTopCard().value());
    }

    @DisplayName("TEST: Taking from an empty pack throws")
    @Test
    void takeFromEmptyPackThrows() {
        // Create an empty pack
        Pack emptyPack = new Pack(1);

        //Take from empty pack and check if errors InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> emptyPack.takeTopCard() );
    }

    @DisplayName("TEST: Adding to a full pack throws")
    @Test
    void addToFullPackThrows() {
        // Create a pack with an initial 5 cards
        int[] initialCardVals = {3,2,5,2,1};
        Pack pack = createPackWithCards(initialCardVals);

        // Create an additional card
        Card extraCard = new Card(5);

        //Add 6th card to full pack and check if errors InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> pack.addCardToBottom(extraCard) );
    }
}
