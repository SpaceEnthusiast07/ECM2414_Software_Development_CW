package card_playing_sim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PackTest {

    @Test
    void packAddAndTake() {
        // Create empty pack of fixed-length 3
        Pack myPack = new Pack(3);

        // Add 3 cards of different values (1,3,2)
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
}
