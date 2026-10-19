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
        private MockGameController mockGameController;
        private Path logDir;

        private final String PROGRAM_TITLE_MESSAGE = "=== ECM2414 Software Development Coursework ===\n" +
                "Multithreaded Card Playing Sim\n";
        private final String PLEASE_ENTER_PLAYER_NUMBER_MESSAGE = "Please enter the number of players: ";
        private final String PLAYER_NUM_GREATER_THAN_ONE_ERROR = "ERROR: Number of players must be an Integer greater than 1.\n";
        private final String PLAYER_NUM_NO_INPUT_ERROR = "ERROR: Please enter a number of players.\n";

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
            String invalidPlayerCounts = "five"   // Invalid Input (not int)
                                        + "\n1"   // Invalid Input (not >1)
                                        + "\n-5"  // Invalid Input (negative)
                                        + "\n";   // No Input

            InputStream playerCountsAsStream = new ByteArrayInputStream(invalidPlayerCounts.getBytes(StandardCharsets.UTF_8));

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream responses = new PrintStream(buffer, true, StandardCharsets.UTF_8);

            CardGame MockGameState = new CardGame(playerCountsAsStream, responses, this.logDir);

            try {
                MockGameState.startSim();
            } catch (NoSuchElementException e) {
                //
            }

            responses.flush();

            String output = buffer.toString(StandardCharsets.UTF_8);

            String expectedOutput = PROGRAM_TITLE_MESSAGE + "\n" +
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR + "\n" +
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR + "\n" +
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_GREATER_THAN_ONE_ERROR + "\n" +
                    PLEASE_ENTER_PLAYER_NUMBER_MESSAGE + PLAYER_NUM_NO_INPUT_ERROR;
            assertEquals(expectedOutput, output);
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
