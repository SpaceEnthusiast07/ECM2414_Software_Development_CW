package card_playing_sim;

/**
 * An exception for any methods/operations/calculations that are invalid or given invalid inputs. Such uses include
 * adding to a full deck, removing from an empty deck, adding a card to a full player's hand, etc.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
