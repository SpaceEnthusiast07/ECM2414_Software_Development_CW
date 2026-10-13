package card_playing_sim;

public class Pack {
    private Card[] cards;
    // Marks the location (index) of the top most card
    private int topOfPack;
    // Marks the location (index) of the next available space at bottom
    private int bottomOfPack;

    /**
     * Creates a new pack of length 8n, where n = the number of players.
     * @param numberOfPlayers The number of players in the game.
     */
    public Pack(int numberOfPlayers) {
        this.cards = new Card[8*numberOfPlayers];
        // Initiate markers
        this.topOfPack = 0;
        this.bottomOfPack = 0;
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
     * @param newCard The new Card to add.
     * @throws InvalidOperationException When pack is full
     */
    public void addCardToBottom(Card newCard) throws InvalidOperationException {
        // Ensure there is enough space to add another card
        if (bottomOfPack == cards.length-1) {
            throw new InvalidOperationException("ERROR: Cannot add card to full pack");
        }

        // Add this new card
        this.cards[bottomOfPack] = newCard;
        // Mark new location of bottom of pack
        bottomOfPack++;

    }

    /**
     * Allows the client to remove a card from the top of the pack.
     * @return The Card removed from the top of the pack.
     * @throws InvalidOperationException When pack is empty
     */
    public Card takeTopCard() throws InvalidOperationException {
        // Ensure there are cards to take
        if (bottomOfPack == topOfPack) {
            throw new InvalidOperationException("ERROR: Cannot take card from empty pack");
        }

        // Point top card marker to new top card
        topOfPack++;
        return cards[topOfPack - 1];

    }
}
