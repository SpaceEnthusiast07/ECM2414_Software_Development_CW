package card_playing_sim;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CardGameTest {
    // TODO: setup custom streams with:
        // TODO: invalidPlayerCountReprompt
        // TODO: missingPackFileReprompt
        // TODO: invalidPackContentsReprompt

    @Nested
    class InputPromptTests {
        // Mock object to control player interaction with mock game state during tests
        private MockGameController mockGameController;

        // Temporary directory where test logfiles are sent to
        private Path logDir;

        private final String PROGRAM_TITLE_MESSAGE = "=== ECM2414 Software Development Coursework ===";
        private final String PROGRAM_SUBTITLE_MESSAGE = "\nMultithreaded Card Playing Sim";
        private final String PLEASE_ENTER_PLAYER_NUMBER_MESSAGE = "Please enter the number of players: ";
        private final String PLAYER_NUM_GREATER_THAN_ONE_ERROR = "ERROR: Number of players must be an Integer greater than 1.";
        private final String PLAYER_NUM_NO_INPUT_ERROR = "ERROR: Please enter a number of players.";

        /**
         * Runs the CardGame as a mock game using streams instead of console
         * Completes on hitting NoSuchElementException
         * @param inputs String array of inputs to be streamed, line by line
         * @return The buffered output as a String from runtime to exception, separated by newlines "\n"
         */
        private String getOutput(String[] inputs) {
            String inputString = String.join("\n", inputs);

            // Convert the inputString to a byte array input stream (using UTF-8 formatting)
            InputStream playerCountsAsStream = new ByteArrayInputStream(inputString.getBytes(StandardCharsets.UTF_8));

            // Create a response stream and buffer from the PrintStream output (using UTF-8 formatting)
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream responses = new PrintStream(buffer, true, StandardCharsets.UTF_8);

            // Create a mock card game, using the streams instead of console
            CardGame MockGameState = new CardGame(playerCountsAsStream, responses, this.logDir);

            // Run the program until NoSuchElementException (when no more responses to process)
            try {
                MockGameState.startSim();
            } catch (NoSuchElementException e) {
                //
            }

            // Now game's finished, flush the response stream to the buffer
            responses.flush();

            // Return the buffer as a string (using UTF-8 formatting)
            return buffer.toString(StandardCharsets.UTF_8);
        }

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

        @Test
        void invalidPlayerCountReprompt() {
            // Create 4 input cases for different scenarios
            String[] invalidPlayerCounts = {
                "five",  // Invalid Input (not int)
                "1",     // Invalid Input (not >1)
                "-5",    // Invalid Input (negative)
                " "      // No Input
            };

            String[] expectedOutputs = {
                    PROGRAM_TITLE_MESSAGE + PROGRAM_SUBTITLE_MESSAGE,
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR,
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR,
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR,
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_NO_INPUT_ERROR
            };

            // Run the program until error and return the string output from PrintStream
            String output = getOutput(invalidPlayerCounts);

            String[] outputs = output.split("\n\n");

            assertEquals(expectedOutputs[0], outputs[0]);
            assertEquals(expectedOutputs[1], outputs[1]);
            assertEquals(expectedOutputs[2], outputs[2]);
            assertEquals(expectedOutputs[3], outputs[3]);
        }

        @Test
        void validPlayerCountSucceeds() {}


        @Test
        void missingPackFileReprompt() {

        }

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
