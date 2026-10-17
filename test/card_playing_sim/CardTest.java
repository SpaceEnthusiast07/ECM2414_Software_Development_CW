package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

    @DisplayName("TEST: Get card value")
    @Test
    void getCardValue() {
        // Create a card with value 5
        int myNumber = 5;
        Card myCard = new Card(myNumber);

        // Call getter and check returned value is 5
        assertEquals(myNumber, myCard.value());
    }
}