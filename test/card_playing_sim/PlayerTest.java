package card_playing_sim;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest {
    private MockGameController mockGameController;

    /**
     * Utility to create a player holding a hand of the given card values
     * @param values Array of values to set each card to in-order given
     * @param playerValue Unique value to assign player (e.g. Player 1)
     * @return The created player with it's created hand of cards
     */
    private Player createPlayerWithHand(int[] values, int playerValue) {
        Player player = new Player(playerValue, mockGameController);
        for (int val : values) {
            player.giveCard(new Card(val));
        }
        return player;
    }

    /**
     * Utility to create a player holding a hand of the given card values
     * Sets default player value of 1
     * @param values Array of values to set each card to in-order given
     * @return The created player with it's created hand of cards
     */
    private Player createPlayerWithHand(int[] values) {
        return createPlayerWithHand(values, 1);
    }

    @DisplayName("TEST: Overfilling a player's hand")
    @Test
    void overfillPlayerHand() {
        // Create a fresh mock game controller for player interaction
        this.mockGameController = new MockGameController("testing_logs/");

        // Create a player with an initial full hand of 4 cards
        int[] initialCards = {1,5,2,7};
        Player player = createPlayerWithHand(initialCards);

        // Create a 5th card
        Card card5 = new Card(2);

        // Give player one too many cards, and check for InvalidOperation error
        assertThrows(InvalidOperationException.class, () -> player.giveCard(card5));
    }
}
