package card_playing_sim;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerTest {
    // Mock object to control player interaction with mock game state during tests
    private MockGameController mockGameController;

    // Temporary directory where test logfiles are sent to
    private Path tempDir;

    /**
     * Before each test, create a unique temporary directory for log files
     * and creates a fresh mock game controller object with that directory
     * @throws IOException If temp directory creation fails
     */
    @BeforeEach
    void setUpControllerAndLogDir() throws IOException {
        // Create temporary logfile directory
        tempDir = Files.createTempDirectory("cardgame_test");
        // Instantiate a new mock game controller instance
        mockGameController = new MockGameController(tempDir);
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
        try (Stream<Path> files = Files.list(tempDir)) {
            for (Path file : files.toList()) {
                Files.delete(file);
            }
        }
        // Then delete the directory itself
        Files.delete(tempDir);
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

    /**
     * Utility to create a deck holding the given card values (FIFO)
     * @param deckValue Unique value to assign the deck
     * @param values Card values to discard to the deck in order
     * @return The created deck
     */
    private Deck createDeckWithCards(int deckValue, int[] values) {
        // Create a new deck with the assigned value
        Deck deck = new Deck(deckValue);

        // Sequentially add each card to deck in order
        for (int value : values) {
            deck.discardToDeck(new Card(value));
        }
        return deck;
    }

    /**
     * Utility to read every line of player 1's log file
     * @return The String array of all lines in log file
     * @throws IOException If the log file can't be read
     */
    private String[] readPlayerOneLog() throws IOException {
        // Read all lines of file and covert from String list to Strign array
        return Files.readAllLines(this.tempDir.resolve("player1_output.txt")).toArray(new String[0]);
    }


    @Nested
    class WinningHandsTests {
        @DisplayName("Four of the player's preferred value wins")
        @Test
        void fourPreferredCardsWin() {
            Player player = createPlayerWithHand(new int[]{1, 1, 1, 1});

            assertTrue(player.isPlayerWon());
        }

        @DisplayName("Four of the player's non-preferred value wins")
        @Test
        void fourNonPreferredCardsWin() {
            Player player = createPlayerWithHand(new int[]{7, 7, 7, 7});

            assertTrue(player.isPlayerWon());
        }

        @DisplayName("Only three of the player's preferred value does not win")
        @Test
        void threeOfAKindDoesNotWin() {
            Player player = createPlayerWithHand(new int[]{1, 1, 1, 5});

            assertFalse(player.isPlayerWon());
        }
    }

    @Nested
    class LogFileTests {
        @DisplayName("Initial hand is logged correctly")
        @Test
        void initialHandLogFormat() throws IOException {
            // Create player with simple hand
            Player player = createPlayerWithHand(new int[]{1, 5, 2, 7});

            // Log player hand to log file
            player.logInitialHand();

            String[] expectedLog = {
                    "player 1 initial hand 1 5 2 7"
            };

            // Check that initial log hand matches expected format
            assertArrayEquals(expectedLog, readPlayerOneLog());


        }

        @DisplayName("Loser logs winner, final hand and exit message")
        @Test
        void loserLogFormat() throws IOException {
            // Create player with simple hand
            Player player = createPlayerWithHand(new int[]{1, 5, 2, 7});

            // Set player's left/right decks empty to prevent discard/draw
            player.setLeftDeck(createDeckWithCards(1, new int[0]));
            player.setRightDeck(createDeckWithCards(2, new int[0]));

            // Simulate player 3 winning (ending game)
            mockGameController.setWinningPlayerValue(3);
            player.run();

            String[] expectedLog = {
                    "player 3 has informed player 1 that player 3 has won",
                    "player 1 exits",
                    "player 1 hand: 1 5 2 7"
            };

            // Check that loser logs match expected format
            assertArrayEquals(expectedLog, readPlayerOneLog());

            // Check that player called that it exited
            assertTrue(mockGameController.wasPlayerExitedCalled());

        }
    }

    @Nested
    class MakeMoveTests {

        @DisplayName("Single move draws from left, discards non-preferred to right, then wins")
        @Test
        void singleMoveThenWin() throws IOException {
            // Create player 1 who needs just one more 1
            Player player = createPlayerWithHand(new int[] {1, 1, 1, 5});

            // Put only 1 in left deck so it definitely takes it
            Deck leftDeck = createDeckWithCards(1, new int[] {1});
            Deck rightDeck = createDeckWithCards(2, new int[0]);
            player.setLeftDeck(leftDeck);
            player.setRightDeck(rightDeck);

            // Simulate run on test thread
            player.run();

            // Expected logs given the player discards non-preferred, draws 1, and wins
            String[] expectedLog = {
                    "player 1 draws a 1 from deck 1",
                    "player 1 discards a 5 to deck 2",
                    "player 1 current hand is 1 1 1 1",
                    "player 1 wins",
                    "player 1 exits",
                    "player 1 final hand: 1 1 1 1"
            };

            // Check that loser logs match expected format
            assertArrayEquals(expectedLog, readPlayerOneLog());

            // Check that left deck is now empty and right deck has discarded 5
            assertFalse(leftDeck.canTakeFromDeck());
            assertEquals(5, rightDeck.takeFromDeck().value());

            // Check that the winning value matches the player number
            assertEquals(1, mockGameController.getWinningPlayerValue());

            // Check that player called that it exited
            assertTrue(mockGameController.wasPlayerExitedCalled());
        }

        @DisplayName("Player never discards its preferred card value")
        @RepeatedTest(20)
        void neverDiscardsPreferredCard() throws IOException {
            // Create player 1 with one of their preferred cards
            Player player = createPlayerWithHand(new int[] {1, 2, 3, 4});

            // Give player 1 card to draw
            Deck leftDeck = createDeckWithCards(1, new int[] {5});
            Deck rightDeck = createDeckWithCards(2, new int[0]);
            player.setLeftDeck(leftDeck);
            player.setRightDeck(rightDeck);

            // Player can't win, so end the game after 10 checks
            mockGameController.endGameAfterChecks(10,2);
            player.run();

            // Get the player's log of their current hand and check always 4 cards
            String playerHandLog = readPlayerOneLog()[2];
            String[] hand = playerHandLog.replace("player 1 current hand is", "").trim().split(" ");
            assertEquals(4, hand.length);

            // Check that player never discards 1 (preferred card)
            assertNotEquals(1, rightDeck.takeFromDeck().value());

            // Check that there should be 0 cards in left deck (1 taken)
            assertFalse(leftDeck.canTakeFromDeck());
        }


        @DisplayName("No move is made when the left deck is empty")
        @Test
        void noMoveWhenLeftDeckEmpty() throws IOException {
            // Create player with simple hand
            Player player = createPlayerWithHand(new int[] {1, 2, 3, 4});

            // Make an empty left and right deck
            Deck leftDeck = createDeckWithCards(1, new int[0]);
            Deck rightDeck = createDeckWithCards(2, new int[0]);
            player.setLeftDeck(leftDeck);
            player.setRightDeck(rightDeck);

            // Player can't win, so end the game after 10 checks
            mockGameController.endGameAfterChecks(10,2);
            player.run();

            // Player makes no moves, so only has end-game logs
            String[] expectedLog = {
                    "player 2 has informed player 1 that player 2 has won",
                    "player 1 exits",
                    "player 1 hand: 1 2 3 4"
            };

            // Check that player has made no moves in 10 checks
            assertArrayEquals(expectedLog, readPlayerOneLog());

            // Check that no cards exist in right deck (nothing discarded)
            assertFalse(rightDeck.canTakeFromDeck());
        }

        @DisplayName("No move is made when the left deck is empty")
        @Test
        void noMoveWhenRightDeckFull() throws IOException {
            // Create player with simple hand
            Player player = createPlayerWithHand(new int[] {1, 2, 3, 4});

            // Make a full right deck (5 cards) so player can't discard
            Deck leftDeck = createDeckWithCards(1, new int[] {5});
            Deck rightDeck = createDeckWithCards(2, new int[] {2, 6, 6, 6, 6});
            player.setLeftDeck(leftDeck);
            player.setRightDeck(rightDeck);

            // Player can't win, so end the game after 10 checks
            mockGameController.endGameAfterChecks(10,2);
            player.run();

            // Player makes no moves, so only has end-game logs
            String[] expectedLog = {
                    "player 2 has informed player 1 that player 2 has won",
                    "player 1 exits",
                    "player 1 hand: 1 2 3 4"
            };

            // Check that player has made no moves in 10 checks
            assertArrayEquals(expectedLog, readPlayerOneLog());

            // Check that a card exists in left deck (nothing drawn)
            assertTrue(leftDeck.canTakeFromDeck());
        }
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
}
