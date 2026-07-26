import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class GamePanel2D extends JPanel implements ActionListener, KeyListener {

    public static final int WIDTH = 672;
    public static final int HEIGHT = 480;

    EscapeMathGUI game;

    Player player;

    Image backgroundImage;
    Image keyImage;

    javax.swing.Timer loop;

    ArrayList<Platform> platforms = new ArrayList<>();
    ArrayList<KeyFragment> keyFragments = new ArrayList<>();

    int pendingFragment = -1;
    int collectedFragments = 0;
    int currentRoom = 1;

    boolean questionOpened = false;

    // Déjalo en true para mostrar las plataformas de madera.
    // Si lo pones en false, no se verán las plataformas, pero seguirán funcionando.
    boolean showDebugPlatforms = true;

    public GamePanel2D(EscapeMathGUI game) {
        this.game = game;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        player = new Player();
        loop = new javax.swing.Timer(16, this);

        loadImages();
        setupRoom(1);
    }

    private Image loadImage(String path) {
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("No se encontró la imagen: " + path);
            return null;
        }

        return new ImageIcon(path).getImage();
    }

    private void loadImages() {
        backgroundImage = loadImage("assets/backgrounds/escenario1.png");
        keyImage = loadImage("assets/items/key_part.png");
    }

    public void startLevel() {
        int room = ((game.nivel - 1) / 5) + 1;

        if (room != currentRoom) {
            currentRoom = room;
            setupRoom(room);
        }

        questionOpened = false;
        pendingFragment = -1;

        player.left = false;
        player.right = false;
        player.jump = false;

        SwingUtilities.invokeLater(() -> {
            requestFocus();
            requestFocusInWindow();
        });

        if (!loop.isRunning()) {
            loop.start();
        }
    }

    public void stopLevel() {
        if (loop != null && loop.isRunning()) {
            loop.stop();
        }
    }

    public void resetGameWorld() {
        currentRoom = 1;
        setupRoom(1);
    }

    private void setupRoom(int room) {
        collectedFragments = 0;
        pendingFragment = -1;
        questionOpened = false;

        player.resetPosition();

        backgroundImage = loadImage("assets/backgrounds/escenario" + room + ".png");

        createPlatforms(room);
        createKeyFragments(room);
    }

    private void createPlatforms(int room) {
        platforms.clear();

        /*
         * IMPORTANTE:
         * Esta primera plataforma es el suelo invisible.
         * No se dibuja, pero el jugador puede caminar encima.
         */
        platforms.add(new Platform(0, 430, 672, 50));

        if (room == 1) {
            platforms.add(new Platform(80, 360, 150, 20));
            platforms.add(new Platform(290, 310, 150, 20));
            platforms.add(new Platform(500, 250, 120, 20));

        } else if (room == 2) {
            platforms.add(new Platform(70, 365, 130, 20));
            platforms.add(new Platform(260, 315, 130, 20));
            platforms.add(new Platform(450, 260, 160, 20));
            platforms.add(new Platform(120, 205, 120, 20));

        } else if (room == 3) {
            platforms.add(new Platform(90, 370, 140, 20));
            platforms.add(new Platform(280, 320, 100, 20));
            platforms.add(new Platform(460, 270, 130, 20));
            platforms.add(new Platform(250, 200, 130, 20));

        } else if (room == 4) {
            platforms.add(new Platform(60, 365, 120, 20));
            platforms.add(new Platform(230, 315, 120, 20));
            platforms.add(new Platform(410, 260, 120, 20));
            platforms.add(new Platform(540, 205, 100, 20));

        } else {
            platforms.add(new Platform(80, 360, 130, 20));
            platforms.add(new Platform(260, 300, 130, 20));
            platforms.add(new Platform(450, 240, 130, 20));
            platforms.add(new Platform(250, 170, 150, 20));
        }
    }

    private void createKeyFragments(int room) {
        keyFragments.clear();

        if (room == 1) {
            keyFragments.add(new KeyFragment(130, 320));
            keyFragments.add(new KeyFragment(340, 270));
            keyFragments.add(new KeyFragment(540, 210));
            keyFragments.add(new KeyFragment(570, 390));
            keyFragments.add(new KeyFragment(300, 390));

        } else if (room == 2) {
            keyFragments.add(new KeyFragment(110, 325));
            keyFragments.add(new KeyFragment(300, 275));
            keyFragments.add(new KeyFragment(500, 225));
            keyFragments.add(new KeyFragment(160, 170));
            keyFragments.add(new KeyFragment(560, 390));

        } else if (room == 3) {
            keyFragments.add(new KeyFragment(140, 335));
            keyFragments.add(new KeyFragment(315, 285));
            keyFragments.add(new KeyFragment(500, 235));
            keyFragments.add(new KeyFragment(290, 165));
            keyFragments.add(new KeyFragment(560, 390));

        } else if (room == 4) {
            keyFragments.add(new KeyFragment(95, 325));
            keyFragments.add(new KeyFragment(265, 275));
            keyFragments.add(new KeyFragment(445, 225));
            keyFragments.add(new KeyFragment(570, 170));
            keyFragments.add(new KeyFragment(320, 390));

        } else {
            keyFragments.add(new KeyFragment(120, 325));
            keyFragments.add(new KeyFragment(300, 265));
            keyFragments.add(new KeyFragment(490, 205));
            keyFragments.add(new KeyFragment(300, 135));
            keyFragments.add(new KeyFragment(560, 390));
        }
    }

    public void confirmCorrectAnswer() {
        if (pendingFragment >= 0 && pendingFragment < keyFragments.size()) {
            KeyFragment fragment = keyFragments.get(pendingFragment);

            if (!fragment.collected) {
                fragment.collected = true;
                collectedFragments++;
            }
        }

        pendingFragment = -1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        player.update(platforms, WIDTH, HEIGHT);
        checkKeyFragments();
        repaint();
    }

    private void checkKeyFragments() {
        if (questionOpened) {
            return;
        }

        Rectangle playerRect = player.getRect();

        for (int i = 0; i < keyFragments.size(); i++) {
            KeyFragment fragment = keyFragments.get(i);

            if (!fragment.collected && playerRect.intersects(fragment.getRect())) {
                questionOpened = true;
                pendingFragment = i;
                game.abrirPanelPregunta();
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        drawBackground(g2);

        /*
         * Primero dibujamos las plataformas de madera.
         * Luego las llaves y el jugador encima.
         */
        if (showDebugPlatforms) {
            drawDebugPlatforms(g2);
        }

        drawKeyFragments(g2);
        player.draw(g2);
        drawHUD(g2);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);
        } else {
            g2.setColor(new Color(30, 32, 42));
            g2.fillRect(0, 0, WIDTH, HEIGHT);
        }
    }

    private void drawKeyFragments(Graphics2D g2) {
        for (KeyFragment fragment : keyFragments) {
            if (!fragment.collected) {
                if (keyImage != null) {
                    g2.drawImage(
                            keyImage,
                            fragment.x,
                            fragment.y,
                            fragment.width,
                            fragment.height,
                            null
                    );
                } else {
                    g2.setColor(EscapeMathGUI.ACCENT_2);
                    g2.fillOval(
                            fragment.x,
                            fragment.y,
                            fragment.width,
                            fragment.height
                    );
                }
            }
        }
    }

    /*
     * Este método dibuja solo las plataformas voladoras.
     * La primera plataforma, índice 0, es el suelo invisible y no se dibuja.
     */
    private void drawDebugPlatforms(Graphics2D g2) {
        for (int i = 0; i < platforms.size(); i++) {
            Platform platform = platforms.get(i);

            // El suelo invisible no se dibuja.
            if (i == 0) {
                continue;
            }

            // Sombra de la plataforma
            g2.setColor(new Color(55, 30, 12, 120));
            g2.fillRoundRect(
                    platform.x + 4,
                    platform.y + 5,
                    platform.width,
                    platform.height,
                    12,
                    12
            );

            // Base de madera
            g2.setColor(new Color(130, 75, 35));
            g2.fillRoundRect(
                    platform.x,
                    platform.y,
                    platform.width,
                    platform.height,
                    12,
                    12
            );

            // Parte superior clara
            g2.setColor(new Color(190, 125, 60));
            g2.fillRoundRect(
                    platform.x,
                    platform.y,
                    platform.width,
                    6,
                    10,
                    10
            );

            // Borde oscuro
            g2.setColor(new Color(75, 40, 18));
            g2.drawRoundRect(
                    platform.x,
                    platform.y,
                    platform.width,
                    platform.height,
                    12,
                    12
            );

            // Líneas decorativas tipo madera
            g2.setColor(new Color(95, 52, 24));

            int middleY = platform.y + platform.height / 2;

            g2.drawLine(
                    platform.x + 10,
                    middleY,
                    platform.x + platform.width - 10,
                    middleY
            );

            g2.drawLine(
                    platform.x + 20,
                    platform.y + 5,
                    platform.x + 45,
                    platform.y + platform.height - 5
            );

            g2.drawLine(
                    platform.x + platform.width - 50,
                    platform.y + 5,
                    platform.x + platform.width - 20,
                    platform.y + platform.height - 5
            );
        }
    }

    private void drawHUD(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(10, 10, 440, 88, 14, 14);

        g2.setColor(EscapeMathGUI.TEXT);
        g2.setFont(new Font("SansSerif", Font.BOLD, 16));
        g2.drawString("Escape Math Plataformas", 24, 34);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g2.setColor(EscapeMathGUI.TEXT_DIM);
        g2.drawString("Mover: A/D o flechas. Saltar: W, ↑ o espacio.", 24, 56);

        int room = ((game.nivel - 1) / 5) + 1;

        g2.setColor(EscapeMathGUI.ACCENT_2);
        g2.drawString(
                "Sala: " + room + " | Partes de llave: " + collectedFragments + "/5",
                24,
                78
        );

        g2.setColor(EscapeMathGUI.TEXT);
        g2.drawString("Puntaje: " + game.puntaje, 285, 78);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            player.left = true;
        }

        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            player.right = true;
        }

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE) {
            player.jump = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
            player.left = false;
        }

        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
            player.right = false;
        }

        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE) {
            player.jump = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}