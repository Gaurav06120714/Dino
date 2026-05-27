import javax.swing.*;

/**
 * Entry point for Chrome Dinosaur Game.
 * Launches on the Event Dispatch Thread for thread safety.
 */
public class Main {

    private static final int    BOARD_WIDTH  = 900;   // wider board for better gameplay
    private static final int    BOARD_HEIGHT = 300;
    private static final String TITLE        = "Chrome Dinosaur — Double Jump Edition";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            ChromeDinosaur game = new ChromeDinosaur(BOARD_WIDTH, BOARD_HEIGHT);
            frame.setIconImage(game.DINOSAUR_JUMP_IMG);
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);   // centre on screen after pack()
            frame.setVisible(true);

            game.requestFocusInWindow();
        });
    }
}