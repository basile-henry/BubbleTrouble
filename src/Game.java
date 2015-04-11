import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.tools.jar.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Game class for the game
 */
public class Game extends JFrame {
    int fps = 60;
    int windowWidth = 1300;
    int windowHeight = 650;

    BufferedImage backBuffer;
    Insets insets;
    InputHandler input;

    private ArrayList<Player> players;
    public ArrayList<Bubble> bubbles;
    private Player winner = null;
    private boolean gameOver = false;
    private int level;
    private Random rand = new Random();

    private WelcomePanel welcome;
    private Game game;

    public Game(int numberPlayers, int level) {
        this.game = this;

        players = new ArrayList<Player>();

        if (numberPlayers == 1) {
            players.add(new Player("Player 1", game.windowWidth / 2, game.windowHeight, 0, game.windowWidth));
            players.get(0).setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
        } else {
            players.add(new Player("Player 1", game.windowWidth * 2 / 3, game.windowHeight, 0, game.windowWidth));
            players.add(new Player("Player 2", game.windowWidth / 3, game.windowHeight, 0, game.windowWidth));

            players.get(0).setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
            players.get(1).setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
        }

        this.level = level;

        game.initialize();
        game.play();
    }

    public static void main(String[] args) {

        new Game(Integer.parseInt((args.length > 0)? args[0] : "1"), Integer.parseInt((args.length > 1)? args[1] : "4") );
    }

    void initialize() {
        setTitle("Bubble Trouble");
        setSize(windowWidth, windowHeight);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        insets = getInsets();
        setSize(insets.left + windowWidth + insets.right, insets.top + windowHeight + insets.bottom);
    }

    public void play() {
        //players = welcome.getPlayers();
        //welcome.setVisible(false);

        //game.requestFocus();


        backBuffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
        input = new InputHandler(game);

        reset();
        run();
    }

    void reset() {
        Bubble bbl = new Bubble(windowWidth / 2, windowHeight / level, 0, windowHeight, 0, windowWidth);
        bbl.setGravity(0.2); // 0.15 works well
        bbl.setSize(120);
        bbl.setSpeedX(2);
        Color[] colors = new Color[level];
        for (int i = 0; i < level; i++) {
            colors[i] = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        }
        bbl.setColor(colors);

        bubbles = new ArrayList<Bubble>();
        bubbles.add(bbl);

        for (Player p : players) {
            p.reset();
        }

        winner = null;
        gameOver = false;
    }

    public void run() {
        GameLoop gameLoop = new GameLoop(this);
        gameLoop.run(1.0/fps);
    }

    /**
     * This method will check for input, move things
     * around and check for win conditions, etc
     */
    void update() {
        // Player 1
        if (input.isKeyDown(KeyEvent.VK_RIGHT)) {
            players.get(0).move(6);
        }
        if (input.isKeyDown(KeyEvent.VK_LEFT)) {
            players.get(0).move(-6);
        }
        if (input.isKeyDown(KeyEvent.VK_UP)) {
            players.get(0).shoot();
        }
        if (input.isKeyDown(KeyEvent.VK_SHIFT)) {
            players.get(0).reSize(-1);
        }
        if (input.isKeyDown(KeyEvent.VK_NUMPAD1)) {
            players.get(0).reSize(1);
        }

        if (players.size() > 1) {
            // PLayer 2
            if (input.isKeyDown(KeyEvent.VK_D)) {
                players.get(1).move(6);
            }
            if (input.isKeyDown(KeyEvent.VK_Q)) {
                players.get(1).move(-6);
            }
            if (input.isKeyDown(KeyEvent.VK_Z)) {
                players.get(1).shoot();
            }
            if (input.isKeyDown(KeyEvent.VK_A)) {
                players.get(1).reSize(-1);
            }
            if (input.isKeyDown(KeyEvent.VK_E)) {
                players.get(1).reSize(1);
            }
        }

        if (input.isKeyDown(KeyEvent.VK_R)) {
            reset();
        }

        for (Bubble b : bubbles) {
            b.update();
        }

        int playersAlive = 0;

        for (Player player : players) {
            if (player.isAlive()) {
                playersAlive += 1;
                player.update();

                for (Bubble b : bubbles) {
                    if (testIntersection(b.getShape(), player.getShape())) {
                        playSound("hurt");
                        player.kill();
                        break;
                    }
                }

                if (player.isShooting()) {
                    for (Bubble b : bubbles) {
                        if (testIntersection(b.getShape(), player.getShoot())) {
                            playSound("point");
                            b.destroy(this, player.getShootSpeed());
                            player.setShooting(false);
                            player.setScore(player.getScore() + 1);
                            break;
                        }
                    }
                }
            }
        }

        if (bubbles.size() == 0) {
            int point = 0;

            for (Player player : players) {
                if (player.isAlive() && player.getScore() > point) {
                    winner = player;
                    point = player.getScore();
                }
            }
        }

        if (playersAlive == 0) {
            gameOver = true;
        }
    }

    public static boolean testIntersection(Shape shapeA, Shape shapeB) {
        Area areaA = new Area(shapeA);
        areaA.intersect(new Area(shapeB));
        return !areaA.isEmpty();
    }

    /**
     * This method will draw everything
     */
    void draw() {
        Graphics g = getGraphics();

        Graphics2D bbg = (Graphics2D) backBuffer.getGraphics();

        bbg.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));
        bbg.setColor(Color.WHITE);
        bbg.fillRect(0, 0, windowWidth, windowHeight);



        int fontSize = 18;
        bbg.setFont(new Font(null, Font.PLAIN, fontSize));

        int scoreY = insets.top;

        for (Player player : players) {
            if (player.isShooting()) {
                bbg.setColor(Color.RED);
                player.displayShoot(bbg);
            }

            bbg.setColor(player.getColor());
            bbg.drawString(player.getName() + ": " + Integer.toString(player.getScore()) + " points", 5, scoreY);

            scoreY += fontSize + 5;

            player.display(bbg);
        }

        for (Bubble b : bubbles) {
            b.display(bbg);
        }

        if (winner != null) {
            fontSize = 40;
            bbg.setFont(new Font(null, Font.PLAIN, fontSize));

            bbg.setColor(winner.getColor());
            bbg.drawString(winner.getName() + " wins!", 2 * windowWidth / 5, windowHeight/2);
        }

        if (gameOver) {
            fontSize = 40;
            bbg.setFont(new Font(null, Font.PLAIN, fontSize));

            bbg.setColor(Color.DARK_GRAY);
            bbg.drawString("Game Over", 3 * windowWidth / 7, windowHeight/2);
        }

        g.drawImage(backBuffer, insets.left, insets.top, this);
    }

    public static synchronized void playSound(final String url) {
        try {
            // get the sound file as a resource out of my jar file;
            // the sound file must be in the same directory as this class file.
            // the input stream portion of this recipe comes from a javaworld.com article.
            InputStream inputStream = new FileInputStream("res/" + url + ".wav");
            AudioStream audioStream = new AudioStream(inputStream);
            AudioPlayer.player.start(audioStream);
        } catch (Exception e) {
            // a special way i'm handling logging in this application
            e.printStackTrace();
        }
    }
}