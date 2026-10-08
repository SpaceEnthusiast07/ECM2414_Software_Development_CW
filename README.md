
# Develop a multi-threaded card playing simulation.

> [!IMPORTANT]
> Execute `.\run.bat` on Windows or `./run.sh` on macOS and Linux from the base project directory to build and run the project.

This document contains notes on the implementation of this card playing sim.

<br>

## Card Game Class - EXECUTABLE
```Java
class CardGame {
	public static void main(String[] args) {
		// Game starting logic here
	}
}
```


<br>

## Card Class - THREAD SAFE
```Java
class Card {
	// Card properties
	private Pip suit;
	private Index value;

    /**
     * Initialises a new card.
     * @param suit The suit of this card, either HEART, DIAMOND, CLUB, or SPADE.
     * @param value The rank of this card, from ACE, through to KING.
     */
	public Card(Pip suit, Index value) {
		this.suit = suit;
		this.value = value;
	}
}
```

Since the suit of a card is a name (string), I thought I would use Enums to improve readability and prevent miss typing of the card's suit. Then, for the value of the card, I also used an Enum because this attribute of a card can take on different data types: integers and words (strings).


<br>

## Card Deck Class: A deck of 52 cards
```Java
class CardDeck {
    // Array of 52 cards
    private Card[] cards;

    /**
     * Initialises a new deck of cards; a deck contains 52 cards: 4 suits and 13 ranks.
     */
    public CardDeck() {
        this.cards = new Card[52];
    }
}
```

Since there are `n` players, there must also be `n` decks of cards; one for each player.


<br>

## Pip Enum: The Card's Suit
```Java
enum Pip {
	HEART, DIAMOND, CLUB, SPADE
}
```

The word **pip** is used to refer to the suit of the card.


<br>

## Index Enum: The Card's Value
```Java
enum Index {
	TWO, THREE, FOUR, FIVE, SIX, SEVEN,
	EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE
}
```

The word **index** is used to refer to the card's value.


<br>

## Player Class - THREAD SAFE
```Java
class Player {
	// Player attributes
	private String name; // structured as "player1", "player2", ..., "playern"
	private Card[] hand; // max 4 cards

    /**
     * Initialises a new player.
     * @param playerIndex The number of players currently in the game, used to label each player.
     */
    public Player(int playerIndex) {
        this.name = String.format("player%d", playerIndex);
        this.hand = new Cards[4];
    }

    /**
     * Initialises a new player.
     * @param name A custom name for this player.
     */
    public Player(String name) {
        this.name = name;
        this.hand = new Cards[4];
    }
}
```

There are `n` players, i.e. any number.


<br>

## Pack class: A collection of `8n` cards
```Java
class Pack {
    private Card[] cards;

    /**
     * Initialises a new pack of cards.
     * @param n The number of players; pack contains 8n cards.
     */
    public Pack(int n) {
        this.cards = new Card[8*n];
    }
}
```
