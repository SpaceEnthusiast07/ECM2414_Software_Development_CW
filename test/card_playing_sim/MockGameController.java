package card_playing_sim;

import java.nio.file.Path;

public class MockGameController implements GameController {
    private final Path logDirectory;
    private int winningPlayerValue = 0;

    MockGameController(Path logDirectory) {
        this.logDirectory = logDirectory;
    }

    @Override
    public Path getLogDirectory() {
        return this.logDirectory;
    }

    @Override
    public boolean isGameOver() {
        return this.winningPlayerValue != 0;
    }

    @Override
    public boolean declareWinner(int playerValue) {
        // If winner has already been set, return false
        if (this.winningPlayerValue != 0) {
            return false;
        }
        // Else set this player's value as the winner
        this.winningPlayerValue = playerValue;
        return true;
    }

    @Override
    public int getWinningPlayerValue() {
        return this.winningPlayerValue;
    }

    @Override
    public void playerExited() {
        return;
    }
}
