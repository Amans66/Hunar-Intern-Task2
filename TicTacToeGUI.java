import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToeGUI extends JFrame implements ActionListener {
    private static final int BOARD_SIZE = 3;
    private static final int TURN_TIME_LIMIT = 15;
    private final JButton[][] buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
    private char currentPlayer = 'X';
    private boolean gameOver = false;
    private String playerXName;
    private String playerOName;
    private int playerXScore = 0;
    private int playerOScore = 0;
    private JLabel playerXLabel;
    private JLabel playerOLabel;
    private JLabel statusLabel;
    private Timer turnTimer;
    private int turnTimeRemaining;

    public TicTacToeGUI() {
        setTitle("Tic-Tac-Toe");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializePlayers();
        initializeBoard();
        initializeScoreBoard();
        initializeStatusLabel();

        startTurnTimer();

        setVisible(true);
    }

    private void initializePlayers() {
        playerXName = JOptionPane.showInputDialog(this, "Enter the name of Player X:", "Player X", JOptionPane.PLAIN_MESSAGE);
        if (playerXName == null || playerXName.trim().isEmpty()) {
            playerXName = "Player X";
        }

        playerOName = JOptionPane.showInputDialog(this, "Enter the name of Player O:", "Player O", JOptionPane.PLAIN_MESSAGE);
        if (playerOName == null || playerOName.trim().isEmpty()) {
            playerOName = "Player O";
        }
    }

    private void initializeBoard() {
        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 5, 5));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        boardPanel.setBackground(new Color(240, 240, 240));

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col] = new JButton("");
                buttons[row][col].setFont(new Font("Arial", Font.PLAIN, 60));
                buttons[row][col].setFocusPainted(false);
                buttons[row][col].setBackground(Color.WHITE);
                buttons[row][col].setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                buttons[row][col].addActionListener(this);
                boardPanel.add(buttons[row][col]);
            }
        }
        add(boardPanel, BorderLayout.CENTER);
    }

    private void initializeScoreBoard() {
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        scorePanel.setPreferredSize(new Dimension(400, 100));
        scorePanel.setBackground(new Color(240, 240, 240));
        scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel playerXNameLabel = new JLabel(playerXName + " (X): ");
        playerXNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerXNameLabel.setForeground(Color.BLUE);
        scorePanel.add(playerXNameLabel);

        playerXLabel = new JLabel(String.valueOf(playerXScore));
        playerXLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerXLabel.setForeground(Color.BLUE);
        scorePanel.add(playerXLabel);

        JLabel playerONameLabel = new JLabel(playerOName + " (O): ");
        playerONameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerONameLabel.setForeground(Color.RED);
        scorePanel.add(playerONameLabel);

        playerOLabel = new JLabel(String.valueOf(playerOScore));
        playerOLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerOLabel.setForeground(Color.RED);
        scorePanel.add(playerOLabel);
        JButton resetScoresButton = new JButton("New Series");
        resetScoresButton.setFont(new Font("Arial", Font.PLAIN, 16));
        resetScoresButton.setBackground(Color.LIGHT_GRAY);
        resetScoresButton.addActionListener(_ -> {
            playerXScore = 0;
            playerOScore = 0;
            updateScoreBoard();
        });
        scorePanel.add(resetScoresButton);

        add(scorePanel, BorderLayout.NORTH);
    }

    private void initializeStatusLabel() {
        statusLabel = new JLabel("Current turn: " + playerXName + " (X)");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(new Color(50, 50, 50));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void startTurnTimer() {
        turnTimeRemaining = TURN_TIME_LIMIT;
        turnTimer = new Timer(1000, _ -> {
            turnTimeRemaining--;
            if (turnTimeRemaining <= 0) {
                turnTimer.stop();
                handleTurnTimeUp();
            }
            statusLabel.setText("Current turn: " + getCurrentPlayerName() + " (" + currentPlayer + ") - Time left: " + turnTimeRemaining + " seconds");
        });
        turnTimer.start();
    }

    private void stopTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
        }
    }

    private void handleTurnTimeUp() {
        JOptionPane.showMessageDialog(this, "Time's up! " + getCurrentPlayerName() + " has exceeded the time limit.");
        changePlayer();
        updateStatusLabel();
        startTurnTimer();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        JButton buttonClicked = (JButton) e.getSource();
        if (!buttonClicked.getText().isEmpty()) return;

        buttonClicked.setText(String.valueOf(currentPlayer));
        buttonClicked.setForeground((currentPlayer == 'X') ? Color.BLUE : Color.RED);

        stopTurnTimer();

        if (checkForWin()) {
            String winner = (currentPlayer == 'X') ? playerXName : playerOName;
            JOptionPane.showMessageDialog(this, "Congratulations " + winner + "! You win!");
            if (currentPlayer == 'X') {
                playerXScore++;
            } else {
                playerOScore++;
            }
            updateScoreBoard();
            gameOver = true;
            askToRestart();
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "The game is a draw!");
            gameOver = true;
            askToRestart();
        } else {
            changePlayer();
            updateStatusLabel();
            startTurnTimer();
        }
    }

    private void changePlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private boolean isBoardFull() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (buttons[row][col].getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkForWin() {

        for (int row = 0; row < BOARD_SIZE; row++) {
            if (buttons[row][0].getText().equals(buttons[row][1].getText()) &&
                    buttons[row][1].getText().equals(buttons[row][2].getText()) &&
                    !buttons[row][0].getText().isEmpty()) {
                return true;
            }
        }

        for (int col = 0; col < BOARD_SIZE; col++) {
            if (buttons[0][col].getText().equals(buttons[1][col].getText()) &&
                    buttons[1][col].getText().equals(buttons[2][col].getText()) &&
                    !buttons[0][col].getText().isEmpty()) {
                return true;
            }
        }

        if (buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][2].getText()) &&
                !buttons[0][0].getText().isEmpty()) {
            return true;
        }
        return buttons[0][2].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][0].getText()) &&
                !buttons[0][2].getText().isEmpty();
    }

    private void updateScoreBoard() {
        playerXLabel.setText(String.valueOf(playerXScore));
        playerOLabel.setText(String.valueOf(playerOScore));
    }

    private void updateStatusLabel() {
        statusLabel.setText("Current turn: " + getCurrentPlayerName() + " (" + currentPlayer + ")");
    }

    private String getCurrentPlayerName() {
        return (currentPlayer == 'X') ? playerXName : playerOName;
    }

    private void askToRestart() {
        int response = JOptionPane.showConfirmDialog(this, "Game over. Would you like to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            resetBoard();
            gameOver = false;
            currentPlayer = 'X';
            updateStatusLabel();
            startTurnTimer();
        } else {
            System.exit(0);
        }
    }

    private void resetBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                buttons[row][col].setText("");
                buttons[row][col].setBackground(Color.WHITE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TicTacToeGUI::new);
    }
}
