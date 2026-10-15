package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest {

    @DisplayName("TEST: Overfilling a player's hand")
    @Test
    void overfillPlayerHand() {
        // Create a new player (no mainProgram needed)
        Player player = new Player(1, null);

        // Create 5 cards (1 too many)
        Card card1 = new Card(1);
        Card card2 = new Card(5);
        Card card3 = new Card(2);
        Card card4 = new Card(7);
        Card card5 = new Card(2);

        // Give player full hand (4 cards)
        player.giveCard(card1);
        player.giveCard(card2);
        player.giveCard(card3);
        player.giveCard(card4);

        // Give player one too many cards, and check for InvalidOperation error
        assertThrows(InvalidOperationException.class, () -> player.giveCard(card5));
    }
}
