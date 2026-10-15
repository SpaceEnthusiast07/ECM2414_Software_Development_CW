package card_playing_sim;

/**
 * Represents a card in this simulation; a card has only one property: a value that is a positive integer.
 * <br>
 *  - A new card is created by specifying a positive integer value to the constructor.
 * <br>
 *  - The value of a card is a constant; hence, cannot be changed.
 * <br>
 *  - To retrieve a card's value, call {@link #getValue()}.
 */
class Card {
    // Each card has an associated value
    private final int value;

    /**
     * Creates a new card by specifying its value.
     * @param value A positive Integer value of the card.
     */
    Card(int value) {
        this.value = value;
    }

    /**
     * Allows the client to retrieve this card's value.
     * @return This card's value as an Integer.
     */
    int getValue() {
        return this.value;
    }
}
