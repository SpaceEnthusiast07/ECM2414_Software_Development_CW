package card_playing_sim;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

class TestUtils {

    static Path generateValidPackFile(Path directory, String fileName, int numPlayers) throws IOException {
        // Create 8n lines (for the packFile)
        String[] lines = new String[8 * numPlayers];

        // Writes 4 of each player's values (1,1,1,1, 2,2,2,2, ...., n,n,n,n)
        for (int i = 0; i < 4 * numPlayers; i++) {
            lines[i] = Integer.toString(i / 4 + 1);
        }

        // For the other half of the file, write random card values between 1 and 2n
        int MAX_CARD_VAL = 2*numPlayers;
        Random rand = new Random();
        for (int i = 4 * numPlayers; i < 8 * numPlayers; i++) {
            int randomCardVal = rand.nextInt(MAX_CARD_VAL) + 1;
            lines[i] = Integer.toString(randomCardVal);
        }

        // Write these lines to an actual file and return the Path
        return writePackFile(directory, fileName, lines);
    }

    static Path writePackFile(Path directory, String fileName, String[] lines) throws IOException {
        Path packFile = directory.resolve(fileName);

        // Write each line to the pack file, followed by a newline
        try (FileWriter fw = new FileWriter(packFile.toFile())) {
            for (String line : lines) {
                fw.write(line + "\n");
            }
        }

        return packFile;
    }
}
