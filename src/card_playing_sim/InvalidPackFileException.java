package card_playing_sim;

// TODO: Jake, can you check exception Javadoc?

/**
 * Used when validating the provided pack file. Problems include not enough numbers, negative numbers, noninteger
 * values, and not enough of each card corresponding to a player's value.
 */
public class InvalidPackFileException extends RuntimeException {
    public InvalidPackFileException(String message) {
        super(message);
    }
}
