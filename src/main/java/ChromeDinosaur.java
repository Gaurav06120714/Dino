import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Objects;

public class ChromeDinosaur extends JPanel implements ActionListener, KeyListener {
    private final int BOARD_WIDTH;
    private final int BOARD_HEIGHT;

    // -------- Images --------
    // Dinosaur images
    public final Image DINOSAUR_IMG;
    public final Image DINOSAUR_DEAD_IMG;
    public final Image DINOSAUR_JUMP_IMG;
    public final Image DINOSAUR_DUCK_IMG;
    // Cactus images
    public final Image CACTUS_SMALL_1_IMG;
    public final Image CACTUS_SMALL_2_IMG;
    public final Image CACTUS_SMALL_3_IMG;
    // Pterodactyl images
    public final Image PTERODACTYL_IMG;
    // Ground image
    public final Image GROUND_IMG;
    private final int GROUND_HEIGHT; // Height of the ground image
    private final int GROUND_WIDTH; // Width of the ground image
    // Cloud image
    public final Image CLOUD_IMG;
    private final ArrayList<VeloBlock> cloudArray;
    // UI elements
    public final Image GAME_OVER_IMG;
    public final Image RESET_IMG;

    // Dinosaur
    // Set dinosaur dimensions and position
    private final int DINOSAUR_WIDTH;
    private final int DINOSAUR_DUCK_WIDTH;
    private final int DINOSAUR_HEIGHT;
    private final int DINOSAUR_DUCK_HEIGHT;
    private final int DINOSAUR_X;
    private final int DINOSAUR_Y;
    private final Block DINOSAUR;

    // Cactus
    private final int CACTUS_1_WIDTH;
    private final int CACTUS_2_WIDTH;
    private final int CACTUS_3_WIDTH;
    private final int CACTUS_HEIGHT; // Height of the cactus images
    private final int CACTUS_X;
    private final int CACTUS_Y;
    private final ArrayList<Block> cactusArray;

    // Pterodactyls
    private final int PTERODACTYL_WIDTH; // Width of the pterodactyl image
    private final int PTERODACTYL_HEIGHT; // Height of the pterodactyl image
    private final ArrayList<VeloBlock> pterodactylArray;

    // Physics
    private int velocityX = -12; // Horizontal velocity, for the cactus movement
    private int velocityY = 0; // Vertical velocity for jumping
    private final int GRAVITY = 1; // Gravity effect on the dinosaur
    private int groundOffsetX = 0; // Offset for the ground movement

    private boolean gameOver = false; // Flag to check if the game is over
    private int score = 0;
    private int highScore = 0;

    // Game loop timer
    private final Timer GAMELOOP;
    private final Timer PLACE_CACTUS_TIMER;
    private final Timer PLACE_CLOUD_TIMER;
    private boolean isDucking = false;
    private JButton resetButton;

