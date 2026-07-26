import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;

public class Animation {

    private Image[] frames;
    private int currentFrame = 0;
    private int counter = 0;
    private int speed;

    public Animation(String[] paths, int speed) {
        this.speed = speed;
        frames = new Image[paths.length];

        for (int i = 0; i < paths.length; i++) {
            frames[i] = loadImage(paths[i]);
        }
    }

    private Image loadImage(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        return new ImageIcon(path).getImage();
    }

    public void update() {
        counter++;

        if (counter >= speed) {
            currentFrame++;
            counter = 0;

            if (currentFrame >= frames.length) {
                currentFrame = 0;
            }
        }
    }

    public Image getCurrentFrame() {
        return frames[currentFrame];
    }

    public void reset() {
        currentFrame = 0;
        counter = 0;
    }
}