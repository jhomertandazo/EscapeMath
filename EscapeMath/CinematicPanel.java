import javax.swing.*;
import java.awt.*;
import java.io.File;

public class CinematicPanel extends JPanel {

    EscapeMathGUI game;

    Image[] scenes;
    String[] texts;

    int currentScene = 0;

    JButton nextButton;

    public CinematicPanel(EscapeMathGUI game) {
        this.game = game;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        scenes = new Image[]{
                loadImage("assets/cinematic/intro1.png"),
                loadImage("assets/cinematic/intro2.png"),
                loadImage("assets/cinematic/intro3.png")
        };

        texts = new String[]{
                "El laboratorio matemático ha sido bloqueado.",
                "La llave de salida se dividió en fragmentos.",
                "Recoge cada parte y resuelve los retos para escapar."
        };

        nextButton = new JButton("Continuar");
        nextButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        nextButton.setBackground(EscapeMathGUI.ACCENT);
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> nextScene());

        add(nextButton, BorderLayout.SOUTH);
    }

    private Image loadImage(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        return new ImageIcon(path).getImage();
    }

    private void nextScene() {
        currentScene++;

        if (currentScene >= scenes.length) {
            game.comenzarPlataformas();
        } else {
            repaint();
        }
    }

    public void resetCinematic() {
        currentScene = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        if (scenes[currentScene] != null) {
            g2.drawImage(scenes[currentScene], 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(new Color(20, 22, 30));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(EscapeMathGUI.ACCENT);
            g2.setFont(new Font("SansSerif", Font.BOLD, 36));
            g2.drawString("ESCAPE MATH", 210, 180);
        }

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(40, getHeight() - 140, getWidth() - 80, 80, 18, 18);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString(texts[currentScene], 65, getHeight() - 95);
    }
}