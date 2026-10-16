:: Turn off batch file output for a cleaner terminal
@echo off

:: Ensure the binaries are up to date
javac -cp .\bin -d .\bin .\src\card_playing_sim\*

:: Generate the jar file with the main entry point being CardGame
:: and place the jar into the !submission folder
jar -cvfe !submission/cards.jar card_playing_sim.CardGame -C bin/ . -C src/card_playing_sim/ .
