package card_playing_sim;

public class MockGameController implements GameController {
    private final String logDirectory;
    private int winningPlayerValue = 0;

    MockGameController(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    @Override
    public String getLogDirectory() {
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
