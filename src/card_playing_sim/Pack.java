package card_playing_sim;

/**
 * Provides a structure for storing 8n {@link Card}s and the operations to manipulate them at the start of the
 * simulation.
 * <br>
 *  - Generally a Pack is constructed by passing in the number of players, but a pre-filled array of Cards can also
 * be supplied.
 * <br>
 *  - To add a card, call {@link #addCardToBottom(Card newCard)}, providing the new card to add; an exception is thrown
 * when the pack is full.
 * <br>
 *  - To take a card from the top of the pack, call {@link #takeTopCard()}, which returns the top card, as a Card object.
 * If the pack doesn't contain any cards, an exception is thrown.
 * <br>
 *  - This Pack class is thread-confined, so doesn't implement any thread-safe aspects.
 */
class Pack {
    // Array of cards in pack
    private Card[] cards;

    // Points TO the top most card
    private int topOfPack;

    // Points to the next AVAILABLE space to place a card in the pack; i.e., bottom of pack
    private int bottomOfPack;

    /**
     * Creates a new pack of length 8n, where n = the number of players.
     * @param numberOfPlayers The number of players in the game.
     */
    Pack(int numberOfPlayers) {
        this.cards = new Card[8*numberOfPlayers];
        // Initiate pointers
        this.topOfPack = 0;
        this.bottomOfPack = 0;
    }

    /**
     * Allows the client to create a new pack by providing an array of cards.
     * @param cards The pre-filled array of cards.
     */
    Pack(Card[] cards) {
        this.cards = cards;
        // Initiate pointers
        this.topOfPack = 0;
        this.bottomOfPack = cards.length;
    }

    /**
     * Allows the client to add a new card to bottom of the pack.
     * @param newCard The new Card to add.
     * @throws InvalidOperationException When the pack is full.
     */
    void addCardToBottom(Card newCard) throws InvalidOperationException {
        // Ensure there is enough space to add another card
        if (this.bottomOfPack == this.cards.length) {
            throw new InvalidOperationException("ERROR: Cannot add a card to a full pack.");
        }

        // Add this new card
        this.cards[this.bottomOfPack] = newCard;

        // Mark the new next available space
        this.bottomOfPack++;
    }

    /**
     * Allows the client to remove a card from the top of the pack.
     * @return The Card removed from the top of the pack.
     * @throws InvalidOperationException When the pack is empty.
     */
    Card takeTopCard() throws InvalidOperationException {
        // Ensure there are cards to take
        if (this.bottomOfPack == this.topOfPack) {
            throw new InvalidOperationException("ERROR: Cannot take a card from an empty pack.");
        }

        // Store reference to top card
        Card topCard = this.cards[this.topOfPack];

        // Remove the reference to this card from the pack
        this.cards[this.topOfPack] = null;

        // Shift top card pointer to new top card
        this.topOfPack++;

        // Return the requested card
        return topCard;
    }
}
