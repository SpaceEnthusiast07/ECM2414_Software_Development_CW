package card_playing_sim;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PackTest {
    // TODO: Setup BeforeEach and AfterEach temporary log directory
    // TODO: Automated pack file creation (random and passed-in vals)

    /**
     * Calculate the number of players, n, needed by a pack (size 8n)
     * @param cardCount A positive number of cards that should be in the pack
     * @return The number of players that pack needs (rounded up)
     */
    private int numPlayersNeededForCards(int cardCount) {
        // Add 7 to get into next 8n interval and perform integer division by 8
        return (cardCount + 7) / 8;
    }

    /**
     * Create a pack full of cards with the given values
     * @param values The numbers that should be on each card, in order
     * @return The created pack with all cards added
     */
    private Pack createPackWithCards(int[] values) {
        // Create empty cards array of values' length
        Card[] cards = new Card[values.length];

        // For each card value, make a card and put in cards array
        for (int i = 0; i < values.length; i++) {
            cards[i] = new Card(values[i]);
        }

        return new Pack(cards);
    }

    @Nested
    class DeckActionTests {
        @DisplayName("Adding and taking from pack")
        @Test
        void takeTopCardReturnsCardsInOrderAdded() {
            // Create empty pack of fixed-length 8 (8*1)
            Pack myPack = new Pack(1);

            // Create 3 cards of different values (4,9,7) - added manually for clearer reading
            Card card1 = new Card(4);
            Card card2 = new Card(9);
            Card card3 = new Card(7);

            // Insert cards sequentially at bottom of pack
            myPack.addCardToBottom(card2);
            myPack.addCardToBottom(card3);
            myPack.addCardToBottom(card1);

            // Take the cards off the top and check they are same order added in
            assertEquals(9, myPack.takeTopCard().value());
            assertEquals(7, myPack.takeTopCard().value());
            assertEquals(4, myPack.takeTopCard().value());
        }

        @DisplayName("Taking from an empty pack throws")
        @Test
        void takeFromEmptyPackThrows() {
            // Create an empty pack
            Pack emptyPack = new Pack(1);

            //Take from empty pack and check if errors InvalidOperationException
            assertThrows(InvalidOperationException.class, () -> emptyPack.takeTopCard() );
        }

        @DisplayName("Adding to a full pack throws")
        @Test
        void addToFullPackThrows() {
            // Create a pack with an initial 5 cards
            int[] initialCardVals = {3,2,5,2,1};
            Pack pack = createPackWithCards(initialCardVals);

            // Create an additional card
            Card extraCard = new Card(5);

            //Add 6th card to full pack and check if errors InvalidOperationException
            assertThrows(InvalidOperationException.class, () -> pack.addCardToBottom(extraCard) );
        }
    }

    @Nested
    class ReadingPackFileTests {
        // Number of players used in every test for consistency
        private final int NUM_PLAYERS = 4;

        // Expected error messages from readPackFile()
        private static final String NEGATIVE_VALUE_ERROR = "ERROR: Pack file must only contain non-negative card values.";
        private static final String NOT_INTEGER_ERROR = "ERROR: At least 1 card value in the provided pack is not an Integer.";
        private static final String TOO_SHORT_ERROR = "ERROR: Pack file is too short, must contain exactly 8n cards.";
        private static final String TOO_MANY_ERROR = "ERROR: Pack file contains too many cards, must contain exactly 8n cards.";
        private static final String FILE_NOT_EXIST_ERROR = "ERROR: Pack file does not exist.";

        // Temporary isolated directory to store files in for each individual test fresh
        private Path tempDir;

        /**
         * Before each test, create a unique temporary directory for files
         * @throws IOException If temp directory or pack file creation fails
         */
        @BeforeEach
        void setUpTempDirAndPackFile() throws IOException {
            this.tempDir = Files.createTempDirectory("readingPackFileTests_");
        }

        /**
         * After each test, delete every file in temporary directory and the directory itself
         * Prevents any test data in log files or game state remaining between tests
         * @throws IOException If file/directory deletion fails
         */
        @AfterEach
        void cleanUpTempDir() throws IOException {
            // List all file in the log folder using a stream and delete each one
            try (Stream<Path> files = Files.list(this.tempDir)) {
                for (Path file : files.toList()) {
                    Files.delete(file);
                }
            }
            Files.delete(this.tempDir);
        }

        /**
         * A copy of a guaranteed valid 32-card pack for 4 players
         * Possible for all players 1-4 to win with a full hand of their preferred card
         * Test operate on same valid pack file for consistency
         * @return All pack lines, top of pack first
         */
        private String[] validLines() {
            return new String[] {
                    "1", "2", "1", "5", "9", "4", "3", "2",
                    "2", "1", "3", "5", "4", "1", "1", "4",
                    "7", "8", "3", "9", "5", "5", "6", "6",
                    "2", "4", "3", "3", "7", "7", "9", "3"
            };
        }

        @DisplayName("Valid pack loads exactly 8n cards in file order")
        @Test
        void validPackLoadsAllCardsInFileOrder() throws IOException {
            // Create a guaranteed valid pack file and write to file
            String[] lines = validLines();
            Path validPackFile = TestUtils.writePackFile(this.tempDir, "validPack.txt", lines);

            // Read that packFile using readPackFile
            Pack pack = Pack.readPackFile(validPackFile.toString(), this.NUM_PLAYERS);

            // For each line we wrote, check that it is the same line taken from top of pack (FIFO order)
            for (String line : lines) {
                assertEquals(Integer.parseInt(line), pack.takeTopCard().value());
            }

            // After checking all lines inputted, check there are no extra cards left in pack
            assertThrows(InvalidOperationException.class, () -> pack.takeTopCard());
        }

        @DisplayName("All non-negative card values allowed")
        @Test
        void nonNegativeCardValuesAllowed() throws IOException {
            // Create a guaranteed valid pack file
            String[] lines = validLines();
            // Ensure all variants/edge cases of non-negative values exist in file
            lines[31] = "0";
            lines[30] = "100000000";
            lines[29] = "1";
            lines[28] = "2";

            // Write lines to pack file
            Path validPackFile = TestUtils.writePackFile(this.tempDir, "validPack.txt", lines);

            // Read that packFile using readPackFile and check it does not throw an error
            assertDoesNotThrow(() -> Pack.readPackFile(validPackFile.toString(), this.NUM_PLAYERS));
        }

        // TODO: packWithNonIntThrows
        // TODO: packWithTrailingNewlineIsValid
        // TODO: missingPackFileThrows
        // TODO: packContainsNegativesThrows
        // TODO: packFileWithLength (parameterised? too short, too long, exactly right)
        // TODO: packFileWithoutWinningHand
    }
}
