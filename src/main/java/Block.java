import java.awt.Image;

/**
 * Block class representing a block in the game.
 * <p>
 * This class holds the properties of a block, including its position, size, and image representation.
 */
public class Block {
    public int x;
    public int y;
    public int width;
    public int height;
    public Image image;

    /**
     * Constructor for Block class.
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param width The width of the block.
     * @param height The height of the block.
     * @param image The image representing the block.
     */
    Block(int x, int y, int width, int height, Image image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }
}