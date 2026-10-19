package card_playing_sim;

import java.nio.file.Path;

/**
 * Interaction layer between callers and the game state.
 * <br>
 * Decouples game state from game logic and interaction.
 * <br>
 * Allows the creation of mock objects within tests to create simulated interactions versus regular player interactions.
 */
public interface GameController {
    /**
     * Atomically sets this player as the winner, checking if any other players have declared winning beforehand.
     * @param playerValue The value of the calling player who's won.
     * @return True if first player to declare win (true winner), otherwise false (not winner).
     */
    boolean declareWinner(int playerValue);

    /**
     * Gets the log file directory Path (defaults to DEFAULT_LOG_DIRECTORY).
     * @return The directory Path that player log files are written to.
     */
    Path getLogDirectory();

    /**
     * Checks if a winning player has been set yet.
     * @return True if there is a winning player, otherwise False.
     */
    boolean isGameOver();

    /**
     * Gets the value of the player who won (0 if none).
     * @return Positive integer value of the winning player.
     */
    int getWinningPlayerValue();

    /**
     * Informs main program that this thread has exited (finished).
     */
    void playerExited();

}
