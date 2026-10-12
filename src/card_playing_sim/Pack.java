package card_playing_sim;

public class Pack {
    private Card[] cards;
    // Marks the location (index) of the top most card
    private int topOfPack;
    // Marks the location (index) of the bottom most card
    private int bottomOfPack;

    /**
     * Creates a new pack of length 8n, where n = the number of players.
     * @param numberOfPlayers The number of players in the game.
     */
    public Pack(int numberOfPlayers) {
        this.cards = new Card[8*numberOfPlayers];
    }

    /**
     * Allows the client to create a new pack by specifying an array of cards.
     * @param cards The array of already created cards.
     */
    public Pack(Card[] cards) {
        this.cards = cards;
        // Initiate markers
        this.topOfPack = 0;
        this.bottomOfPack = cards.length-1;
    }

    /**
     * Allows the client to add a new card to bottom of the pack.
     * @param card The new Card to add.
     */
    public void addCard(Card card) {
        // Ensure there is enough space to add another card
        if (bottomOfPack == cards.length-1) {
            return;
        } else {
            // Mark new location of bottom of pack
            bottomOfPack++;
            this.cards[bottomOfPack] = card;
        }
    }

    /**
     * Allows the client to remove a card from the top of the pack.
     * @return The Card removed from the top of the pack.
     */
    public Card removeCard() {
        // Point top card marker to new top card
        topOfPack++;
        return cards[topOfPack-1];
    }
}
