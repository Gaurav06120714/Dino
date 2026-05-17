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
    public final Image DINOSAUR_IMG;
    public final Image DINOSAUR_DEAD_IMG;
    public final Image DINOSAUR_JUMP_IMG;
    public final Image DINOSAUR_DUCK_IMG;
    public final Image CACTUS_SMALL_1_IMG;
    public final Image CACTUS_SMALL_2_IMG;
    public final Image CACTUS_SMALL_3_IMG;
    public final Image PTERODACTYL_IMG;
    public final Image GROUND_IMG;
    private final int GROUND_HEIGHT;
    private final int GROUND_WIDTH;
    public final Image CLOUD_IMG;
    private final ArrayList<VeloBlock> cloudArray;
    public final Image GAME_OVER_IMG;
    public final Image RESET_IMG;

    // -------- Dinosaur --------
    private final int DINOSAUR_WIDTH   = 88;
    private final int DINOSAUR_HEIGHT  = 94;
    private final int DINOSAUR_DUCK_WIDTH  = 118;
    private final int DINOSAUR_DUCK_HEIGHT = 60;
    private final int DINOSAUR_X;
    private final int DINOSAUR_Y;
    private final Block DINOSAUR;

    // -------- Cactus --------
    private final int CACTUS_1_WIDTH = 34;
    private final int CACTUS_2_WIDTH = 69;
    private final int CACTUS_3_WIDTH = 102;
    private final int CACTUS_HEIGHT  = 70;
    private final int CACTUS_X;
    private final int CACTUS_Y;
    private final ArrayList<Block> cactusArray;

    // -------- Pterodactyl --------
    private final int PTERODACTYL_WIDTH  = 90;
    private final int PTERODACTYL_HEIGHT = 60;
    private final ArrayList<VeloBlock> pterodactylArray;

    // -------- Physics --------
    private static final int INITIAL_VELOCITY_X = -12;
    private int velocityX = INITIAL_VELOCITY_X;
    private int velocityY = 0;
    private final int GRAVITY = 1;
    private int groundOffsetX = 0;

    // -------- State --------
    private boolean gameOver  = false;
    private boolean gameStarted = false; // NEW: show start screen first
    private boolean isDucking = false;
    private int score     = 0;
    private int highScore = 0;
    private int level     = 1;   // NEW: difficulty level display

    // -------- Timers --------
    private final Timer GAMELOOP;
    private final Timer PLACE_CACTUS_TIMER;
    private final Timer PLACE_CLOUD_TIMER;

    private JButton resetButton;

    // -------- Fonts --------
    private final Font SCORE_FONT      = new Font("Courier New", Font.BOLD, 18);
    private final Font HISCORE_FONT    = new Font("Courier New", Font.PLAIN, 16);
    private final Font HINT_FONT       = new Font("Courier New", Font.BOLD, 14);
    private final Font START_FONT      = new Font("Courier New", Font.BOLD, 20);
    private final Font LEVEL_FONT      = new Font("Courier New", Font.BOLD, 13);

    ChromeDinosaur(int boardWidth, int boardHeight) {
        this.BOARD_WIDTH  = boardWidth;
        this.BOARD_HEIGHT = boardHeight;

        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(this);
        this.setBackground(Color.WHITE); // White like the real Chrome Dino

        // Load images
        DINOSAUR_IMG      = loadImage("images/dino-run.gif");
        DINOSAUR_DEAD_IMG = loadImage("images/dino-dead.png");
        DINOSAUR_JUMP_IMG = loadImage("images/dino-jump.png");
        DINOSAUR_DUCK_IMG = loadImage("images/dino-duck.gif");
        CACTUS_SMALL_1_IMG = loadImage("images/cactus1.png");
        CACTUS_SMALL_2_IMG = loadImage("images/cactus2.png");
        CACTUS_SMALL_3_IMG = loadImage("images/cactus3.png");
        PTERODACTYL_IMG   = loadImage("images/bird.gif");
        GROUND_IMG        = loadImage("images/track.png");
        CLOUD_IMG         = loadImage("images/cloud.png");
        GAME_OVER_IMG     = loadImage("images/game-over.png");
        RESET_IMG         = loadImage("images/reset.png");

        GROUND_HEIGHT = GROUND_IMG.getHeight(null);
        GROUND_WIDTH  = GROUND_IMG.getWidth(null);

        DINOSAUR_X = 50;
        DINOSAUR_Y = BOARD_HEIGHT - DINOSAUR_HEIGHT - 10;
        CACTUS_X   = BOARD_WIDTH;
        CACTUS_Y   = BOARD_HEIGHT - CACTUS_HEIGHT;

        DINOSAUR        = new Block(DINOSAUR_X, DINOSAUR_Y, DINOSAUR_WIDTH, DINOSAUR_HEIGHT, DINOSAUR_IMG);
        cactusArray     = new ArrayList<>();
        pterodactylArray = new ArrayList<>();
        cloudArray      = new ArrayList<>();

        GAMELOOP = new Timer(1_000 / 60, this);
        PLACE_CACTUS_TIMER = new Timer(1_500, e -> placeCactus());
        PLACE_CLOUD_TIMER  = new Timer(2_000, e -> placeClouds());

        // Don't start timers yet — wait for player to press UP/SPACE
        PLACE_CLOUD_TIMER.start(); // clouds can drift before game starts (visual only)
    }

    private Image loadImage(String path) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();
    }

    // ------------------------------------------------------------------ placing
    public void placeCactus() {
        double chance = Math.random();
        Block cactus = null;
        VeloBlock pterodactyl = null;

        if      (chance > .90) cactus = new Block(CACTUS_X, CACTUS_Y, CACTUS_3_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_3_IMG);
        else if (chance > .70) cactus = new Block(CACTUS_X, CACTUS_Y, CACTUS_2_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_2_IMG);
        else if (chance > .50) cactus = new Block(CACTUS_X, CACTUS_Y, CACTUS_1_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_1_IMG);
        else if (chance > .40) {
            int pteroY = (int) (Math.random() * (BOARD_HEIGHT - PTERODACTYL_HEIGHT - 10));
            pterodactyl = new VeloBlock(CACTUS_X, pteroY, PTERODACTYL_WIDTH, PTERODACTYL_HEIGHT, PTERODACTYL_IMG, 1.2);
        }

        if (cactus      != null) cactusArray.add(cactus);
        if (pterodactyl != null) pterodactylArray.add(pterodactyl);
    }

    private void placeClouds() {
        int cloudY     = (int) (Math.random() * ((double) BOARD_HEIGHT / 2));
        int cloudWidth = (int) (Math.random() * 100 + 50);
        int cloudHeight= (int) (CLOUD_IMG.getHeight(null) * ((double) cloudWidth / CLOUD_IMG.getWidth(null)));
        double cloudVX = Math.random() + 0.2;
        cloudArray.add(new VeloBlock(CACTUS_X, cloudY, cloudWidth, cloudHeight, CLOUD_IMG, cloudVX));
    }

    // ------------------------------------------------------------------ paint
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Ground
        g.drawImage(GROUND_IMG, groundOffsetX, BOARD_HEIGHT - GROUND_HEIGHT, GROUND_WIDTH, GROUND_HEIGHT, null);
        g.drawImage(GROUND_IMG, groundOffsetX + GROUND_WIDTH, BOARD_HEIGHT - GROUND_HEIGHT, GROUND_WIDTH, GROUND_HEIGHT, null);

        // Clouds
        for (Block cloud : cloudArray)
            g.drawImage(cloud.image, cloud.x, cloud.y, cloud.width, cloud.height, null);

        // Dinosaur
        if (isDucking && DINOSAUR.y == DINOSAUR_Y) {
            int yPos = DINOSAUR_Y + (DINOSAUR_HEIGHT - DINOSAUR_DUCK_HEIGHT);
            g.drawImage(DINOSAUR_DUCK_IMG, DINOSAUR.x, yPos, DINOSAUR_DUCK_WIDTH, DINOSAUR_DUCK_HEIGHT, null);
        } else {
            g.drawImage(DINOSAUR.image, DINOSAUR.x, DINOSAUR.y, DINOSAUR.width, DINOSAUR.height, null);
        }

        // Cacti
        for (Block cactus : cactusArray)
            g.drawImage(cactus.image, cactus.x, cactus.y, cactus.width, cactus.height, null);

        // Pterodactyls
        for (Block ptero : pterodactylArray)
            g.drawImage(ptero.image, ptero.x, ptero.y, ptero.width, ptero.height, null);

        // ---- HUD ----
        // Score (top-left)
        g.setColor(new Color(83, 83, 83));
        g.setFont(SCORE_FONT);
        g.drawString(String.format("%05d", score), 10, 30);

        // High score (top-right)
        g.setFont(HISCORE_FONT);
        String hiStr = "HI " + String.format("%05d", highScore);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hiStr, BOARD_WIDTH - fm.stringWidth(hiStr) - 10, 28);

        // Level indicator
        g.setFont(LEVEL_FONT);
        g.setColor(new Color(150, 150, 150));
        g.drawString("LVL " + level, BOARD_WIDTH / 2 - 25, 28);

        // Controls hint (bottom)
        g.setFont(HINT_FONT);
        g.setColor(new Color(180, 180, 180));
        String hint = "↑ Jump   ↓ Duck   R Restart";
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(hint, BOARD_WIDTH / 2 - fm2.stringWidth(hint) / 2, BOARD_HEIGHT - 4);

        // ---- Start screen ----
        if (!gameStarted && !gameOver) {
            drawCenteredBox(g2, "Press  ↑  or  SPACE  to Start", BOARD_HEIGHT / 2 - 20, START_FONT, new Color(83,83,83));
        }

        // ---- Game over overlay ----
        if (gameOver) {
            int goW = GAME_OVER_IMG.getWidth(null);
            int goH = GAME_OVER_IMG.getHeight(null);
            g.drawImage(GAME_OVER_IMG, BOARD_WIDTH / 2 - goW / 2, BOARD_HEIGHT / 2 - goH - 10, null);

            // Final score
            g.setFont(HINT_FONT);
            g.setColor(new Color(100, 100, 100));
            String finalStr = "Score: " + String.format("%05d", score) + "   Best: " + String.format("%05d", highScore);
            FontMetrics fm3 = g.getFontMetrics();
            g.drawString(finalStr, BOARD_WIDTH / 2 - fm3.stringWidth(finalStr) / 2, BOARD_HEIGHT / 2 + goH - 5);
        }
    }

    /** Draws a semi-transparent rounded box with centered text. */
    private void drawCenteredBox(Graphics2D g2, String text, int centerY, Font font, Color textColor) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getHeight();
        int pad = 14;
        int bx = BOARD_WIDTH / 2 - tw / 2 - pad;
        int by = centerY - th / 2 - pad / 2;
        int bw = tw + pad * 2;
        int bh = th + pad;

        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(bx, by, bw, bh, 12, 12);
        g2.setColor(new Color(200, 200, 200));
        g2.drawRoundRect(bx, by, bw, bh, 12, 12);

        g2.setColor(textColor);
        g2.drawString(text, BOARD_WIDTH / 2 - tw / 2, centerY + th / 4);
    }

    // ------------------------------------------------------------------ move
    public void move() {
        velocityY += GRAVITY;
        DINOSAUR.y += velocityY;

        if (DINOSAUR.y > DINOSAUR_Y) {
            DINOSAUR.y = DINOSAUR_Y;
            velocityY = 0;
            DINOSAUR.image = DINOSAUR_IMG;
        }

        // Cacti
        for (Block cactus : cactusArray) {
            cactus.x += velocityX;
            if (collision(cactus)) { triggerGameOver(); return; }
        }
        cactusArray.removeIf(c -> c.x + c.width < 0);

        // Pterodactyls
        for (VeloBlock ptero : pterodactylArray) {
            ptero.x += (int) (velocityX * ptero.velocityX);
            if (collision(ptero)) { triggerGameOver(); return; }
        }
        pterodactylArray.removeIf(p -> p.x + p.width < 0);

        // Clouds (always move, even before game starts for ambience)
        for (VeloBlock cloud : cloudArray)
            cloud.x += (int) (velocityX * cloud.velocityX);
        cloudArray.removeIf(c -> c.x + c.width < 0);

        // Ground scroll
        groundOffsetX += velocityX;
        if (groundOffsetX <= -GROUND_WIDTH) groundOffsetX += GROUND_WIDTH;

        // Scoring
        score++;
        if (score > highScore) highScore = score;

        // Speed & level increase every 500 points
        if (score % 500 == 0 && velocityX > -22) {
            velocityX--;
            level = 1 + (INITIAL_VELOCITY_X - velocityX); // level 1 at start, increases by 1 per speed-up
            PLACE_CACTUS_TIMER.setDelay(Math.max(700, PLACE_CACTUS_TIMER.getDelay() - 50)); // spawn faster too
        }
    }

    private void triggerGameOver() {
        gameOver = true;
        DINOSAUR.image = DINOSAUR_DEAD_IMG;
    }

    // ------------------------------------------------------------------ collision
    private boolean collision(Block a) {
        if (isDucking && DINOSAUR.y == DINOSAUR_Y)
            return DINOSAUR.x < a.x + a.width &&
                   DINOSAUR.x + DINOSAUR_DUCK_WIDTH > a.x &&
                   DINOSAUR.y + (DINOSAUR_HEIGHT - DINOSAUR_DUCK_HEIGHT) < a.y + a.height &&
                   DINOSAUR.y + DINOSAUR_DUCK_HEIGHT > a.y;

        return DINOSAUR.x < a.x + a.width &&
               DINOSAUR.x + DINOSAUR.width > a.x &&
               DINOSAUR.y < a.y + a.height &&
               DINOSAUR.y + DINOSAUR.height > a.y;
    }

    // ------------------------------------------------------------------ reset button
    private void showResetButton() {
        if (resetButton == null) {
            resetButton = new JButton();
            resetButton.setIcon(new ImageIcon(RESET_IMG));
            int bx = BOARD_WIDTH / 2 - RESET_IMG.getWidth(null) / 2;
            int by = BOARD_HEIGHT / 2 + GAME_OVER_IMG.getHeight(null);
            resetButton.setBounds(bx, by, RESET_IMG.getWidth(null), RESET_IMG.getHeight(null));
            resetButton.setContentAreaFilled(false);
            resetButton.setBorderPainted(false);
            resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            resetButton.addActionListener(_ -> resetGame());
            this.setLayout(null);
            this.add(resetButton);
            this.repaint();
        }
    }

    private void resetGame() {
        // FIX: reset all mutable state including velocityX
        DINOSAUR.y     = DINOSAUR_Y;
        DINOSAUR.image = DINOSAUR_IMG;
        velocityY      = 0;
        velocityX      = INITIAL_VELOCITY_X; // BUG FIX: was never reset before
        isDucking      = false;
        gameOver       = false;
        score          = 0;
        level          = 1;
        cactusArray.clear();
        pterodactylArray.clear();

        PLACE_CACTUS_TIMER.setDelay(1_500); // reset spawn rate too
        GAMELOOP.start();
        PLACE_CACTUS_TIMER.start();

        if (resetButton != null) {
            this.remove(resetButton);
            resetButton = null;
            this.repaint();
        }
        this.setFocusable(true);
        this.requestFocus();
    }

    // ------------------------------------------------------------------ action / key
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted) move();

        // Always scroll clouds for visual ambience
        if (!gameStarted) {
            for (VeloBlock cloud : cloudArray)
                cloud.x -= 1;
            cloudArray.removeIf(c -> c.x + c.width < 0);
            groundOffsetX -= 1;
            if (groundOffsetX <= -GROUND_WIDTH) groundOffsetX += GROUND_WIDTH;
        }

        this.repaint();
        if (gameOver) {
            PLACE_CACTUS_TIMER.stop();
            PLACE_CLOUD_TIMER.stop();
            GAMELOOP.stop();
            showResetButton();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Start game on first UP or SPACE press
        if (!gameStarted && !gameOver &&
            (key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE)) {
            gameStarted = true;
            GAMELOOP.start();
            PLACE_CACTUS_TIMER.start();
        }

        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE) {
            if (DINOSAUR.y == DINOSAUR_Y) {
                velocityY = -17;
                DINOSAUR.image = DINOSAUR_JUMP_IMG;
            }
        }

        if (key == KeyEvent.VK_DOWN) {
            isDucking = true;
            if (DINOSAUR.y < DINOSAUR_Y)
                velocityY += GRAVITY * 8;
        }

        if (key == KeyEvent.VK_R && gameOver) resetGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) isDucking = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
