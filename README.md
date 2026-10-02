
# Develop a multi-threaded card playing simulation.

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
	// Card details
	private Pip suit;
	private Index value;
	
	public Card(Pip suit, Index value) {
		this.suit = suit;
		this.value = value;
	}
}
```

Since the suit of a card is a name (string), I thought I would use Enums to improve readability and prevent miss typing of the card's suit. Then, for the value of the card, I also used an Enum because this attribute of a card can take on different data types: integers and words (strings).


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
}
```

There are `n` players, i.e. any number.