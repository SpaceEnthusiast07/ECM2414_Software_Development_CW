package card_playing_sim;

public class Card {
    // Each card has an associated value
    private final int value;

    /**
     * Creates a new card by specifying its value.
     * @param value Regular Integer value of the card.
     */
    public Card(int value) {
        this.value = value;
    }

    /**
     * Allows the client to retrieve this card's value.
     * @return This card's value as an Integer.
     */
    public int getValue() {
        return value;
    }
}
