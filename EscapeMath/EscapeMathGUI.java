import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EscapeMathGUI extends JFrame {

    public static final Color BG        = new Color(18, 20, 28);
    public static final Color PANEL_BG  = new Color(28, 31, 43);
    public static final Color ACCENT    = new Color(0, 200, 170);
    public static final Color ACCENT_2  = new Color(255, 176, 46);
    public static final Color TEXT      = new Color(235, 237, 240);
    public static final Color TEXT_DIM  = new Color(160, 166, 178);
    public static final Color ERROR     = new Color(230, 90, 90);
    public static final Color OK        = new Color(90, 210, 130);
    public static final Color HEART_ON  = new Color(235, 70, 90);

    static final int SEGUNDOS_POR_INTENTO = 30;

    Random aleatorio = new Random();

    String nombreUsuario = "";
    public int nivel;
    public int puntaje;
    int totalPreguntas;
    int salaAnterior = 0;
    double respuestaCorrecta;
    int intentos;
    int segundosRestantes;

    ArrayList<Boolean> historialCorrecto = new ArrayList<>();
    ArrayList<Integer> historialTema = new ArrayList<>();

    int matriz[][] = new int[6][3];
    boolean preguntasUsadas[] = new boolean[6];

    String[][] textoPregunta = new String[6][6];
    double[][] respuestas = new double[6][6];

    javax.swing.Timer temporizador;

    DatabaseManager db = new DatabaseManager();

    CardLayout cards = new CardLayout();
    JPanel root = new JPanel(cards);

    SplashIntroPanel splashIntroPanel;
    JPanel menuPanel;
    CinematicPanel cinematicPanel;
    LevelIntroPanel levelIntroPanel;
    GamePanel2D gamePanel;
    QuestionPanel questionPanel;
    JPanel resultadoPanel;

    JTextField campoUsuario;
    JLabel lblMenuError;

    JLabel lblResultadoTitulo;
    JLabel lblGuardando;
    JTextArea areaEstadisticas;
    JTextArea areaTop3;

    public EscapeMathGUI() {
        super("Escape Math Plataformas");

        cargarBancoPreguntas();
        construirUI();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 560);
        setMinimumSize(new Dimension(700, 560));
        setLocationRelativeTo(null);
        setContentPane(root);

        cards.show(root, "splash");
        setVisible(true);
        splashIntroPanel.iniciarIntro();
    }

    void construirUI() {
        root.setBackground(BG);

        splashIntroPanel = new SplashIntroPanel(this);
        menuPanel = construirMenu();
        cinematicPanel = new CinematicPanel(this);
        levelIntroPanel = new LevelIntroPanel(this);
        gamePanel = new GamePanel2D(this);
        questionPanel = new QuestionPanel(this);
        resultadoPanel = construirResultado();

        root.add(splashIntroPanel, "splash");
        root.add(menuPanel, "menu");
        root.add(cinematicPanel, "cinematic");
        root.add(levelIntroPanel, "levelIntro");
        root.add(gamePanel, "platform");
        root.add(questionPanel, "question");
        root.add(resultadoPanel, "resultado");
    }

    public void mostrarMenuPrincipal() {
        cards.show(root, "menu");
    }

    JPanel construirMenu() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.insets = new Insets(8, 0, 8, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("ESCAPE MATH");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 42));
        titulo.setForeground(ACCENT);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitulo = new JLabel("Juego educativo de plataformas");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitulo.setForeground(TEXT_DIM);
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JTextArea reglas = new JTextArea(
                "Tu misión es escapar del laboratorio matemático.\n\n" +
                "• Muévete con A/D o flechas.\n" +
                "• Salta con W, flecha arriba o espacio.\n" +
                "• Recoge partes de la llave.\n" +
                "• Cada parte activa un ejercicio matemático.\n" +
                "• Tienes 3 vidas por ejercicio.\n" +
                "• Tienes " + SEGUNDOS_POR_INTENTO + " segundos por intento.\n" +
                "• Gana puntos y guarda tu resultado en el ranking."
        );

        reglas.setEditable(false);
        reglas.setFocusable(false);
        reglas.setBackground(BG);
        reglas.setForeground(TEXT);
        reglas.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel lblUsuario = new JLabel("Tu nombre para el ranking:");
        lblUsuario.setForeground(TEXT_DIM);
        lblUsuario.setHorizontalAlignment(SwingConstants.CENTER);

        campoUsuario = new JTextField();
        campoUsuario.setFont(new Font("SansSerif", Font.PLAIN, 16));
        campoUsuario.setHorizontalAlignment(SwingConstants.CENTER);

        lblMenuError = new JLabel(" ");
        lblMenuError.setForeground(ERROR);
        lblMenuError.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnIniciar = crearBoton("Iniciar aventura", ACCENT);
        btnIniciar.addActionListener(e -> validarYIniciar());
        campoUsuario.addActionListener(e -> validarYIniciar());

        JButton btnSalir = crearBoton("Salir", new Color(70, 74, 88));
        btnSalir.addActionListener(e -> System.exit(0));

        gc.gridy = 0;
        p.add(titulo, gc);

        gc.gridy = 1;
        p.add(subtitulo, gc);

        gc.gridy = 2;
        gc.insets = new Insets(22, 40, 18, 40);
        p.add(reglas, gc);

        gc.gridy = 3;
        gc.insets = new Insets(4, 60, 2, 60);
        p.add(lblUsuario, gc);

        gc.gridy = 4;
        p.add(campoUsuario, gc);

        gc.gridy = 5;
        p.add(lblMenuError, gc);

        gc.gridy = 6;
        gc.insets = new Insets(10, 60, 8, 60);
        p.add(btnIniciar, gc);

        gc.gridy = 7;
        p.add(btnSalir, gc);

        return p;
    }

    JButton crearBoton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 15));
        b.setBackground(color);
        b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 26, 10, 26));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    void validarYIniciar() {
        String nombre = campoUsuario.getText().trim();

        if (nombre.isEmpty()) {
            lblMenuError.setText("Escribe tu nombre antes de comenzar.");
            return;
        }

        if (nombre.length() > 50) {
            nombre = nombre.substring(0, 50);
        }

        nombreUsuario = nombre;
        lblMenuError.setText(" ");
        iniciarJuego();
    }

    void iniciarJuego() {
        nivel = 1;
        puntaje = 0;
        totalPreguntas = 0;
        salaAnterior = 0;

        historialCorrecto.clear();
        historialTema.clear();

        for (int i = 0; i < 6; i++) {
            preguntasUsadas[i] = false;
            matriz[i][0] = 0;
            matriz[i][1] = 0;
        }

        gamePanel.resetGameWorld();
        cinematicPanel.resetCinematic();

        cards.show(root, "cinematic");
    }

    public void comenzarPlataformas() {
        cards.show(root, "platform");
        gamePanel.startLevel();
    }

    public void mostrarIntroCambioNivel() {
        int sala = ((nivel - 1) / 5) + 1;

        levelIntroPanel.mostrarIntroNivel(sala);
        cards.show(root, "levelIntro");
    }

    public void continuarDespuesDeIntroNivel() {
        cards.show(root, "platform");
        gamePanel.startLevel();
    }

    public void mostrarIntroFinal() {
        levelIntroPanel.mostrarIntroFinal();
        cards.show(root, "levelIntro");
    }

    public void mostrarResultadoFinalDesdeIntro() {
        finalizarJuego(true);
    }

    public void abrirPanelPregunta() {
        gamePanel.stopLevel();
        prepararPreguntaActual();
        cards.show(root, "question");
    }

    void prepararPreguntaActual() {
        int sala = ((nivel - 1) / 5) + 1;

        if (sala != salaAnterior) {
            for (int i = 0; i < 6; i++) {
                preguntasUsadas[i] = false;
            }
            salaAnterior = sala;
        }

        int pregunta;

        do {
            pregunta = aleatorio.nextInt(5) + 1;
        } while (preguntasUsadas[pregunta]);

        preguntasUsadas[pregunta] = true;

        respuestaCorrecta = respuestas[sala][pregunta];

        String[] nombres = {
                "",
                "Lógica proposicional",
                "Sistemas numéricos",
                "Álgebra básica",
                "Expresiones algebraicas",
                "Fracciones"
        };

        intentos = 3;

        questionPanel.setQuestion(
                "Sala " + sala + " - " + nombres[sala],
                textoPregunta[sala][pregunta],
                puntaje
        );

        questionPanel.setHearts(intentos);
        questionPanel.setFeedback(" ", TEXT_DIM);

        iniciarTemporizador();
    }

    void iniciarTemporizador() {
        detenerTemporizador();

        segundosRestantes = SEGUNDOS_POR_INTENTO;
        questionPanel.setTimer(segundosRestantes);

        temporizador = new javax.swing.Timer(1000, e -> {
            segundosRestantes--;
            questionPanel.setTimer(segundosRestantes);

            if (segundosRestantes <= 0) {
                detenerTemporizador();
                registrarFallo("Tiempo agotado.");
            }
        });

        temporizador.start();
    }

    void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }
    }

    public void procesarRespuestaDesdePanel() {
        String texto = questionPanel.getAnswer().replace(",", ".");

        double respuestaUsuario;

        try {
            respuestaUsuario = Double.parseDouble(texto);
        } catch (NumberFormatException ex) {
            questionPanel.setFeedback("Escribe un número válido.", ERROR);
            return;
        }

        if (Math.abs(respuestaUsuario - respuestaCorrecta) < 0.01) {
            detenerTemporizador();
            registrarAcierto();
        } else {
            registrarFallo("Respuesta incorrecta.");
        }
    }

    void registrarAcierto() {
        int sala = ((nivel - 1) / 5) + 1;
        int pts = intentos == 3 ? 10 : intentos == 2 ? 7 : 5;

        puntaje += pts;
        matriz[sala][0]++;
        historialCorrecto.add(true);
        historialTema.add(sala);

        totalPreguntas++;
        nivel++;

        questionPanel.setFeedback("¡Correcto! +" + pts + " puntos.", OK);
        questionPanel.setEnabledInput(false);

        javax.swing.Timer t = new javax.swing.Timer(700, e -> {
            gamePanel.confirmCorrectAnswer();

            if (nivel > 25) {
                mostrarIntroFinal();
            } else {
                int retoActual = ((nivel - 1) % 5) + 1;

                if (retoActual == 1) {
                    mostrarIntroCambioNivel();
                } else {
                    cards.show(root, "platform");
                    gamePanel.startLevel();
                }
            }
        });

        t.setRepeats(false);
        t.start();
    }

    void registrarFallo(String motivo) {
        detenerTemporizador();

        int sala = ((nivel - 1) / 5) + 1;

        matriz[sala][1]++;
        intentos--;
        questionPanel.setHearts(intentos);

        if (intentos > 0) {
            questionPanel.setFeedback(motivo + " Vidas restantes: " + intentos, ERROR);
            iniciarTemporizador();
        } else {
            historialCorrecto.add(false);
            historialTema.add(sala);
            totalPreguntas++;

            questionPanel.setFeedback(motivo + " Sin vidas restantes.", ERROR);
            questionPanel.setEnabledInput(false);

            javax.swing.Timer t = new javax.swing.Timer(800, e -> finalizarJuego(false));
            t.setRepeats(false);
            t.start();
        }
    }

    JPanel construirResultado() {
        JPanel p = new JPanel(new BorderLayout(0, 14));
        p.setBackground(BG);
        p.setBorder(new EmptyBorder(26, 40, 26, 40));

        lblResultadoTitulo = new JLabel("RESULTADOS", SwingConstants.CENTER);
        lblResultadoTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblResultadoTitulo.setForeground(ACCENT);

        lblGuardando = new JLabel(" ", SwingConstants.CENTER);
        lblGuardando.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblGuardando.setForeground(TEXT_DIM);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setBackground(BG);
        norte.add(lblResultadoTitulo);
        norte.add(lblGuardando);

        areaEstadisticas = new JTextArea();
        areaEstadisticas.setEditable(false);
        areaEstadisticas.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaEstadisticas.setBackground(PANEL_BG);
        areaEstadisticas.setForeground(TEXT);
        areaEstadisticas.setBorder(new EmptyBorder(14, 14, 14, 14));

        JScrollPane scrollStats = new JScrollPane(areaEstadisticas);
        scrollStats.setBorder(BorderFactory.createEmptyBorder());

        areaTop3 = new JTextArea();
        areaTop3.setEditable(false);
        areaTop3.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaTop3.setBackground(PANEL_BG);
        areaTop3.setForeground(TEXT);
        areaTop3.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel lblTop3 = new JLabel("TOP 3 MEJORES PUNTAJES");
        lblTop3.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTop3.setForeground(ACCENT_2);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BG);
        topPanel.add(lblTop3, BorderLayout.NORTH);
        topPanel.add(new JScrollPane(areaTop3), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        botones.setBackground(BG);

        JButton btnReintentar = crearBoton("Jugar de nuevo", ACCENT);
        btnReintentar.addActionListener(e -> cards.show(root, "menu"));

        JButton btnSalir = crearBoton("Salir", new Color(70, 74, 88));
        btnSalir.addActionListener(e -> System.exit(0));

        botones.add(btnReintentar);
        botones.add(btnSalir);

        JPanel sur = new JPanel(new BorderLayout(0, 10));
        sur.setBackground(BG);
        sur.add(topPanel, BorderLayout.CENTER);
        sur.add(botones, BorderLayout.SOUTH);

        p.add(norte, BorderLayout.NORTH);
        p.add(scrollStats, BorderLayout.CENTER);
        p.add(sur, BorderLayout.SOUTH);

        return p;
    }

    void finalizarJuego(boolean gano) {
        detenerTemporizador();
        gamePanel.stopLevel();

        lblResultadoTitulo.setText(gano ? "¡FELICIDADES! ESCAPASTE DEL LABORATORIO" : "GAME OVER");
        lblResultadoTitulo.setForeground(gano ? OK : ERROR);

        areaEstadisticas.setText(construirTextoEstadisticas());
        areaTop3.setText("Guardando tu puntaje...");
        lblGuardando.setText("Conectando con MySQL...");

        cards.show(root, "resultado");
        guardarYMostrarTop3(gano);
    }

    void guardarYMostrarTop3(boolean gano) {
        final String usuarioFinal = nombreUsuario;
        final int puntajeFinal = puntaje;
        final int totalFinal = totalPreguntas;

        SwingWorker<Object[], Void> worker = new SwingWorker<>() {
            @Override
            protected Object[] doInBackground() {
                boolean guardado = db.guardarPuntuacion(usuarioFinal, puntajeFinal, totalFinal, gano);
                List<Puntuacion> top3 = db.obtenerTop3();
                return new Object[]{guardado, top3};
            }

            @Override
            protected void done() {
                try {
                    Object[] resultado = get();

                    boolean guardado = (boolean) resultado[0];

                    @SuppressWarnings("unchecked")
                    List<Puntuacion> top3 = (List<Puntuacion>) resultado[1];

                    lblGuardando.setText(guardado
                            ? "Puntaje guardado en el ranking."
                            : "No se pudo guardar el puntaje. Revisa MySQL y db.properties.");

                    areaTop3.setText(construirTextoTop3(top3));
                } catch (Exception ex) {
                    lblGuardando.setText("No se pudo conectar con la base de datos.");
                    areaTop3.setText("Ranking no disponible.");
                }
            }
        };

        worker.execute();
    }

    String construirTextoTop3(List<Puntuacion> top3) {
        if (top3 == null || top3.isEmpty()) {
            return "Aún no hay puntajes guardados.";
        }

        StringBuilder sb = new StringBuilder();
        String[] medallas = {"1º", "2º", "3º"};

        for (int i = 0; i < top3.size(); i++) {
            Puntuacion p = top3.get(i);

            sb.append(medallas[i])
                    .append(" - ")
                    .append(p.usuario)
                    .append(" : ")
                    .append(p.puntaje)
                    .append(" pts")
                    .append(" (")
                    .append(p.resultado)
                    .append(")\n");
        }

        return sb.toString();
    }

    String construirTextoEstadisticas() {
        StringBuilder sb = new StringBuilder();

        sb.append("Jugador: ").append(nombreUsuario).append("\n");
        sb.append("Puntaje obtenido: ").append(puntaje).append("\n");
        sb.append("Retos respondidos: ").append(totalPreguntas).append("\n\n");

        String[] nombres = {
                "",
                "Lógica proposicional",
                "Sistemas numéricos",
                "Álgebra básica",
                "Expresiones algebraicas",
                "Fracciones"
        };

        for (int i = 1; i <= 5; i++) {
            int correctas = matriz[i][0];
            int incorrectas = matriz[i][1];
            int total = correctas + incorrectas;
            double porcentaje = total > 0 ? (correctas * 100.0) / total : 0;

            sb.append("--------------------------------------\n");
            sb.append("Sala ").append(i).append(" - ").append(nombres[i]).append("\n");
            sb.append("Correctas: ").append(correctas).append("\n");
            sb.append("Incorrectas: ").append(incorrectas).append("\n");
            sb.append(String.format("Porcentaje: %.1f%%\n", porcentaje));
        }

        sb.append("\n========================================\n");
        sb.append("HISTORIAL DE RESPUESTAS\n");
        sb.append("========================================\n");

        for (int i = 0; i < totalPreguntas; i++) {
            sb.append("Pregunta ")
                    .append(i + 1)
                    .append(" | Sala: ")
                    .append(historialTema.get(i))
                    .append(" | ")
                    .append(historialCorrecto.get(i) ? "Correcta" : "Incorrecta")
                    .append("\n");
        }

        return sb.toString();
    }

    void cargarBancoPreguntas() {
        textoPregunta[1][1] = "p = Verdadero, q = Falso\np AND q\n\n1 = Verdadero | 0 = Falso";
        respuestas[1][1] = 0;
        textoPregunta[1][2] = "p = Verdadero, q = Falso\np OR q\n\n1 = Verdadero | 0 = Falso";
        respuestas[1][2] = 1;
        textoPregunta[1][3] = "p = Falso\nNOT p\n\n1 = Verdadero | 0 = Falso";
        respuestas[1][3] = 1;
        textoPregunta[1][4] = "p = Verdadero, q = Verdadero\np implica q\n\n1 = Verdadero | 0 = Falso";
        respuestas[1][4] = 1;
        textoPregunta[1][5] = "p = Falso, q = Falso\np OR q\n\n1 = Verdadero | 0 = Falso";
        respuestas[1][5] = 0;

        textoPregunta[2][1] = "¿8 pertenece a los números naturales?\n\n1 = Sí | 0 = No";
        respuestas[2][1] = 1;
        textoPregunta[2][2] = "¿-15 pertenece a los enteros?\n\n1 = Sí | 0 = No";
        respuestas[2][2] = 1;
        textoPregunta[2][3] = "¿√2 es un número racional?\n\n1 = Sí | 0 = No";
        respuestas[2][3] = 0;
        textoPregunta[2][4] = "¿3.14 es un número irracional?\n\n1 = Sí | 0 = No";
        respuestas[2][4] = 0;
        textoPregunta[2][5] = "¿0 pertenece a los naturales?\n\n1 = Sí | 0 = No";
        respuestas[2][5] = 1;

        textoPregunta[3][1] = "Resuelve: 3x + 6 = 21";
        respuestas[3][1] = 5;
        textoPregunta[3][2] = "Resuelve: 2x - 8 = 10";
        respuestas[3][2] = 9;
        textoPregunta[3][3] = "Resuelve: 5x = 45";
        respuestas[3][3] = 9;
        textoPregunta[3][4] = "Resuelve: 4x + 4 = 20";
        respuestas[3][4] = 4;
        textoPregunta[3][5] = "Resuelve: 6x - 12 = 24";
        respuestas[3][5] = 6;

        textoPregunta[4][1] = "Simplifica: 3x + 4x - 2x";
        respuestas[4][1] = 5;
        textoPregunta[4][2] = "Simplifica: 6a - 2a + a";
        respuestas[4][2] = 5;
        textoPregunta[4][3] = "Simplifica: 8y + 3y - 5y";
        respuestas[4][3] = 6;
        textoPregunta[4][4] = "Simplifica: 10m - 4m + 2m";
        respuestas[4][4] = 8;
        textoPregunta[4][5] = "Simplifica: 9p - 3p + 2p";
        respuestas[4][5] = 8;

        textoPregunta[5][1] = "Resuelve: 1/2 + 1/4\nEscribe el resultado decimal";
        respuestas[5][1] = 0.75;
        textoPregunta[5][2] = "Resuelve: 3/4 - 1/2\nEscribe el resultado decimal";
        respuestas[5][2] = 0.25;
        textoPregunta[5][3] = "Resuelve: 2/3 + 1/3\nEscribe el resultado decimal";
        respuestas[5][3] = 1;
        textoPregunta[5][4] = "Resuelve: 5/6 - 2/6\nEscribe el resultado decimal";
        respuestas[5][4] = 0.5;
        textoPregunta[5][5] = "Resuelve: 2/5 × 3/4\nEscribe el resultado decimal";
        respuestas[5][5] = 0.3;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EscapeMathGUI::new);
    }
}