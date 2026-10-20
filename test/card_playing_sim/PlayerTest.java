package card_playing_sim;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest {
    // Mock object to control player interaction with mock game state during tests
    private MockGameController mockGameController;

    // Temporary directory where test logfiles are sent to
    private Path logDir;

    /**
     * Before each test, create a unique temporary directory for log files
     * and creates a fresh mock game controller object with that directory
     * @throws IOException If temp directory creation fails
     */
    @BeforeEach
    void setUpControllerAndLogDir() throws IOException {
        // Create temporary logfile directory
        logDir = Files.createTempDirectory("cardgame_test");
        // Instantiate a new mock game controller instance
        mockGameController = new MockGameController(logDir);
    }

    /**
     * After each test, reset the mock game controller object to null
     * and then delete every file in the temp directory, before deleting the directory itself
     * Prevents any test data in log files or game state remaining between tests
     * @throws IOException If file/directory deletion fails
     */
    @AfterEach
    void cleanUpControllerAndLogDir() throws IOException {
        // Force null the mock game controller
        mockGameController = null;

        // List all file in the log folder using a stream and delete each one
        try (Stream<Path> files = Files.list(logDir)) {
            for (Path file : files.toList()) {
                Files.delete(file);
            }
        }
        // Then delete the directory itself
        Files.delete(logDir);
    }

    /**
     * Utility to create a player holding a hand of the given card values
     * @param values Array of values to set each card to in-order given
     * @param playerValue Unique value to assign player (e.g. Player 1)
     * @return The created player with it's created hand of cards
     */
    private Player createPlayerWithHand(int[] values, int playerValue) {
        Player player = new Player(playerValue, this.mockGameController);

        // Create and give cards to player hand in order, using values
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

    // ========= Instant Winning Hands =========

    @DisplayName("Four of the player's preferred value wins")
    @Test
    void fourPreferredCardsWin() {
        Player player = createPlayerWithHand(new int[] {1,1,1,1});

        assertTrue(player.isPlayerWon());
    }

    @DisplayName("Four of the player's non-preferred value wins")
    @Test
    void fourNonPreferredCardsWin() {
        Player player = createPlayerWithHand(new int[] {7,7,7,7});

        assertTrue(player.isPlayerWon());
    }

    @DisplayName("Only three of the player's preferred value does not win")
    @Test
    void threeOfAKindDoesNotWin() {
        Player player = createPlayerWithHand(new int[] {1,1,1,5});

        assertFalse(player.isPlayerWon());
    }

    @DisplayName("Giving to a player's full hand throws")
    @Test
    void giveCardToFullHandThrows() {

        // Create a player with an initial full hand of 4 cards
        int[] initialCardVals = {1,5,2,7};
        Player player = createPlayerWithHand(initialCardVals);

        // Create a 5th card
        Card card5 = new Card(2);

        // Give player one too many cards, and check for InvalidOperation error
        assertThrows(InvalidOperationException.class, () -> player.giveCard(card5));
    }

    // TODO: InstantWIn
    // TODO: SingleMoveThenWin
    // TODO: parameterised repeated tests of batch runs
}
