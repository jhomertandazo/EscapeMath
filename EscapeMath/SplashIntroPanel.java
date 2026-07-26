import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SplashIntroPanel extends JPanel {

    EscapeMathGUI game;

    Image[] frames;
    int frameActual = 0;

    javax.swing.Timer timerAnimacion;

    final int TOTAL_FRAMES = 18;

    /*
     * Velocidad de la intro.
     * 80 ms ≈ 12.5 FPS
     * 100 ms ≈ 10 FPS
     * 120 ms = más lento
     */
    final int VELOCIDAD_FRAME = 250;

    public SplashIntroPanel(EscapeMathGUI game) {
        this.game = game;

        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        cargarFrames();

        timerAnimacion = new javax.swing.Timer(VELOCIDAD_FRAME, e -> avanzarFrame());
        timerAnimacion.setRepeats(true);
    }

    private void cargarFrames() {
        frames = new Image[TOTAL_FRAMES];

        for (int i = 0; i < TOTAL_FRAMES; i++) {
            String path = "assets/splash/frame" + (i + 1) + ".png";
            frames[i] = cargarImagen(path);
        }
    }

    private Image cargarImagen(String path) {
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("No se encontró frame de intro: " + path);
            return null;
        }

        System.out.println("Frame cargado: " + path);
        return new ImageIcon(path).getImage();
    }

    public void iniciarIntro() {
        frameActual = 0;

        if (timerAnimacion != null && !timerAnimacion.isRunning()) {
            timerAnimacion.start();
        }

        repaint();
    }

    public void detenerIntro() {
        if (timerAnimacion != null && timerAnimacion.isRunning()) {
            timerAnimacion.stop();
        }
    }

    private void avanzarFrame() {
        frameActual++;

        if (frameActual >= TOTAL_FRAMES) {
            detenerIntro();
            game.mostrarMenuPrincipal();
            return;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        /*
         * Mantiene el estilo pixel art sin suavizar demasiado.
         */
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        dibujarFrame(g2);

        g2.dispose();
    }

    private void dibujarFrame(Graphics2D g2) {
        Image frame = frames[frameActual];

        if (frame != null) {
            g2.drawImage(frame, 0, 0, getWidth(), getHeight(), null);
        } else {
            dibujarFallback(g2);
        }
    }

    private void dibujarFallback(Graphics2D g2) {
        g2.setColor(new Color(18, 20, 28));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(EscapeMathGUI.ACCENT);
        g2.setFont(new Font("SansSerif", Font.BOLD, 42));
        g2.drawString("ESCAPE MATH", getWidth() / 2 - 160, getHeight() / 2 - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.drawString("Cargando intro...", getWidth() / 2 - 75, getHeight() / 2 + 25);
    }
}