    /**
     * Constructor for ChromeDinosaur class.
     * @param boardWidth The width of the game board.
     * @param boardHeight The height of the game board.
     */
    ChromeDinosaur(int boardWidth, int boardHeight) {
        this.BOARD_WIDTH = boardWidth;
        this.BOARD_HEIGHT = boardHeight;

        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(this); // Add key listener
        this.setBackground(Color.LIGHT_GRAY);

        // Load images
        DINOSAUR_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/dino-run.gif"))).getImage();
        DINOSAUR_DEAD_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/dino-dead.png"))).getImage();
        DINOSAUR_JUMP_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/dino-jump.png"))).getImage();
        DINOSAUR_DUCK_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/dino-duck.gif"))).getImage();
        CACTUS_SMALL_1_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/cactus1.png"))).getImage();
        CACTUS_SMALL_2_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/cactus2.png"))).getImage();
        CACTUS_SMALL_3_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/cactus3.png"))).getImage();
        PTERODACTYL_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/bird.gif"))).getImage();
        GROUND_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/track.png"))).getImage();
        CLOUD_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/cloud.png"))).getImage();
        GAME_OVER_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/game-over.png"))).getImage();
        RESET_IMG = new ImageIcon(Objects.requireNonNull(getClass().getResource("images/reset.png"))).getImage();

        // Set ground dimensions
        this.GROUND_HEIGHT = GROUND_IMG.getHeight(null); // Height of the ground image
        this.GROUND_WIDTH = GROUND_IMG.getWidth(null); // Width of the ground image

        // Set dinosaur dimensions and position
        this.DINOSAUR_WIDTH = 88; // Width of the dinosaur image
        this.DINOSAUR_HEIGHT = 94; // Height of the dinosaur image
        this.DINOSAUR_DUCK_WIDTH = 118; // Width of the ducking dinosaur image
        this.DINOSAUR_DUCK_HEIGHT = 60; // Height of the ducking dinosaur image
        this.DINOSAUR_X = 50; // X position of the dinosaur
        this.DINOSAUR_Y = BOARD_HEIGHT - DINOSAUR_HEIGHT - 10; // Y position of the dinosaur, 10 pixels above the bottom

        // Set cactus dimensions and position
        this.CACTUS_1_WIDTH = 34;
        this.CACTUS_2_WIDTH = 69;
        this.CACTUS_3_WIDTH = 102;
        this.CACTUS_HEIGHT = 70; // Height of the cactus images
        this.CACTUS_X = BOARD_WIDTH;
        this.CACTUS_Y = BOARD_HEIGHT - CACTUS_HEIGHT; // Y position of the cactus

        // Set pterodactyl dimensions and position
        this.PTERODACTYL_HEIGHT = 60;
        this.PTERODACTYL_WIDTH = 90;

        // Initialize the cloud array
        this.cloudArray = new ArrayList<>();

        // Initialize the dinosaur block
        this.DINOSAUR = new Block(DINOSAUR_X, DINOSAUR_Y, DINOSAUR_WIDTH, DINOSAUR_HEIGHT, DINOSAUR_IMG);

        // Initialize the cactus array
        this.cactusArray = new ArrayList<>();

        // Initialize the pterodactyl array
        this.pterodactylArray = new ArrayList<>();

        // Set the game loop timer
        GAMELOOP = new Timer(1_000 / 60, this); // 60 FPS
        GAMELOOP.start();

        // Set the cactus placement timer
        PLACE_CACTUS_TIMER = new Timer(1_500, _ -> placeCactus());
        PLACE_CLOUD_TIMER = new Timer(2_000, _ -> placeClouds());
        PLACE_CACTUS_TIMER.start();
        PLACE_CLOUD_TIMER.start();
    }

    /**
     * Places a cactus on the game board with a random chance.
     * The cactus can be of three different sizes, each with a different probability.
     */
    public void placeCactus() {
        Block cactus = null;
        VeloBlock pterodactyl = null;
        double placeCactusChance = Math.random();

        if (placeCactusChance > .90) { // 10% chance
            cactus = new Block(CACTUS_X, CACTUS_Y, CACTUS_3_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_3_IMG);
        } else if (placeCactusChance > .70) { // 20% chance
            cactus = new Block(CACTUS_X, CACTUS_Y, CACTUS_2_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_2_IMG);
        } else if (placeCactusChance > .50) { // 20% chance
            cactus = new Block(CACTUS_X, CACTUS_Y, CACTUS_1_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_1_IMG);
        } else if (placeCactusChance > .40) { // 10% chance
            // Add a pterodactyl instead of a cactus
            double pterodactylVelocityX = 1.2;
            int pterodactylY = (int) (Math.random() * (BOARD_HEIGHT - PTERODACTYL_HEIGHT - 10)); // Random Y position
            pterodactyl = new VeloBlock(CACTUS_X, pterodactylY, PTERODACTYL_WIDTH, PTERODACTYL_HEIGHT, PTERODACTYL_IMG, pterodactylVelocityX);
        }
        
        if (cactus != null)
            cactusArray.add(cactus);

        if (pterodactyl != null)
            pterodactylArray.add(pterodactyl);
    }

    /**
     * Places clouds on the game board at random positions.
     * Clouds appear in the upper half of the screen with random widths and heights.
     */
    private void placeClouds() {
        int cloudY = (int) (Math.random() * ((double) BOARD_HEIGHT / 2)); // Clouds appear in the upper half of the screen
        int cloudWidth = (int) (Math.random() * 100 + 50); // Random width between 50 and 150
        int cloudHeight = (int) (CLOUD_IMG.getHeight(null) * ((double) cloudWidth / CLOUD_IMG.getWidth(null))); // Maintain aspect ratio
        double cloudVelocityX = Math.random() + 0.2; // Random speed between 0.2 and 1.2
        VeloBlock cloud = new VeloBlock(CACTUS_X, cloudY, cloudWidth, cloudHeight, CLOUD_IMG, cloudVelocityX);
        cloudArray.add(cloud);
    }

