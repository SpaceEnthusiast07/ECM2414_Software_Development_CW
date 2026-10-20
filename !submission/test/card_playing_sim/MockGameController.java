package card_playing_sim;

import java.nio.file.Path;

/**
 * Mock object of {@link GameController} for unit testing {@link Player}
 * Includes additional methods to imitate player interaction and logic.
 */
public class MockGameController implements GameController {
    private final Path logDirectory;
    private int winningPlayerValue = 0;

    // Remaining checks of isGameOver() calls that return false before the game force ends
    // Used to control how long game runs for
    // (-1 = never ends on its own)
    private int checksRemaining = -1;

    // Winner to report once checksRemaining runs out
    private int winnerWhenChecksRunOut = 0;

    // Whether the player reported that it exited
    private boolean playerExitedCalled = false;

    MockGameController(Path logDirectory) {
        this.logDirectory = logDirectory;
    }

    /**
     * Simulates another player having already won the game
     * @param playerValue The value of the winning player
     */
    void setWinningPlayerValue(int playerValue) {
        this.winningPlayerValue = playerValue;
    }

    /**
     * Ends the game after isGameOver() has returned false the given number of times
     * then reports back the given player as the winner.
     * Used to stop a player that can't stop on its own
     * @param checks The number of isGameOver() calls before the game ends
     * @param winnerValue The value of the player to report as the winner
     */
    void endGameAfterChecks(int checks, int winnerValue) {
        this.checksRemaining = checks;
        this.winnerWhenChecksRunOut = winnerValue;
    }

    /**
     * @return True if the player reported that it exited already, otherwise false
     */
    boolean wasPlayerExitedCalled() {
        return this.playerExitedCalled;
    }

    @Override
    public Path getLogDirectory() {
        return this.logDirectory;
    }

    @Override
    public boolean isGameOver() {
        // Check if a winner already set
        if (this.winningPlayerValue != 0) {
           return true;
        }

        // If remaining checks ran out, end game with set winner
        if (this.checksRemaining == 0) {
            this.winningPlayerValue = this.winnerWhenChecksRunOut;
            return true;
        }

        // Otherwise, decrement the checks counter and continue game
        if (this.checksRemaining > 0) {
            this.checksRemaining--;
        }
        return false;
    }

    @Override
    public boolean declareWinner(int playerValue) {
        // If winner has already been set, return false
        if (this.winningPlayerValue != 0) {
            return false;
        }
        // Else set this player's value as the winner
        this.winningPlayerValue = playerValue;
        return true;
    }

    @Override
    public int getWinningPlayerValue() {
        return this.winningPlayerValue;
    }

    @Override
    public void playerExited() {
        this.playerExitedCalled = true;
    }
}
