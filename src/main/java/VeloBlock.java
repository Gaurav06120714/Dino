import java.awt.Image;

/**
 * VeloBlock class representing a block with a horizontal velocity in the game.
 * <p>
 * This class extends the Block class and adds a velocity property to it.
 */
public class VeloBlock extends Block {
    public double velocityX;

    /**
     * Constructor for VeloBlock class.
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param width The width of the block.
     * @param height The height of the block.
     * @param image The image representing the block.
     * @param velocityX The horizontal velocity of the block.
     */
    VeloBlock(int x, int y, int width, int height, Image image, double velocityX) {
        super(x, y, width, height, image); // Assuming the image is set later
        this.velocityX = velocityX;
    }
}