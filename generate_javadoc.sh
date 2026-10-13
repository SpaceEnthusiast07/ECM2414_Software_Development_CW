#!/bin/bash

# Optional argument for mode (-v for output)
MODE=${1:-""}

# Turn off bash file output for a cleaner terminal
set +x

# Compile javadoc with quiet flag unless "-v" arg passed
if [ "$MODE" = "-v" ]; then
    javadoc -d ./docs/javadoc/ ./src/card_playing_sim/*
else
    javadoc -quiet -d ./docs/javadoc/ ./src/card_playing_sim/*
fi
