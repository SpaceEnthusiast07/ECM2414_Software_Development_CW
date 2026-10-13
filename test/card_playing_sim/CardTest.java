package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {

    @DisplayName("TEST: Card value getter")
    @Test
    void getCardValue() {
        // Create a card with value 5
        int my_number = 5;
        Card myCard = new Card(my_number);

        // Call getter and check returned value is 5
        assertEquals(my_number, myCard.getValue());
    }
}