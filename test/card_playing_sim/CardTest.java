package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @DisplayName("TEST: Card value getter")
    @Test
    void getCardValue() {
        int my_number = 5;
        Card myCard = new Card(my_number);
        assertEquals(my_number, myCard.getValue());
    }
}