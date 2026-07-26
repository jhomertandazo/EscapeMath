import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class QuestionPanel extends JPanel {

    EscapeMathGUI game;

    JLabel titleLabel;
    JLabel heartsLabel;
    JLabel timerLabel;
    JLabel scoreLabel;
    JTextArea questionArea;
    JTextField answerField;
    JButton answerButton;
    JLabel feedbackLabel;

    public QuestionPanel(EscapeMathGUI game) {
        this.game = game;

        setLayout(new BorderLayout(12, 12));
        setBackground(EscapeMathGUI.BG);
        setBorder(new EmptyBorder(24, 36, 24, 36));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(EscapeMathGUI.BG);

        titleLabel = new JLabel("Ejercicio matemático");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(EscapeMathGUI.ACCENT);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        statusPanel.setBackground(EscapeMathGUI.BG);

        heartsLabel = new JLabel("❤❤❤");
        heartsLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        heartsLabel.setForeground(EscapeMathGUI.HEART_ON);

        timerLabel = new JLabel("⏱ 30s");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        timerLabel.setForeground(EscapeMathGUI.TEXT_DIM);

        scoreLabel = new JLabel("Puntaje: 0");
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        scoreLabel.setForeground(EscapeMathGUI.ACCENT_2);

        statusPanel.add(scoreLabel);
        statusPanel.add(heartsLabel);
        statusPanel.add(timerLabel);

        top.add(titleLabel, BorderLayout.WEST);
        top.add(statusPanel, BorderLayout.EAST);

        JPanel card = new JPanel(new BorderLayout(12, 12));
        card.setBackground(EscapeMathGUI.PANEL_BG);
        card.setBorder(new EmptyBorder(24, 24, 24, 24));

        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setFocusable(false);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setFont(new Font("SansSerif", Font.PLAIN, 20));
        questionArea.setBackground(EscapeMathGUI.PANEL_BG);
        questionArea.setForeground(EscapeMathGUI.TEXT);

        answerField = new JTextField();
        answerField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        answerField.setBackground(new Color(40, 44, 58));
        answerField.setForeground(EscapeMathGUI.TEXT);
        answerField.setCaretColor(EscapeMathGUI.TEXT);

        answerButton = new JButton("Responder");
        answerButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        answerButton.setBackground(EscapeMathGUI.ACCENT);
        answerButton.setForeground(Color.BLACK);
        answerButton.setFocusPainted(false);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(EscapeMathGUI.PANEL_BG);
        inputPanel.add(answerField, BorderLayout.CENTER);
        inputPanel.add(answerButton, BorderLayout.EAST);

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        card.add(questionArea, BorderLayout.CENTER);
        card.add(inputPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        add(feedbackLabel, BorderLayout.SOUTH);

        answerButton.addActionListener(e -> game.procesarRespuestaDesdePanel());
        answerField.addActionListener(e -> game.procesarRespuestaDesdePanel());
    }

    public void setQuestion(String title, String question, int score) {
        titleLabel.setText(title);
        questionArea.setText(question);
        answerField.setText("");
        feedbackLabel.setText(" ");
        scoreLabel.setText("Puntaje: " + score);
        setEnabledInput(true);

        SwingUtilities.invokeLater(() -> answerField.requestFocusInWindow());
    }

    public String getAnswer() {
        return answerField.getText().trim();
    }

    public void setFeedback(String text, Color color) {
        feedbackLabel.setText(text);
        feedbackLabel.setForeground(color);
    }

    public void setHearts(int lives) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < lives; i++) {
            sb.append("❤");
        }

        for (int i = lives; i < 3; i++) {
            sb.append("♡");
        }

        heartsLabel.setText(sb.toString());
    }

    public void setTimer(int seconds) {
        timerLabel.setText("⏱ " + seconds + "s");
        timerLabel.setForeground(seconds <= 10 ? EscapeMathGUI.ERROR : EscapeMathGUI.TEXT_DIM);
    }

    public void setEnabledInput(boolean enabled) {
        answerField.setEnabled(enabled);
        answerButton.setEnabled(enabled);
    }
}