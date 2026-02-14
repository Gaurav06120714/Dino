import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Constants for the game board dimensions
        final int BOARD_WIDTH = 750;
        final int BOARD_HEIGHT = 250;

        // Create the main frame for the game
        JFrame frame = new JFrame("Chrome Dinosaur");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Create an instance of the game panel
        ChromeDinosaur chromeDinosaur = new ChromeDinosaur(BOARD_WIDTH, BOARD_HEIGHT);
        frame.setIconImage(chromeDinosaur.DINOSAUR_JUMP_IMG);
        frame.add(chromeDinosaur);
        chromeDinosaur.requestFocus(); // Request focus for key events
        frame.pack();

        frame.setVisible(true);
    }
}