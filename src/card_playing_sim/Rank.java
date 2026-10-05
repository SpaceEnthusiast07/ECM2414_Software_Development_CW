package card_playing_sim;

/**
 * This Enum represents the card's rank/value,
 * i.e. 1 through 10; and Jack, Queen, King, and Ace.
 */
public enum Rank {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
    EIGHT(8), NINE(9), TEN(10), JACK(11), QUEEN(12), KING(13), ACE(14);

    // The value of the card's rank as an integer which is used for calculations
    private int value;

    Rank(int value) {
        this.value = value;
    }

    /**
     * Allows the client to retrieve the integer version of the card's rank.
     * @return An integer representing the card's value.
     */
    public int getValue() {
        return this.value;
    }
}