    /**
     * Paints the game components.
     * @param g The Graphics object used for painting.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * Draws the game components.
     * @param g The Graphics object used for drawing.
     */
    public void draw(Graphics g) {
        // Draw the ground
        g.drawImage(GROUND_IMG, groundOffsetX, BOARD_HEIGHT - GROUND_HEIGHT, GROUND_WIDTH, GROUND_HEIGHT, null);
        // Draw the ground again to create a seamless effect
        g.drawImage(GROUND_IMG, groundOffsetX + GROUND_WIDTH, BOARD_HEIGHT - GROUND_HEIGHT, GROUND_WIDTH, GROUND_HEIGHT, null);

        // Draw the clouds
        for (Block cloud : cloudArray) {
            g.drawImage(cloud.image, cloud.x, cloud.y, cloud.width, cloud.height, null);
        }

        // Draw the dinosaur
        if (isDucking && DINOSAUR.y == DINOSAUR_Y) {
            int yPos = DINOSAUR_Y + (DINOSAUR_HEIGHT - DINOSAUR_DUCK_HEIGHT); // Adjust Y position for ducking
            g.drawImage(DINOSAUR_DUCK_IMG, DINOSAUR.x, yPos, DINOSAUR_DUCK_WIDTH, DINOSAUR_DUCK_HEIGHT, null);
        } else
            g.drawImage(DINOSAUR.image, DINOSAUR.x, DINOSAUR.y, DINOSAUR.width, DINOSAUR.height, null);

        // Draw the cacti
        for (Block cactus : cactusArray) {
            g.drawImage(cactus.image, cactus.x, cactus.y, cactus.width, cactus.height, null);
        }

        // Draw the pterodactyls
        for (Block pterodactyl : pterodactylArray) {
            g.drawImage(pterodactyl.image, pterodactyl.x, pterodactyl.y, pterodactyl.width, pterodactyl.height, null);
        }

        // Write the commands in the bottom center
        g.setColor(new Color(5, 90, 185));
        g.setFont(new Font("Courrier", Font.BOLD, 15));
        g.drawString("Press UP to jump, DOWN to duck", BOARD_WIDTH / 2 - 150, BOARD_HEIGHT - 3);

        // Draw the score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Courrier", Font.PLAIN, 23));
        g.drawString(String.valueOf(score), 10, 30);

        // Draw the high score in the top right corner
        g.setFont(new Font("Courrier", Font.PLAIN, 19));
        g.drawString("High Score: " + highScore, BOARD_WIDTH - 200, 20);

        // If the game is over, draw the game over image
        if (gameOver)
            g.drawImage(GAME_OVER_IMG, BOARD_WIDTH / 2 - GAME_OVER_IMG.getWidth(null) / 2, BOARD_HEIGHT / 2 - GAME_OVER_IMG.getHeight(null) / 2, null);
    }

