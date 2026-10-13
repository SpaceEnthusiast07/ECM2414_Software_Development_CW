package card_playing_sim;

// TODO: Write class Javadoc
public class Pack {
    private Card[] cards;

    // Marks the location (index) of the top most card
    private int topOfPack;

    // Marks the location (index) of the next available space at bottom of the pack
    private int bottomOfPack;

    /**
     * Creates a new pack of length 8n, where n = the number of players.
     * @param numberOfPlayers The number of players in the game.
     */
    public Pack(int numberOfPlayers) {
        this.cards = new Card[8*numberOfPlayers];
        // Initiate pointers
        this.topOfPack = 0;
        this.bottomOfPack = 0;
    }

    /**
     * Allows the client to create a new pack by providing an array of cards.
     * @param cards The pre-filled array of cards.
     */
    public Pack(Card[] cards) {
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
    public void addCardToBottom(Card newCard) throws InvalidOperationException {
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
    public Card takeTopCard() throws InvalidOperationException {
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
