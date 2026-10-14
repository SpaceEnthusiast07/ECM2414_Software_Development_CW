package card_playing_sim;

// TODO: Jake, can you check exception Javadoc?

/**
 * Used throughout the project. Such uses include adding to a full deck, removing from an empty deck, adding a card
 * to a full player's hand, etc.
 */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