    /**
     * Moves the dinosaur and cacti, applies gravity, and checks for collisions.
     */
    public void move() {
        // Apply gravity to the dinosaur
        velocityY += GRAVITY;
        DINOSAUR.y += velocityY;

        if (DINOSAUR.y > DINOSAUR_Y) {
            DINOSAUR.y = DINOSAUR_Y;
            velocityY = 0; // Reset velocity when dinosaur lands
            DINOSAUR.image = DINOSAUR_IMG; // Change back to run image
        }

        // Move the cacti
        for (Block cactus : cactusArray) {
            cactus.x += velocityX;

            if (collision(cactus)) {
                gameOver = true; // Set game over flag
                DINOSAUR.image = DINOSAUR_DEAD_IMG; // Change dinosaur image to dead
                return; // Exit the method to prevent further processing
            }
        }

        // Remove cacti that are off-screen
        cactusArray.removeIf(cactus -> cactus.x + cactus.width < 0);

        // Move the pterodactyls
        for (VeloBlock pterodactyl : pterodactylArray) {
            pterodactyl.x += (int) (velocityX * pterodactyl.velocityX); // Pterodactyls move faster than cacti

            if (collision(pterodactyl)) {
                gameOver = true; // Set game over flag
                DINOSAUR.image = DINOSAUR_DEAD_IMG; // Change dinosaur image to dead
                return; // Exit the method to prevent further processing
            }
        }

        // Remove pterodactyls that are off-screen
        pterodactylArray.removeIf(pterodactyl -> pterodactyl.x + pterodactyl.width < 0);

        // Move the clouds
        for (VeloBlock cloud : cloudArray) {
            cloud.x += (int) (velocityX * cloud.velocityX); // Clouds move at a variable speed
        }

        // Remove clouds that are off-screen
        cloudArray.removeIf(cloud -> cloud.x + cloud.width < 0);

        // Move the ground
        groundOffsetX += velocityX;
        if (groundOffsetX <= -GROUND_WIDTH)
            groundOffsetX += GROUND_WIDTH; // Reset ground offset when it goes off-screen

        // Score calculation
        score++;
        if (score > highScore)
            highScore = score; // Update high score

        // Update cactus speed based on score
        if (score % 1_000 == 0 && velocityX > -20) // Increase speed every 1'000 points
            velocityX -= 1; // Make the game harder by increasing cactus speed
    }

    /**
     * Checks for collision between two blocks.
     * @param a The block to check for collision with the dinosaur.
     * @return true if there is a collision, false otherwise.
     */
    private boolean collision(Block a) {
        if (isDucking && DINOSAUR.y == DINOSAUR_Y)
            return DINOSAUR.x < a.x + a.width && DINOSAUR.x + DINOSAUR_DUCK_WIDTH > a.x &&
                    DINOSAUR.y + (DINOSAUR_HEIGHT - DINOSAUR_DUCK_HEIGHT) < a.y + a.height &&
                    DINOSAUR.y + DINOSAUR_DUCK_HEIGHT > a.y;

        return DINOSAUR.x < a.x + a.width && DINOSAUR.x + DINOSAUR.width > a.x &&
                DINOSAUR.y < a.y + a.height && DINOSAUR.y + DINOSAUR.height > a.y;
    }

    /**
     * Shows the reset button when the game is over.
     */
    private void showResetButton() {
        if (resetButton == null) {
            resetButton = new JButton();
            resetButton.setIcon(new ImageIcon(RESET_IMG));
            resetButton.setBounds(BOARD_WIDTH / 2 - RESET_IMG.getWidth(null) / 2, BOARD_HEIGHT / 2 + GAME_OVER_IMG.getHeight(null) / 2 + 10, RESET_IMG.getWidth(null), RESET_IMG.getHeight(null));
            resetButton.setContentAreaFilled(false);
            resetButton.setBorderPainted(false);
            resetButton.addActionListener(_ -> resetGame());
            this.add(resetButton);
            this.repaint();
        }
    }

    /**
     * Resets the game to its initial state.
     */
    private void resetGame() {
        DINOSAUR.y = DINOSAUR_Y;
        DINOSAUR.image = DINOSAUR_IMG;
        velocityY = 0;
        cactusArray.clear();
        pterodactylArray.clear();
        gameOver = false;
        score = 0;
        GAMELOOP.start();
        PLACE_CLOUD_TIMER.start();
        PLACE_CACTUS_TIMER.start();
        if (resetButton != null) {
            this.remove(resetButton);
            resetButton = null; // Clear the reset button reference
            this.repaint();
        }
        this.setFocusable(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        this.repaint();
        if (gameOver) {
            PLACE_CACTUS_TIMER.stop();
            PLACE_CLOUD_TIMER.stop();
            GAMELOOP.stop();
            showResetButton(); // Show reset button when game is over
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> {
                if (DINOSAUR.y == DINOSAUR_Y) {
                    velocityY = -17;
                    DINOSAUR.image = DINOSAUR_JUMP_IMG; // Change to jump image
                }
            }
            case KeyEvent.VK_DOWN -> {
                isDucking = true;
                // If ducking while in the air, velocityY is increased to simulate falling faster
                if (DINOSAUR.y < DINOSAUR_Y)
                    velocityY += GRAVITY * 8; // Increase falling speed while ducking
            }
            case KeyEvent.VK_R -> {
                if (gameOver)
                    resetGame(); // Reset the game if it is over
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN)
            isDucking = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}