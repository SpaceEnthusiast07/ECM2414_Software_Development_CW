package card_playing_sim;

/**
 * An exception for any problems with pack file formatting during validation. Such uses include not enough numbers,
 * negative numbers, noninteger, values, and not enough of each card corresponding to a player's value.
 */
public class InvalidPackFileException extends RuntimeException {
    public InvalidPackFileException(String message) {
        super(message);
    }
}
