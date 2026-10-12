package card_playing_sim;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardGameTest {

    @Test
    void missingPackFilepath() {
        CardGame myCardGame = new CardGame();
        assertNull(myCardGame.readPackFile("missingfile"));
    }
}
