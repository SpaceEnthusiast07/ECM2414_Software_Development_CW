package card_playing_sim;

public interface GameController {
    /**
     * Atomically sets this player as the winner, checking if any other player's have declared winning beforehand.
     * @param playerValue The value of the calling player who's won
     * @return True if first player setting win (true winner), otherwise false - not winner
     */
    boolean declareWinner(int playerValue);

    /**
     * Gets the string log file directory (defaults to DEFAULT_LOG_DIRECTORY)
     * @return A String log file directory
     */
    String getLogDirectory();

    /**
     * Checks if a winning player has been set yet
     * @return True if there is a winning player, otherwise False
     */
    boolean isGameOver();

    /**
     * Gets the value of the player who won (0 if none)
     * @return Positive integer value of the winning player
     */
    int getWinningPlayerValue();

    /**
     * Informs main program that this thread has exited (finished)
     */
    void playerExited();

}
