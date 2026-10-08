#!/bin/bash

# Turn off bash file output for a cleaner terminal
set +x

# Compile everything in the card_playing_sim package
javac -cp ./bin -d ./bin ./src/card_playing_sim/*

# Run the main executable
java -cp ./bin card_playing_sim.CardGame