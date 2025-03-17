import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class BreakOut extends JFrame {
    // Game state variables
    int score = 0;
    int ballXDir = 4, ballYDir = -4; // Ball movement direction (x and y axis)
    int ballX, ballY; // Ball position
    int paddleX, paddleY; // Paddle position
    int paddleWidth = 100, paddleHeight = 10; // Paddle dimensions
    int ballSize = 10; // Ball size
    Timer t, paddleTimer; // Timers for game loop and paddle movement
    int[][] map = new int[4][6]; // Brick map (4 rows, 6 columns)
    boolean moveLeft, moveRight; // Paddle movement flags
    boolean paused = false; // Game paused state

    // Constructor
    public BreakOut(String title) {
        super(title);

        // Basic JFrame settings
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocation(450, 100);
        this.setSize(600, 600);
        this.setResizable(false);
        this.setUndecorated(true); // No window border
        this.setVisible(true);

        // Initialize game variables and objects
        initGame();

        // Main game loop timer (ball movement and collision detection)
        t = new Timer(15, e -> {
            if (!paused) {
                // Ball collision with left and right walls
                if (ballX <= 20 || ballX >= getWidth() - ballSize - 20) {
                    ballXDir = -ballXDir;
                    if (ballX <= 20) ballX = 21; // Prevent sticking
                    if (ballX >= getWidth() - ballSize - 20) ballX = getWidth() - ballSize - 21;
                }

                // Ball collision with top wall
                if (ballY <= 20) {
                    ballYDir = -ballYDir;
                    ballY = 21; // Prevent sticking
                }

                // Ball and paddle collision
                if (new Rectangle(ballX, ballY, ballSize, ballSize)
                        .intersects(new Rectangle(paddleX, paddleY, paddleWidth, paddleHeight))) {
                    ballYDir = -ballYDir;
                }

                // Ball and bricks collision
                int X = 50, Y = 50;
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[i].length; j++) {
                        if (map[i][j] == 0) { // Brick exists
                            Rectangle brickRect = new Rectangle(X + j * (75 + 10), Y + i * (30 + 10), 75, 30);
                            Rectangle ballRect = new Rectangle(ballX, ballY, ballSize, ballSize);

                            if (ballRect.intersects(brickRect)) {
                                score += 10; // Increase score
                                map[i][j] = 1; // Remove brick

                                // Change ball direction based on where it hit
                                if (ballX + ballSize - 1 <= brickRect.x || ballX + 1 >= brickRect.x + brickRect.width) {
                                    ballXDir = -ballXDir;
                                } else {
                                    ballYDir = -ballYDir;
                                }

                                // Win condition
                                if (score >= 240) {
                                    t.stop();
                                    showWinDialog();
                                }
                            }
                        }
                    }
                }

                // Ball falls below paddle (game over)
                if (ballY >= getHeight()) {
                    t.stop();
                    showGameOverDialog();
                }

                // Update ball position
                ballX += ballXDir;
                ballY += ballYDir;

                repaint(); // Redraw
            }
        });

        // Paddle movement timer
        paddleTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused) {
                    if (moveLeft) {
                        paddleX = Math.max(20, paddleX - 5); // Move left
                    }
                    if (moveRight) {
                        paddleX = Math.min(getWidth() - paddleWidth - 20, paddleX + 5); // Move right
                    }
                    repaint();
                }
            }
        });

        // Handle key inputs
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    moveLeft = true;
                    moveRight = false;
                    if (!paddleTimer.isRunning()) paddleTimer.start();
                }
                if (key == KeyEvent.VK_RIGHT) {
                    moveRight = true;
                    moveLeft = false;
                    if (!paddleTimer.isRunning()) paddleTimer.start();
                }
                if (key == KeyEvent.VK_SPACE) { // Pause toggle
                    paused = !paused;
                    if (paused) {
                        paddleTimer.stop();
                    } else {
                        paddleTimer.start();
                    }
                    repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT) {
                    moveLeft = false;
                }
                if (key == KeyEvent.VK_RIGHT) {
                    moveRight = false;
                }
                if (!moveLeft && !moveRight) {
                    paddleTimer.stop();
                }
                repaint();
            }
        });

        t.start(); // Start game loop
    }

    // Initialize game variables (reset positions, score, bricks)
    private void initGame() {
        ballX = getWidth() / 2 - ballSize / 2;
        ballY = getHeight() - 80;
        paddleX = getWidth() / 2 - paddleWidth / 2;
        paddleY = getHeight() - 50;
        score = 0;
        for (int[] ints : map) {
            Arrays.fill(ints, 0);
        }
    }

    // Drawing everything on screen
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Borders
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), 20); // Top
        g.fillRect(0, getHeight() - 20, getWidth(), 20); // Bottom
        g.fillRect(0, 0, 20, getHeight()); // Left
        g.fillRect(getWidth() - 20, 0, 20, getHeight()); // Right

        // Bricks
        int x = 50, y = 50;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == 0) {
                    g.setColor(Color.GRAY);
                    g.fillRect(x + j * (75 + 10), y + i * (30 + 10), 75, 30);
                }
            }
        }

        // Paddle
        g.setColor(Color.GRAY);
        g.fillRoundRect(paddleX, paddleY, paddleWidth, paddleHeight, 5, 5);

        // Ball
        g.setColor(Color.BLACK);
        g.fillOval(ballX, ballY, ballSize, ballSize);

        // Score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, getWidth() - 120, 40);

        // Pause message
        if (paused) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press SPACE to continue", getWidth() / 2 - 100, getHeight() / 2);
        }
    }

    // Win screen dialog
    private void showWinDialog() {
        JFrame winFrame = new JFrame("You Win!");
        winFrame.setSize(400, 300);
        winFrame.setLocationRelativeTo(null);
        winFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        winFrame.setUndecorated(true);

        JPanel winPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(10, 30, 64));
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("YOU WIN!", getWidth() / 2 - 100, getHeight() / 2 - 50);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("Score: " + score, getWidth() / 2 - 70, getHeight() / 2);

                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("Press 'R' to Restart", getWidth() / 2 - 85, getHeight() / 2 + 40);
                g.drawString("Press 'E' to Exit", getWidth() / 2 - 70, getHeight() / 2 + 80);
            }
        };

        winPanel.setLayout(null);
        winFrame.add(winPanel);
        winFrame.setVisible(true);

        // Handle key inputs for win screen
        winFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_R) {
                    initGame();
                    winFrame.dispose();
                    t.start();
                } else if (key == KeyEvent.VK_E) {
                    System.exit(0);
                }
            }
        });

        winFrame.requestFocus();
    }

    // Game over screen dialog
    private void showGameOverDialog() {
        JFrame gameOverFrame = new JFrame("Game Over");
        gameOverFrame.setSize(400, 300);
        gameOverFrame.setLocationRelativeTo(null);
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverFrame.setUndecorated(true);

        JPanel gameOverPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());

                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 40));
                g.drawString("GAME OVER", getWidth() / 2 - 120, getHeight() / 2 - 50);

                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 30));
                g.drawString("Score: " + score, getWidth() / 2 - 70, getHeight() / 2);

                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.PLAIN, 20));
                g.drawString("Press 'R' to Restart", getWidth() / 2 - 85, getHeight() / 2 + 40);
                g.drawString("Press 'E' to Exit", getWidth() / 2 - 75, getHeight() / 2 + 80);
            }
        };

        gameOverPanel.setLayout(null);
        gameOverFrame.add(gameOverPanel);
        gameOverFrame.setVisible(true);

        // Handle key inputs for game over screen
        gameOverFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_R) {
                    initGame();
                    gameOverFrame.dispose();
                    t.start();
                } else if (key == KeyEvent.VK_E) {
                    System.exit(0);
                }
            }
        });

        gameOverFrame.requestFocus();
    }
}

// Main class to start the game
class main {
    public static void main(String[] args) {
        new BreakOut("hi");
    }
}
