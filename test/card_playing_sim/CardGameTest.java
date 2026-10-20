package card_playing_sim;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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

        // All print statements used by the program:
        private final String PROGRAM_TITLE_MESSAGE = "=== ECM2414 Software Development Coursework ===";
        private final String PROGRAM_SUBTITLE_MESSAGE = "\nMultithreaded Card Playing Sim";
        private final String PLEASE_ENTER_PLAYER_NUMBER_MESSAGE = "Please enter the number of players: ";
        private final String PLAYER_NUM_GREATER_THAN_ONE_ERROR = "ERROR: Number of players must be an Integer greater than 1.";
        private final String PLAYER_NUM_NO_INPUT_ERROR = "ERROR: Please enter a number of players.";
        private final String PLEASE_ENTER_PACK_LOCATION_MESSAGE = "Please enter location of pack to load: ";

        /**
         * Runs the CardGame's user inputs as a mock game using streams instead of console
         * @param inputs String array of inputs to be streamed, line by line
         * @return The buffered outputs as a String array from runtime to exception, separated by double newlines "\n\n"
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

            // Get the buffer as a string array (using UTF-8 formatting)
            String outputString = buffer.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");;

            // Split the output into string elements by double newline (and ignore leading newline)
            return outputString.stripLeading().split("\n\n");
        }

        /**
         * Before each test, create a unique temporary directory for files
         * and creates a fresh randomised valid pack file
         * @throws IOException If temp directory or pack file creation fails
         */
        @BeforeEach
        void setUpTempDirAndRandPackFile() throws IOException {
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
        void cleanUpTempDirAndRandPackFile() throws IOException {
            Files.delete(this.randomValidPackFile);
            Files.delete(this.tempDir);

            this.randomValidPackFile = null;
            this.tempDir = null;
        }

        @DisplayName("Invalid player counts reprompt until valid")
        @Test
        void invalidPlayerCountReprompt() throws IOException {
            // Create 4 input cases for different scenarios and edge cases for player count prompt
            // As well as 2 guaranteed valid pack file prompts (to complete the readUserInput() method)
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
        void validInputsImmediatelySucceed() throws IOException {
            // Create 2 perfectly valid inputs for both player num and pack file
            String[] inputs = {
                    Integer.toString(NUM_PLAYERS),          // Valid Input, ends player count prompt
                    this.randomValidPackFile.toString()     // Valid packFile, ends the packFile prompt
            };

            // List the expected outputs for each given input
            String[] expectedOutputs = {
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + "\n" + PLEASE_ENTER_PACK_LOCATION_MESSAGE // Valid input, progress to packfile prompt
                    // No error response if correct pack file input
            };

            // Run the prompts and return the string outputs from PrintStream
            String[] actualOutputs = getOutputs(inputs);

            // Check all values in expected outputs are equal to all values in actual outputs
            assertArrayEquals(expectedOutputs, actualOutputs);
        }

        // TODO: write test
        @Test
        void missingPackFileReprompt() {

        }

        // TODO: write test
        @Test
        void invalidPackContentsReprompt() {

        }
    }

    // TODO: Integration tests
    // TODO: Deterministic tests of whole system
    // TODO: handsDealtRoundRobin
    // TODO: decksFilledRoundRobin

    // TODO: bunch of random repeated tests and analyse output:
    // TODO: exactly one wins, and all log files show this
    // TODO: all log files follow spec, always discards then draws, always hand of 4 cards
    // TODO: No player discards its preferred value
    // TODO: player doesn't hold onto same card forever
    // TODO: no cards ever lost or gained (pack file matches final hand sets)

    // TODO: Setup test suite of integration tests and individual unit tests

    // TODO: Performance tests?? eg test completes within a timeout

}
