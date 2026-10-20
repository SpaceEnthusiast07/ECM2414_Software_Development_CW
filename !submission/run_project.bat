:: Compile everything in the card_playing_sim package, including tests
javac -cp .\bin;.\libs\junit-platform-console-standalone-6.1.3.jar -d .\bin .\src\card_playing_sim\*.java .\test\card_playing_sim\*.java

:: Run the main executable
java -cp .\bin card_playing_sim.CardGame