import java.awt.Rectangle;

public class KeyFragment {

    public int x;
    public int y;
    public int width = 32;
    public int height = 32;
    public boolean collected = false;

    public KeyFragment(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
}