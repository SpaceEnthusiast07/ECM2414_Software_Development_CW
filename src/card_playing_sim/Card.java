package card_playing_sim;

public class Card {
    // Card properties
    private Suit suit;
    private Rank rank;

    /**
     * Regular Card constructor to create a card by specifying the actual Suit and Rank enum constant.
     * @param suit The Suit enum constant for this card.
     * @param rank The Rank enum constant for this card.
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Allows automated creation of a card by specifying the index of the enum constant rather than the constant
     * name itself.
     * @param suitIndex The index of the suit enum constant: HEARTS=0, DIAMONDS=1, CLUBS=2, and SPADES=3.
     * @param rankIndex The index of the rank enum constant: TWO=0, THREE=1, FOUR=2, ..., TEN=8, JACK=9, QUEEN=10,
     *                  KING=11, and ACE=12
     */
    public Card(int suitIndex, int rankIndex) {
        this.suit = Suit.values()[suitIndex];
        this.rank = Rank.values()[rankIndex];
    }
}