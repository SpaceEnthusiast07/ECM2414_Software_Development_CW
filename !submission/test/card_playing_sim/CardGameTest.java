package card_playing_sim;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CardGameTest {

    /**
     * A set of tests to check the readUserInput() method, by passing a custom byte stream instead of system in/out
     * Compares line-by-line expected output and actual output for prompts given user inputs
     */
    @Nested
    class InputPromptTests {
        // Default number of players to use in tests (for consistency)
        private final int NUM_PLAYERS = 4;
        // Temporary isolated directory to store files in for each individual test fresh
        private Path tempDir;
        // Completely fresh valid pack file to use each test
        private Path randomValidPackFile;

        // Prompt and Error messages printed during readUserInput();
        private final String PLEASE_ENTER_PLAYER_NUMBER_MESSAGE = "Please enter the number of players: ";
        private final String PLAYER_NUM_GREATER_THAN_ONE_ERROR = "ERROR: Number of players must be an Integer greater than 1.";
        private final String PLEASE_ENTER_PACK_LOCATION_MESSAGE = "Please enter location of pack to load: ";
        private final String PACK_FILE_NOT_EXIST_ERROR = "ERROR: Pack file does not exist.";
        private final String PACK_VALUE_NOT_INTEGER_ERROR = "ERROR: At least 1 card value in the provided pack is not an Integer.";

        /**
         * Runs the CardGame's user inputs as a mock game using streams instead of console
         * @param inputs String array of inputs to be streamed, line by line
         * @return The buffered outputs as a String array, one element per prompt, separated by double newlines "\n\n"
         */
        private String[] getOutputs(String[] inputs) {
            String inputString = String.join("\n", inputs) + "\n";

            // Convert the inputString to a byte array input stream (using UTF-8 formatting)
            InputStream playerCountsAsStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));

            // Create a response stream and buffer from the PrintStream output (using UTF-8 formatting)
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream responses = new PrintStream(buffer, true, StandardCharsets.UTF_8);

            // Create a mock card game, using the streams instead of console
            CardGame MockGameState = new CardGame(playerCountsAsStream, responses, this.tempDir);

            // Run only the input prompts
            MockGameState.readUserInput();

            // Now prompts finished, flush the response stream to the buffer
            responses.flush();

            // Get the buffer as a string (using UTF-8 formatting)
            String outputString = buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");

            // Split the output into string elements by double newline (and ignore leading newline)
            return outputString.stripLeading().split("\n\n");
        }



        /**
         * Before each test, create a unique temporary directory for files
         * and creates a fresh randomised valid pack file
         * @throws IOException If temp directory or pack file creation fails
         */
        @BeforeEach
        void setUpTempDirAndPackFile() throws IOException {
            this.tempDir = Files.createTempDirectory("inputPromptTests_");
            this.randomValidPackFile = TestUtils.generateValidPackFile(
                    this.tempDir, "randomValidPackFile.txt", this.NUM_PLAYERS);

        }

        /**
         * After each test, delete both the generated pack file and the directory itself
         * and force reset the paths to null
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

            // Force reset paths to null
            this.randomValidPackFile = null;
            this.tempDir = null;
        }

        @DisplayName("Invalid player counts log error and reprompt until valid")
        @Test
        void invalidPlayerCountReprompt() {
            // Create 3 invalid input cases for player count prompt, and a guaranteed valid playerNum
            // and packFile prompts (to complete readUserInput())
            String[] inputs = {
                    "five",  // Invalid Input (not int)
                    "1",     // Invalid Input (not >1)
                    "-5",    // Invalid Input (negative)
                    Integer.toString(NUM_PLAYERS),          // Valid Input, ends player count prompt
                    this.randomValidPackFile.toString()     // Valid packFile, ends the packFile prompt
            };

            // List the expected outputs for each given input
            String[] expectedOutputs = {
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR, // five is not int
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR, // 1 is not >1
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR, // -5 is not >1
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + "\n" + PLEASE_ENTER_PACK_LOCATION_MESSAGE // Valid input, progress to packfile prompt
                    // No error response if correct pack file input
            };

            // Run the prompts and return the string outputs from PrintStream
            String[] actualOutputs = getOutputs(inputs);

            // Check all values in expected outputs are equal to all values in actual outputs
            assertArrayEquals(expectedOutputs, actualOutputs);
        }

        @DisplayName("Valid inputs immediately succeed without any reprompting")
        @Test
        void validInputsImmediatelySucceed() {
            // Create 2 perfectly valid inputs for both player num and pack file
            String[] inputs = {
                    Integer.toString(NUM_PLAYERS),          // Valid Input, ends player count prompt
                    this.randomValidPackFile.toString()     // Valid packFile, ends the packFile prompt
            };

            // List the expected outputs for each given input
            String[] expectedOutputs = {
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + "\n" + PLEASE_ENTER_PACK_LOCATION_MESSAGE // Valid input, progress to packfile prompt
                    // No error response as correct pack file input
            };

            // Run the prompts and return the string outputs from PrintStream
            String[] actualOutputs = getOutputs(inputs);

            // Check all values in expected outputs are equal to all values in actual outputs
            assertArrayEquals(expectedOutputs, actualOutputs);
        }

        @DisplayName("Invalid pack files log error and reprompt until valid")
        @Test
        void invalidPackFileReprompt() throws IOException {
            // Generate a pack file with invalid contents
            String[] invalidPackLines = {"1", "five", "2", "4"};
            Path invalidPackFile = TestUtils.writePackFile(this.tempDir, "invalidPack.txt", invalidPackLines);

            // Create input cases for different scenarios and edge cases for pack file prompt
            // As well as a guaranteed playerNum input and final valid packFile input, to complete readUserInput()
            String[] inputs = {
                    Integer.toString(NUM_PLAYERS),          // Valid Input, ends player count prompt
                    "missingFile",                          // Invalid packFile, doesn't exist
                    "randomValidPackFile",                  // Invalid packFile, no file extension
                    invalidPackFile.toString(),             // Invalid packFile, contains non-ints
                    this.randomValidPackFile.toString()     // Valid packFile, ends the packFile prompt
            };

            // List the expected outputs for each given input
            String[] expectedOutputs = {
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + "\n" +
                        PLEASE_ENTER_PACK_LOCATION_MESSAGE + PACK_FILE_NOT_EXIST_ERROR, // Valid player count (progresses), but missing packFile (errors)
                    PLEASE_ENTER_PACK_LOCATION_MESSAGE + PACK_FILE_NOT_EXIST_ERROR, // Missing packFile (errors)
                    PLEASE_ENTER_PACK_LOCATION_MESSAGE + PACK_VALUE_NOT_INTEGER_ERROR, // Invalid packfile (errors)
                    PLEASE_ENTER_PACK_LOCATION_MESSAGE  // No error response as correct pack file input
            };

            // Run the prompts and return the string outputs from PrintStream
            String[] actualOutputs = getOutputs(inputs);

            // Check all values in expected outputs are equal to all values in actual outputs
            assertArrayEquals(expectedOutputs, actualOutputs);

        }
    }

    @Nested
    @Tag("integration")
    class CardGameIntegrationTest {
        // Temporary directory where test logfiles are sent to
        private Path tempDir;

        /**
         * Before each test, create a unique temporary directory for log files
         * @throws IOException If temp directory creation fails
         */
        @BeforeEach
        void setUpTempDir() throws IOException {
            // Create temporary logfile directory
            tempDir = Files.createTempDirectory("cardGameIntegrationTests_");
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
         * Runs a complete game, failing if it takes over 10 seconds
         * @param numPlayers The number of players, n
         * @throws IOException If the pack file can't be written
         */
        private void runGame(int numPlayers, Path packFile) throws IOException {
            // Answer the two prompts: number of players and pack file location
            String input = numPlayers + "\n" + packFile + "\n";

            // Create an input stream and null output stream (ignore output here)
            InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            PrintStream out = new PrintStream(OutputStream.nullOutputStream());

            // Create a card game simulation
            CardGame game = new CardGame(in, out, this.tempDir);

            // Run the startSim() and check it completes under 10 seconds (performance)
            assertTimeoutPreemptively(Duration.ofSeconds(10), () -> game.startSim());
        }

        /**
         * Reads every line of a log file in the temp directory
         * @param fileName The log file name (e.g. player1_output.txt)
         * @return The lines of the file in order
         * @throws IOException If the file fails to be read
         */
        private String[] readLog(String fileName) throws IOException {
            // Read all lines of file and covert from String list to String array
            return Files.readAllLines(this.tempDir.resolve(fileName)).toArray(new String[0]);
        }

        /**
         * Gets the card values from a log line, starting from a given word
         * @param line The log line to read card values from
         * @param fromWord the index of the first card value word
         * @return The card values from that word to the end of the line
         */
        private int[] cardsFrom(String line, int fromWord) {
            // Split the line into its individual words
            String[] words = line.split(" ");

            // Every word from fromWord onwards is a card value
            int[] values = new int[words.length - fromWord];
            for (int i = fromWord; i < words.length; i++) {
                values[i - fromWord] = Integer.parseInt(words[i]);
            }

            return values;
        }

        // If there is an immediate win, it should detect this and immediately end game and log only 4 files
        @DisplayName("Immediate win for one winner")
        @RepeatedTest(20)  // Another player moving first is a timing race, so repeat to catch it
        void immediateWin() throws IOException {

            // Create a pack where player 2 instantly wins (initial hand 2,2,2,2)
            String[] pack = {
                "10", "2", "12", "13", "14", "2", "16", "17",
                "18", "2", "20", "21", "22", "2", "24", "25",
                "26", "27", "28", "29", "30", "31", "32", "33",
                "34", "35", "36", "37", "38", "39", "40", "41"
            };

            Path packFile = TestUtils.writePackFile(this.tempDir, "pack.txt", pack);

            runGame(4, packFile);

            String[] expectedWinnerLog = {
                    "player 2 initial hand 2 2 2 2",
                    "player 2 wins",
                    "player 2 exits",
                    "player 2 final hand: 2 2 2 2"
            };

            // Check winner logs as expected in correct format
            assertArrayEquals(expectedWinnerLog, readLog("player2_output.txt"));

            // Check every player ONLY logs 4 lines (no moves)
            for (int playerNum = 1; playerNum <= 4; playerNum++) {
                String logFileName = "player" + playerNum + "_output.txt";
                assertEquals(4, readLog(logFileName).length);
            }

            String[] deck1ExpectedLog = {
                    "deck1 contents: 26 30 34 38"
            };

            // Check deck 1 still holds initial cards (no moves)
            assertArrayEquals(deck1ExpectedLog, readLog("deck1_output.txt"));
        }

        @DisplayName("Fixed pack games end with one winner, 4-card hands, no cards lost")
        @RepeatedTest(20) // Use the same pack each time but thread timing and random discards make games different
        void fixedPackGameInvariants() throws IOException {
            final int NUM_PLAYERS = 4;
            // 8 of each value 1 to 4 (with no immediate winning hands)
            String[] pack = {
                    "1", "2", "3", "4", "2", "3", "4", "1",
                    "3", "4", "1", "2", "4", "1", "2", "3",
                    "2", "3", "4", "1", "3", "4", "1", "2",
                    "4", "1", "2", "3", "1", "2", "3", "4"
            };

            // Write pack lines to pack file
            Path packFile = TestUtils.writePackFile(this.tempDir, "pack.txt", pack);

            // Play a game with this pack
            runGame(NUM_PLAYERS, packFile);

            // Check the invariants of the simulation, comparing before and after
            assertGameInvariants(NUM_PLAYERS, pack);
        }


        @DisplayName("Generated pack games end with one winner, 4-card hands, no cards lost")
        @RepeatedTest(20) // Use a newly generated valid pack file each time
        void generatedPackGameInvariants() throws IOException {
            final int NUM_PLAYERS = 4;

            // Generate a random valid pack file
            Path packFile = TestUtils.generateValidPackFile(this.tempDir, "pack.txt", NUM_PLAYERS);

            // Get the raw pack lines of the pack file
            String[] pack = readLog("pack.txt");

            // Play a game with this pack
            runGame(NUM_PLAYERS, packFile);

            // Check the invariants of the simulation, comparing before and after
            assertGameInvariants(NUM_PLAYERS, pack);
        }


        /**
         * Check that the internal conditions of the class remains the safe no matter what
         * e.g. that the number of cards remains the same before and after, and that only one player wins
         * @param NUM_PLAYERS The number of players, n
         * @param packLines The raw lines of the packfile containing card values
         * @throws IOException If a log file fails to be read
         */
        void assertGameInvariants(int NUM_PLAYERS, String[] packLines) throws IOException {
            final int DISCARDED_INDEX_IN_LOGSTRING = 4;
            final int HAND_CARDS_FROMWORD_INDEX = 5;

            // Collect all the cards held by a player or deck at the end of game
            // And compare to cards at start to check total counts match
            int[] endCards = new int[8 * NUM_PLAYERS];
            int numEndCards = 0;

            // Count how many players declared a win in their own log file
            int numWinners = 0;

            // Check each player's log file
            for (int playerNum = 1; playerNum <= NUM_PLAYERS; playerNum++) {
                String[] log = readLog("player" + playerNum + "_output.txt");

                // Check every line this player logged during the game
                for (String line : log) {
                    // Count player win logs (validate only 1 winner)
                    if (line.equals("player " + playerNum + " wins")) {
                        numWinners++;
                    }

                    // Check never discarded a preferred card value
                    if (line.startsWith("player " + playerNum + " discards")) {
                        int discardedCardVal = Integer.parseInt(
                                line.split(" ")[DISCARDED_INDEX_IN_LOGSTRING]);
                        assertNotEquals(playerNum, discardedCardVal);
                    }

                    // Check there are always 4 cards in hand
                    if (line.startsWith("player " + playerNum + "current hand is")) {
                        int handLength = cardsFrom(line, HAND_CARDS_FROMWORD_INDEX).length;
                        assertEquals(4, handLength);
                    }
                }

                // Get the final hand card values (always the last 4 numbers)
                String finalHandLine = log[log.length - 1];
                int numWords = finalHandLine.split(" ").length;
                for (int value : cardsFrom(finalHandLine, numWords - 4)) {
                    endCards[numEndCards] = value;
                    numEndCards++;
                }
            }

            // Check each deck's log file
            for (int deck = 1; deck <= NUM_PLAYERS; deck++) {
                // Deck logs only have 1 line
                String deckLine = readLog("deck" + deck + "_output.txt")[0];
                for (int value : cardsFrom(deckLine, 2)) {
                    endCards[numEndCards] = value;
                    numEndCards++;
                }
            }

            // Check total conservation of cards
            int[] packValues = new int[packLines.length];
            for (int i = 0; i < packLines.length; i++) {
                packValues[i] = Integer.parseInt(packLines[i]);
            }

            // Sort before and after arrays and check equal
            Arrays.sort(packValues);
            Arrays.sort(endCards);
            assertArrayEquals(packValues, endCards);

            // Check that there is only one recorded winner
            assertEquals(1, numWinners);
        }

    }
}
