import java.io.*;
import java.nio.file.*;

/**
 * Persists the all-time high score to disk so it survives between sessions.
 * Saves to: ~/.dino-game/highscore.txt
 */
public class ScoreManager {

    private static final Path SAVE_DIR  = Path.of(System.getProperty("user.home"), ".dino-game");
    private static final Path SAVE_FILE = SAVE_DIR.resolve("highscore.txt");

    private int sessionHighScore = 0;
    private int allTimeHighScore = 0;

    public ScoreManager() {
        allTimeHighScore = load();
    }

    /** Call every frame to track current run and update all-time record. */
    public void update(int currentScore) {
        if (currentScore > sessionHighScore) sessionHighScore = currentScore;
        if (currentScore > allTimeHighScore) {
            allTimeHighScore = currentScore;
            save(allTimeHighScore);
        }
    }

    /** Returns the best score of the current session. */
    public int getSessionHigh() { return sessionHighScore; }

    /** Returns the all-time best score (persisted across sessions). */
    public int getAllTimeHigh() { return allTimeHighScore; }

    /** Reset session high (call on game reset — all-time is kept). */
    public void resetSession() { sessionHighScore = 0; }

    // ── persistence ──────────────────────────────────────────────────────

    private int load() {
        try {
            if (Files.exists(SAVE_FILE)) {
                String raw = Files.readString(SAVE_FILE).trim();
                return Integer.parseInt(raw);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private void save(int score) {
        try {
            Files.createDirectories(SAVE_DIR);
            Files.writeString(SAVE_FILE, String.valueOf(score));
        } catch (Exception ignored) {}
    }
}
