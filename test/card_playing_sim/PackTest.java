package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PackTest {

    @DisplayName("TEST: adding and taking from pack")
    @Test
    void packAddAndTake() {
        // Create empty pack of fixed-length 3
        Pack myPack = new Pack(3);

        // Create 3 cards of different values (1,3,2)
        Card card1 = new Card(1);
        Card card2 = new Card(3);
        Card card3 = new Card(2);

        // Insert cards sequentially at bottom of pack
        myPack.addCardToBottom(card2);
        myPack.addCardToBottom(card3);
        myPack.addCardToBottom(card1);

        // Take the cards off the top and check they are same order added in
        assertEquals(card2.getValue(), myPack.takeTopCard().getValue());
        assertEquals(card3.getValue(), myPack.takeTopCard().getValue());
        assertEquals(card1.getValue(), myPack.takeTopCard().getValue());
    }

    @DisplayName("TEST: taking from an empty pack")
    @Test
    void takeFromEmptyPack() {
        // Create an empty pack
        Pack emptyPack = new Pack(1);

        //Take from empty pack and check if errors InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> emptyPack.takeTopCard() );
    }

    @DisplayName("TEST: adding to a full pack")
    @Test
    void addToFullPack() {
        // Create 5 cards
        Card card1 = new Card(3);
        Card card2 = new Card(2);
        Card card3 = new Card(5);
        Card card4 = new Card(2);
        Card card5 = new Card(1);

        // Create a pack with the 5 cards (setting max of 5)
        Card[] myCards = {card1, card2, card3, card4, card5};
        Pack myPack = new Pack(myCards);

        // Create an additional card
        Card card6 = new Card(5);

        //Add 6th card to full pack and check if errors InvalidOperationException
        assertThrows(InvalidOperationException.class, () -> myPack.addCardToBottom(card6) );
    }
}
