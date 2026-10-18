package card_playing_sim;

/**
 * Represents a card in this simulation; a card has only one property: a value that is a positive integer.
 * <br>
 * - A new card is created by specifying a positive integer value to the constructor.
 * <br>
 * - The value of a card is a constant; hence, cannot be changed.
 * <br>
 * - To retrieve a card's value, call the {@link #value()} method.
 *
 * @param value The value of this card as an Integer.
 */
record Card(int value) {}
