import java.awt.Image;

public class Block {
    public int x;        // x position of the block on screen
    public int y;        // y position of the block on screen
    public int width;    // width of the block
    public int height;   // height of the block
    public Image image;  // image used to display the block

    Block(int x, int y, int width, int height, Image image) {
        this.x = x;          // assign x coordinate
        this.y = y;          // assign y coordinate
        this.width = width;  // assign block width
        this.height = height;// assign block height
        this.image = image;  // assign block image
    }
}
