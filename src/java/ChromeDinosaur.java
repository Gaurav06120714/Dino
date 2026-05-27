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
    public final Image CACTUS_BIG_1_IMG;      // NEW: big cacti
    public final Image CACTUS_BIG_2_IMG;
    public final Image CACTUS_BIG_3_IMG;
    public final Image PTERODACTYL_IMG;
    public final Image GROUND_IMG;
    private final int GROUND_HEIGHT;
    private final int GROUND_WIDTH;
    public final Image CLOUD_IMG;
    private final ArrayList<VeloBlock> cloudArray;
    public final Image GAME_OVER_IMG;
    public final Image RESET_IMG;

    // -------- Dinosaur --------
    private final int DINOSAUR_WIDTH       = 88;
    private final int DINOSAUR_HEIGHT      = 94;
    private final int DINOSAUR_DUCK_WIDTH  = 118;
    private final int DINOSAUR_DUCK_HEIGHT = 60;
    private final int DINOSAUR_X;
    private final int DINOSAUR_Y;
    private final Block DINOSAUR;

    // -------- Cactus (small) --------
    private final int CACTUS_1_WIDTH = 34;
    private final int CACTUS_2_WIDTH = 69;
    private final int CACTUS_3_WIDTH = 102;
    private final int CACTUS_HEIGHT  = 70;

    // -------- Cactus (big) — NEW --------
    private final int BIG_CACTUS_WIDTH  = 50;
    private final int BIG_CACTUS_HEIGHT = 96;

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
    private final int GRAVITY    = 1;
    private int groundOffsetX    = 0;

    // -------- Double Jump — NEW --------
    private int  jumpsRemaining  = 2;   // 2 = can jump twice before landing
    private static final int JUMP_VELOCITY = -17;

    // -------- State --------
    private boolean gameOver    = false;
    private boolean gameStarted = false;
    private boolean isDucking   = false;
    private int score           = 0;
    private int level           = 1;

    // -------- Managers — NEW --------
    private final ScoreManager scoreManager = new ScoreManager();
    private final NightMode    nightMode    = new NightMode();

    // -------- Timers --------
    private final Timer GAMELOOP;
    private final Timer PLACE_CACTUS_TIMER;
    private final Timer PLACE_CLOUD_TIMER;

    private JButton resetButton;

    // -------- Fonts --------
    private final Font SCORE_FONT   = new Font("Courier New", Font.BOLD,  18);
    private final Font HISCORE_FONT = new Font("Courier New", Font.PLAIN, 16);
    private final Font HINT_FONT    = new Font("Courier New", Font.BOLD,  14);
    private final Font START_FONT   = new Font("Courier New", Font.BOLD,  20);
    private final Font LEVEL_FONT   = new Font("Courier New", Font.BOLD,  13);

    ChromeDinosaur(int boardWidth, int boardHeight) {
        this.BOARD_WIDTH  = boardWidth;
        this.BOARD_HEIGHT = boardHeight;

        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setFocusable(true);
        this.addKeyListener(this);
        this.setBackground(Color.WHITE);

        // Load images
        DINOSAUR_IMG       = loadImage("images/dino-run.gif");
        DINOSAUR_DEAD_IMG  = loadImage("images/dino-dead.png");
        DINOSAUR_JUMP_IMG  = loadImage("images/dino-jump.png");
        DINOSAUR_DUCK_IMG  = loadImage("images/dino-duck.gif");
        CACTUS_SMALL_1_IMG = loadImage("images/cactus1.png");
        CACTUS_SMALL_2_IMG = loadImage("images/cactus2.png");
        CACTUS_SMALL_3_IMG = loadImage("images/cactus3.png");
        CACTUS_BIG_1_IMG   = loadImage("images/big-cactus1.png");
        CACTUS_BIG_2_IMG   = loadImage("images/big-cactus2.png");
        CACTUS_BIG_3_IMG   = loadImage("images/big-cactus3.png");
        PTERODACTYL_IMG    = loadImage("images/bird.gif");
        GROUND_IMG         = loadImage("images/track.png");
        CLOUD_IMG          = loadImage("images/cloud.png");
        GAME_OVER_IMG      = loadImage("images/game-over.png");
        RESET_IMG          = loadImage("images/reset.png");

        GROUND_HEIGHT = GROUND_IMG.getHeight(null);
        GROUND_WIDTH  = GROUND_IMG.getWidth(null);

        DINOSAUR_X = 50;
        DINOSAUR_Y = BOARD_HEIGHT - DINOSAUR_HEIGHT - 10;
        CACTUS_X   = BOARD_WIDTH;
        CACTUS_Y   = BOARD_HEIGHT - CACTUS_HEIGHT;

        DINOSAUR         = new Block(DINOSAUR_X, DINOSAUR_Y, DINOSAUR_WIDTH, DINOSAUR_HEIGHT, DINOSAUR_IMG);
        cactusArray      = new ArrayList<>();
        pterodactylArray = new ArrayList<>();
        cloudArray       = new ArrayList<>();

        GAMELOOP           = new Timer(1_000 / 60, this);
        PLACE_CACTUS_TIMER = new Timer(1_500, e -> placeCactus());
        PLACE_CLOUD_TIMER  = new Timer(2_000, e -> placeClouds());

        PLACE_CLOUD_TIMER.start();
    }

    private Image loadImage(String path) {
        return new ImageIcon(Objects.requireNonNull(getClass().getResource(path))).getImage();
    }

    // ------------------------------------------------------------------ placing

    public void placeCactus() {
        double chance = Math.random();

        // After level 3 big cacti appear
        boolean bigCactiEnabled = (level >= 3);

        if (chance > .92) {
            int y = BOARD_HEIGHT - BIG_CACTUS_HEIGHT;
            Image img = randomBigCactus();
            cactusArray.add(new Block(CACTUS_X, y, BIG_CACTUS_WIDTH, BIG_CACTUS_HEIGHT, img));
        } else if (chance > .70) {
            cactusArray.add(new Block(CACTUS_X, CACTUS_Y, CACTUS_3_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_3_IMG));
        } else if (chance > .50) {
            cactusArray.add(new Block(CACTUS_X, CACTUS_Y, CACTUS_2_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_2_IMG));
        } else if (chance > .30) {
            cactusArray.add(new Block(CACTUS_X, CACTUS_Y, CACTUS_1_WIDTH, CACTUS_HEIGHT, CACTUS_SMALL_1_IMG));
        } else if (chance > .20) {
            int pteroY = (int)(Math.random() * (BOARD_HEIGHT - PTERODACTYL_HEIGHT - 30)) + 10;
            pterodactylArray.add(new VeloBlock(CACTUS_X, pteroY, PTERODACTYL_WIDTH, PTERODACTYL_HEIGHT, PTERODACTYL_IMG, 1.2));
        }
    }

    private Image randomBigCactus() {
        return switch ((int)(Math.random() * 3)) {
            case 0  -> CACTUS_BIG_1_IMG;
            case 1  -> CACTUS_BIG_2_IMG;
            default -> CACTUS_BIG_3_IMG;
        };
    }

    private void placeClouds() {
        int cloudY      = (int)(Math.random() * ((double) BOARD_HEIGHT / 2));
        int cloudWidth  = (int)(Math.random() * 100 + 50);
        int cloudHeight = (int)(CLOUD_IMG.getHeight(null) * ((double) cloudWidth / CLOUD_IMG.getWidth(null)));
        double cloudVX  = Math.random() + 0.2;
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ── Background (day/night) ──
        Color bg = nightMode.getBackground();
        g2.setColor(bg);
        g2.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        this.setBackground(bg);

        // ── Ground ──
        g.drawImage(GROUND_IMG, groundOffsetX,               BOARD_HEIGHT - GROUND_HEIGHT, GROUND_WIDTH, GROUND_HEIGHT, null);
        g.drawImage(GROUND_IMG, groundOffsetX + GROUND_WIDTH, BOARD_HEIGHT - GROUND_HEIGHT, GROUND_WIDTH, GROUND_HEIGHT, null);

        // ── Clouds ──
        for (Block cloud : cloudArray)
            g.drawImage(cloud.image, cloud.x, cloud.y, cloud.width, cloud.height, null);

        // ── Dinosaur ──
        if (isDucking && DINOSAUR.y == DINOSAUR_Y) {
            int yPos = DINOSAUR_Y + (DINOSAUR_HEIGHT - DINOSAUR_DUCK_HEIGHT);
            g.drawImage(DINOSAUR_DUCK_IMG, DINOSAUR.x, yPos, DINOSAUR_DUCK_WIDTH, DINOSAUR_DUCK_HEIGHT, null);
        } else {
            g.drawImage(DINOSAUR.image, DINOSAUR.x, DINOSAUR.y, DINOSAUR.width, DINOSAUR.height, null);
        }

        // ── Cacti ──
        for (Block cactus : cactusArray)
            g.drawImage(cactus.image, cactus.x, cactus.y, cactus.width, cactus.height, null);

        // ── Pterodactyls ──
        for (Block ptero : pterodactylArray)
            g.drawImage(ptero.image, ptero.x, ptero.y, ptero.width, ptero.height, null);

        // ── HUD ──
        Color textColor    = nightMode.getTextColor();
        Color textDimColor = nightMode.getTextDimColor();
        Color hintColor    = nightMode.getHintColor();

        // Score (top-left)
        g.setColor(textColor);
        g.setFont(SCORE_FONT);
        g.drawString(String.format("%05d", score), 10, 30);

        // All-time best (top-right)
        g.setFont(HISCORE_FONT);
        g.setColor(textDimColor);
        String hiStr = "HI " + String.format("%05d", scoreManager.getAllTimeHigh());
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hiStr, BOARD_WIDTH - fm.stringWidth(hiStr) - 10, 28);

        // Level indicator (centre top)
        g.setFont(LEVEL_FONT);
        g.setColor(textDimColor);
        g.drawString("LVL " + level, BOARD_WIDTH / 2 - 25, 28);

        // Night mode indicator
        if (nightMode.isNight()) {
            g.setFont(HINT_FONT);
            g.setColor(new Color(180, 180, 255));
            g.drawString("🌙", BOARD_WIDTH / 2 + 35, 28);
        }

        // Double-jump indicator (small dots under dino)
        drawJumpIndicator(g);

        // Controls hint (bottom)
        g.setFont(HINT_FONT);
        g.setColor(hintColor);
        String hint = "↑/SPACE Jump (×2)   ↓ Duck   R Restart";
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(hint, BOARD_WIDTH / 2 - fm2.stringWidth(hint) / 2, BOARD_HEIGHT - 4);

        // ── Start screen ──
        if (!gameStarted && !gameOver) {
            drawCenteredBox(g2, "Press  ↑  or  SPACE  to Start", BOARD_HEIGHT / 2 - 20, START_FONT, textColor);
        }

        // ── Game over overlay ──
        if (gameOver) {
            int goW = GAME_OVER_IMG.getWidth(null);
            int goH = GAME_OVER_IMG.getHeight(null);
            g.drawImage(GAME_OVER_IMG, BOARD_WIDTH / 2 - goW / 2, BOARD_HEIGHT / 2 - goH - 10, null);

            g.setFont(HINT_FONT);
            g.setColor(textDimColor);
            String finalStr = "Score: " + String.format("%05d", score)
                            + "   Best: " + String.format("%05d", scoreManager.getAllTimeHigh());
            FontMetrics fm3 = g.getFontMetrics();
            g.drawString(finalStr, BOARD_WIDTH / 2 - fm3.stringWidth(finalStr) / 2, BOARD_HEIGHT / 2 + goH - 5);
        }
    }

    /** Two small dots under the dino show remaining air jumps. */
    private void drawJumpIndicator(Graphics g) {
        if (DINOSAUR.y >= DINOSAUR_Y) return;   // on ground — no indicator needed
        int dotSize = 5;
        int dotY    = DINOSAUR_Y + DINOSAUR_HEIGHT + 4;
        int dotX    = DINOSAUR_X + DINOSAUR_WIDTH / 2 - dotSize;
        for (int i = 0; i < 2; i++) {
            g.setColor(i < jumpsRemaining
                ? new Color(100, 180, 255, 200)
                : new Color(180, 180, 180, 80));
            g.fillOval(dotX + i * (dotSize + 3), dotY, dotSize, dotSize);
        }
    }

    private void drawCenteredBox(Graphics2D g2, String text, int centerY, Font font, Color textColor) {
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        int tw  = fm.stringWidth(text);
        int th  = fm.getHeight();
        int pad = 14;
        int bx  = BOARD_WIDTH / 2 - tw / 2 - pad;
        int by  = centerY - th / 2 - pad / 2;
        int bw  = tw + pad * 2;
        int bh  = th + pad;

        g2.setColor(new Color(128, 128, 128, 60));
        g2.fillRoundRect(bx, by, bw, bh, 12, 12);
        g2.setColor(new Color(128, 128, 128, 120));
        g2.drawRoundRect(bx, by, bw, bh, 12, 12);

        g2.setColor(textColor);
        g2.drawString(text, BOARD_WIDTH / 2 - tw / 2, centerY + th / 4);
    }

    // ------------------------------------------------------------------ move

    public void move() {
        velocityY += GRAVITY;
        DINOSAUR.y += velocityY;

        // Land on ground
        if (DINOSAUR.y > DINOSAUR_Y) {
            DINOSAUR.y    = DINOSAUR_Y;
            velocityY     = 0;
            jumpsRemaining = 2;           // restore double-jump on landing
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
            ptero.x += (int)(velocityX * ptero.velocityX);
            if (collision(ptero)) { triggerGameOver(); return; }
        }
        pterodactylArray.removeIf(p -> p.x + p.width < 0);

        // Clouds
        for (VeloBlock cloud : cloudArray)
            cloud.x += (int)(velocityX * cloud.velocityX);
        cloudArray.removeIf(c -> c.x + c.width < 0);

        // Ground scroll
        groundOffsetX += velocityX;
        if (groundOffsetX <= -GROUND_WIDTH) groundOffsetX += GROUND_WIDTH;

        // Scoring
        score++;
        scoreManager.update(score);
        nightMode.update(score);

        // Speed + level increase every 500 points
        if (score % 500 == 0 && velocityX > -22) {
            velocityX--;
            level = 1 + (INITIAL_VELOCITY_X - velocityX);
            PLACE_CACTUS_TIMER.setDelay(Math.max(700, PLACE_CACTUS_TIMER.getDelay() - 50));
        }
    }

    private void triggerGameOver() {
        gameOver       = true;
        DINOSAUR.image = DINOSAUR_DEAD_IMG;
    }

    // ------------------------------------------------------------------ collision (tighter hitboxes)

    private boolean collision(Block a) {
        // Shrink hitbox by 8px on each side for more forgiving collisions
        final int SHRINK = 8;
        int dx = DINOSAUR.x + SHRINK;
        int dw = (isDucking ? DINOSAUR_DUCK_WIDTH : DINOSAUR.width) - SHRINK * 2;
        int dy = isDucking ? DINOSAUR_Y + (DINOSAUR_HEIGHT - DINOSAUR_DUCK_HEIGHT) : DINOSAUR.y;
        int dh = (isDucking ? DINOSAUR_DUCK_HEIGHT : DINOSAUR.height) - SHRINK;

        return dx            < a.x + a.width  - SHRINK &&
               dx + dw       > a.x            + SHRINK &&
               dy            < a.y + a.height - SHRINK &&
               dy + dh       > a.y;
    }

    // ------------------------------------------------------------------ reset button

    private void showResetButton() {
        if (resetButton == null) {
            resetButton = new JButton();
            resetButton.setIcon(new ImageIcon(RESET_IMG));
            int bx = BOARD_WIDTH  / 2 - RESET_IMG.getWidth(null)  / 2;
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
        DINOSAUR.y     = DINOSAUR_Y;
        DINOSAUR.image = DINOSAUR_IMG;
        velocityY      = 0;
        velocityX      = INITIAL_VELOCITY_X;
        isDucking      = false;
        gameOver       = false;
        score          = 0;
        level          = 1;
        jumpsRemaining = 2;

        scoreManager.resetSession();
        nightMode.reset();

        cactusArray.clear();
        pterodactylArray.clear();

        PLACE_CACTUS_TIMER.setDelay(1_500);
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

        if (!gameStarted) {
            for (VeloBlock cloud : cloudArray) cloud.x -= 1;
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

        // Start game
        if (!gameStarted && !gameOver &&
            (key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE)) {
            gameStarted = true;
            GAMELOOP.start();
            PLACE_CACTUS_TIMER.start();
        }

        // Jump / double-jump
        if (key == KeyEvent.VK_UP || key == KeyEvent.VK_SPACE) {
            if (jumpsRemaining > 0) {
                velocityY      = JUMP_VELOCITY;
                jumpsRemaining--;
                DINOSAUR.image = DINOSAUR_JUMP_IMG;
            }
        }

        // Duck
        if (key == KeyEvent.VK_DOWN) {
            isDucking = true;
            if (DINOSAUR.y < DINOSAUR_Y) velocityY += GRAVITY * 8;
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
