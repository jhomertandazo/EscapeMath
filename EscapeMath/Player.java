import javax.swing.ImageIcon;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Player {

    public int x = 60;
    public int y = 300;
    public int width = 42;
    public int height = 48;

    public double velocityX = 0;
    public double velocityY = 0;

    public double gravity = 0.75;
    public double jumpForce = -13;
    public int moveSpeed = 5;

    public boolean left;
    public boolean right;
    public boolean jump;
    public boolean onGround;

    public boolean facingRight = true;

    private Image idleImage;
    private Image jumpImage;
    private Animation walkAnimation;

    public Player() {
        idleImage = loadImage("assets/player/idle.png");
        jumpImage = loadImage("assets/player/jump.png");

        walkAnimation = new Animation(new String[]{
                "assets/player/walk1.png",
                "assets/player/walk2.png",
                "assets/player/walk3.png",
                "assets/player/walk4.png"
        }, 8);
    }

    private Image loadImage(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        return new ImageIcon(path).getImage();
    }

    public void update(ArrayList<Platform> platforms, int screenWidth, int screenHeight) {
        if (left) {
            velocityX = -moveSpeed;
            facingRight = false;
        } else if (right) {
            velocityX = moveSpeed;
            facingRight = true;
        } else {
            velocityX = 0;
        }

        if (jump && onGround) {
            velocityY = jumpForce;
            onGround = false;
        }

        velocityY += gravity;

        moveHorizontal(platforms, screenWidth);
        moveVertical(platforms);

        if (y > screenHeight) {
            resetPosition();
        }

        updateAnimation();
    }

    private void moveHorizontal(ArrayList<Platform> platforms, int screenWidth) {
        x += velocityX;

        Rectangle playerRect = getRect();

        for (Platform p : platforms) {
            if (playerRect.intersects(p.getRect())) {
                if (velocityX > 0) {
                    x = p.x - width;
                } else if (velocityX < 0) {
                    x = p.x + p.width;
                }
                break;
            }
        }

        if (x < 0) {
            x = 0;
        }

        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }

    private void moveVertical(ArrayList<Platform> platforms) {
        y += velocityY;
        onGround = false;

        Rectangle playerRect = getRect();

        for (Platform p : platforms) {
            if (playerRect.intersects(p.getRect())) {
                if (velocityY > 0) {
                    y = p.y - height;
                    velocityY = 0;
                    onGround = true;
                } else if (velocityY < 0) {
                    y = p.y + p.height;
                    velocityY = 0;
                }
                break;
            }
        }
    }

    private void updateAnimation() {
        if ((left || right) && onGround) {
            walkAnimation.update();
        } else {
            walkAnimation.reset();
        }
    }

    public void draw(Graphics2D g2) {
        Image currentImage;

        if (!onGround) {
            currentImage = jumpImage;
        } else if (left || right) {
            currentImage = walkAnimation.getCurrentFrame();
        } else {
            currentImage = idleImage;
        }

        if (currentImage != null) {
            if (facingRight) {
                g2.drawImage(currentImage, x, y, width, height, null);
            } else {
                g2.drawImage(currentImage, x + width, y, -width, height, null);
            }
        } else {
            drawFallback(g2);
        }
    }

    private void drawFallback(Graphics2D g2) {
        g2.setColor(EscapeMathGUI.ACCENT);
        g2.fillRoundRect(x, y, width, height, 12, 12);

        g2.setColor(Color.BLACK);
        g2.fillOval(x + 10, y + 14, 6, 6);
        g2.fillOval(x + 26, y + 14, 6, 6);
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    public void resetPosition() {
        x = 60;
        y = 300;
        velocityX = 0;
        velocityY = 0;
        left = false;
        right = false;
        jump = false;
    }
}