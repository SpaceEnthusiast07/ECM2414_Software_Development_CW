package card_playing_sim;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

    /**
     * Utility to create a deck with the given card values
     * @param values Array of values to set each card to in-order given
     * @param deckValue Unique value to assign deck (e.g. Deck 1)
     * @return The created deck with it's created cards
     */
    private Deck createDeckWithCardValues(int[] values, int deckValue) {
        Deck deck = new Deck(deckValue);

        // Create and discard cards to deck in order, using values
        for (int val : values) {
            deck.discardToDeck(new Card(val));
        }
        return deck;
    }

    /**
     * Utility to create a deck with the given card values
     * Sets default deck value of 1
     * @param values Array of values to set each card to in-order given
     * @return TThe created deck with it's created cards
     */
    private Deck createDeckWithCardValues(int[] values) {
        return createDeckWithCardValues(values, 1);
    }

    @Nested
    class DeckActionTests {
        @DisplayName("Discarding and taking from deck in FIFO order")
        @Test
        void takeCardsReturnsInOrderDiscarded() {
            // Create 3 cards of different values (1,3,2)
            Card card1 = new Card(3);
            Card card2 = new Card(2);
            Card card3 = new Card(5);

            // Create an empty deck
            Deck deck = new Deck(1);

            // Discard 2 cards to deck
            deck.discardToDeck(card2);
            deck.discardToDeck(card3);

            // Take 1 card from deck and check is value of first card discarded
            assertEquals(card2.value(), deck.takeFromDeck().value());

            // Discard the last card to deck
            deck.discardToDeck(card1);

            // Take both remaining cards from deck and check values are in FIFO order
            assertEquals(card3.value(), deck.takeFromDeck().value());
            assertEquals(card1.value(), deck.takeFromDeck().value());
        }

        @DisplayName("Taking from an empty deck throws")
        @Test
        void takeFromEmptyDeckThrows() {
            // Create an empty deck
            Deck emptyDeck = new Deck(1);

            //Take from empty deck and check if errors InvalidOperationException
            assertThrows(InvalidOperationException.class, () -> emptyDeck.takeFromDeck());
        }

        @DisplayName("Discarding to a full deck throws")
        @Test
        void discardToFullDeckThrows() {
            // Create a full deck with an initial 5 cards
            int[] initialVals = {3, 2, 5, 2, 1};
            Deck deck = createDeckWithCardValues(initialVals);

            // Create 6th cards (deck has max size 5)
            Card extraCard = new Card(5);

            //Discard 6th card to full deck and check if errors InvalidOperationException
            assertThrows(InvalidOperationException.class, () -> deck.discardToDeck(extraCard));
        }
    }

    @Nested
    class LoggingTests {
        // Temporary directory where test logfiles are sent to
        private Path tempDir;

        /**
         * Before each test, create a unique temporary directory for log files
         * @throws IOException If temp directory creation fails
         */
        @BeforeEach
        void setUpTempDir() throws IOException {
            // Create temporary logfile directory
            tempDir = Files.createTempDirectory("deckLoggingTests_");
        }

        /**
         * After each test, delete every file in the temp directory, before deleting the directory itself
         * Prevents any test data in log files or game state remaining between tests
         * @throws IOException If file/directory deletion fails
         */
        @AfterEach
        void cleanUpTempDir() throws IOException {
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
         * Utility to read every line of deck 1's log file
         * @return The String array of all lines in log file
         * @throws IOException If the log file can't be read
         */
        private String[] readDeckOneLog() throws IOException {
            // Read all lines of file and covert from String list to String array
            return Files.readAllLines(this.tempDir.resolve("deck1_output.txt")).toArray(new String[0]);
        }

        @DisplayName("Final deck contents are logged in correct format")
        @Test
        void finalContentsLogFormat() throws IOException {
            // Create simple deck
            Deck deck = createDeckWithCardValues(new int[] {1, 3, 3, 7});

            // Log Deck contents
            deck.logFinalDeckContents(this.tempDir);

            String[] expectedLog = {
                    "deck1 contents: 1 3 3 7"
            };

            // Check deck log matches card values
            assertArrayEquals(expectedLog, readDeckOneLog());
        }

        @DisplayName("An empty deck is logged with no card values")
        @Test
        void emptyDeckLogFormat() throws IOException {
            // Create empty deck
            Deck deck = createDeckWithCardValues(new int[] {});

            // Log Deck contents
            deck.logFinalDeckContents(this.tempDir);

            String[] expectedLog = {
                    "deck1 contents:"
            };

            // Check deck log matches empty hand
            assertArrayEquals(expectedLog, readDeckOneLog());
        }

    }
}
