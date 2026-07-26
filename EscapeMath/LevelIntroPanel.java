import javax.swing.*;
import java.awt.*;
import java.io.File;

public class LevelIntroPanel extends JPanel {

    EscapeMathGUI game;

    Image[] escenas = new Image[2];
    String[] titulos = new String[2];
    String[] textos = new String[2];

    int escenaActual = 0;
    boolean esFinal = false;

    JButton btnContinuar;

    public LevelIntroPanel(EscapeMathGUI game) {
        this.game = game;

        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        btnContinuar = new JButton("Continuar");
        btnContinuar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnContinuar.setBackground(EscapeMathGUI.ACCENT);
        btnContinuar.setForeground(Color.BLACK);
        btnContinuar.setFocusPainted(false);

        btnContinuar.addActionListener(e -> avanzarEscena());

        add(btnContinuar, BorderLayout.SOUTH);
    }

    private Image loadImage(String path) {
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("No se encontró imagen de intro: " + path);
            return null;
        }

        return new ImageIcon(path).getImage();
    }

    public void mostrarIntroNivel(int sala) {
        esFinal = false;
        escenaActual = 0;

        escenas[0] = loadImage("assets/level_intro/sala" + sala + "_1.png");
        escenas[1] = loadImage("assets/level_intro/sala" + sala + "_2.png");

        configurarTextoSala(sala);

        btnContinuar.setText("Continuar");
        repaint();
    }

    public void mostrarIntroFinal() {
        esFinal = true;
        escenaActual = 0;

        escenas[0] = loadImage("assets/level_intro/final1.png");
        escenas[1] = loadImage("assets/level_intro/final2.png");

        titulos[0] = "¡Llave completada!";
        textos[0] = "Has reunido todos los fragmentos de la llave. La salida del laboratorio empieza a desbloquearse.";

        titulos[1] = "La puerta final está abierta";
        textos[1] = "El escape está frente a ti. Tu desempeño será guardado en el ranking final.";

        btnContinuar.setText("Continuar");
        repaint();
    }

    private void configurarTextoSala(int sala) {
        if (sala == 2) {
            titulos[0] = "Sala 2: Sistemas Numéricos";
            textos[0] = "Has superado la primera sala. Nuevos caminos aparecen frente a ti.";

            titulos[1] = "Nuevo desafío";
            textos[1] = "Ahora deberás reconocer conjuntos numéricos y resolver retos para conseguir más fragmentos.";

        } else if (sala == 3) {
            titulos[0] = "Sala 3: Álgebra Básica";
            textos[0] = "La llave reacciona a tus respuestas. El laboratorio cambia de forma.";

            titulos[1] = "Ecuaciones bloqueadas";
            textos[1] = "Resuelve ecuaciones para avanzar entre plataformas y desbloquear el siguiente fragmento.";

        } else if (sala == 4) {
            titulos[0] = "Sala 4: Expresiones Algebraicas";
            textos[0] = "Las plataformas se vuelven más inestables. Cada salto requiere precisión.";

            titulos[1] = "Simplifica para continuar";
            textos[1] = "Simplifica expresiones algebraicas y recoge las piezas restantes de la llave.";

        } else if (sala == 5) {
            titulos[0] = "Sala 5: Fracciones";
            textos[0] = "Estás cerca de la salida. El último escenario aparece frente a ti.";

            titulos[1] = "Reto final";
            textos[1] = "Resuelve operaciones con fracciones para completar la llave y escapar del laboratorio.";

        } else {
            titulos[0] = "Nueva Sala";
            textos[0] = "Prepárate para un nuevo reto matemático.";

            titulos[1] = "Continúa la aventura";
            textos[1] = "Recoge fragmentos y resuelve ejercicios para avanzar.";
        }
    }

    private void avanzarEscena() {
        if (escenaActual == 0) {
            escenaActual = 1;

            if (esFinal) {
                btnContinuar.setText("Ver resultados");
            } else {
                btnContinuar.setText("Entrar a la sala");
            }

            repaint();
            return;
        }

        if (esFinal) {
            game.mostrarResultadoFinalDesdeIntro();
        } else {
            game.continuarDespuesDeIntroNivel();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        Image imagenActual = escenas[escenaActual];

        if (imagenActual != null) {
            g2.drawImage(imagenActual, 0, 0, getWidth(), getHeight(), null);
        } else {
            dibujarFondoFallback(g2);
        }

        dibujarCajaTexto(g2);
    }

    private void dibujarFondoFallback(Graphics2D g2) {
        g2.setColor(new Color(18, 20, 28));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(EscapeMathGUI.ACCENT);
        g2.setFont(new Font("SansSerif", Font.BOLD, 36));
        g2.drawString("ESCAPE MATH", 210, 170);
    }

    private void dibujarCajaTexto(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 178));
        g2.fillRoundRect(40, getHeight() - 170, getWidth() - 80, 110, 20, 20);

        g2.setColor(EscapeMathGUI.ACCENT_2);
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        g2.drawString(titulos[escenaActual], 70, getHeight() - 125);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));

        drawMultilineText(
                g2,
                textos[escenaActual],
                70,
                getHeight() - 95,
                getWidth() - 140,
                22
        );
    }

    private void drawMultilineText(Graphics2D g2, String text, int x, int y, int maxWidth, int lineHeight) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        String line = "";

        for (String word : words) {
            String testLine = line + word + " ";

            if (fm.stringWidth(testLine) > maxWidth) {
                g2.drawString(line, x, y);
                line = word + " ";
                y += lineHeight;
            } else {
                line = testLine;
            }
        }

        if (!line.isEmpty()) {
            g2.drawString(line, x, y);
        }
    }
}