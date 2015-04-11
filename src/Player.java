import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.tools.jar.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.InputStream;

public class Player {

    private Polygon polygon;
    private Color color = Color.ORANGE;
    private int size = 70;
    private double speed = 0.5;
    private int startPositionX, positionX, positionY, min, max;
    private int minSize = 30;
    private int maxSize = 130;

    private boolean isShooting;
    private int shootHeight;
    private int shootSpeed;
    private int shootPosition;
    private Color shootingColor = Color.RED;

    private int score;
    private boolean alive;
    private String name;

    public Player(String name, int positionX, int positionY, int min, int max) {
        this.startPositionX = positionX;
        this.positionX = positionX;
        this.positionY = positionY;
        this.min = min;
        this.max = max;

        isShooting = false;
        alive = true;
        this.name = name;
        score = 0;
    }

    public void reset() {
        this.positionX = startPositionX;

        isShooting = false;
        alive = true;
        score = 0;
    }

    //Draw the triangle with current specifications on screen.
    public void display(Graphics2D g) {
        if (alive) {
            g.setColor(color);
            g.fillPolygon(polygon);
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setShootingColor(Color color) {
        this.shootingColor = color;
    }


    public Shape getShape() {
        return polygon;
    }


    public void move(int delta) {
        positionX += (int) (delta * speed);
        positionBounded();
    }

    private void positionBounded() {
        if (positionX - size / 2 <= min) {
            positionX = min + size / 2;
        } else if (positionX + size / 2 >= max) {
            positionX = max - size / 2;
        }
    }

    public void reSize(int delta) {
        if (size + delta <= minSize) {
            size = minSize;
        } else if (size + delta >= maxSize) {
            size = maxSize;
        } else {
            size += delta;
        }
        positionBounded();
    }

    public void update() {
        if (isShooting) {
            shootHeight += shootSpeed;
            if (shootHeight >= positionY) {
                isShooting = false;
            }
        }

        Point a = new Point(positionX - size / 2, positionY);
        Point b = new Point(positionX, (int) (positionY - size * Math.sqrt(3) / 2));
        Point c = new Point(positionX + size / 2, positionY);
        int[] xPoints = {a.x, b.x, c.x};
        int[] yPoints = {a.y, b.y, c.y};
        int nPoints = 3;
        polygon = new Polygon(xPoints, yPoints, nPoints);
    }

    public void shoot() {

        if (!isShooting && alive) {
            playSound("laser");
            isShooting = true;
            shootHeight = (int) (size * Math.sqrt(3) / 2);
            shootPosition = positionX;
            shootSpeed = size / 10;
        }

    }

    public int getShootSpeed() {
        return shootSpeed;
    }

    public boolean isShooting() {
        return isShooting;
    }

    public void setShooting(boolean s) {
        isShooting = s;
    }

    public void displayShoot(Graphics2D g) {
        g.setColor(shootingColor);
        g.drawLine(shootPosition, positionY, shootPosition, positionY - shootHeight);
    }

    public Shape getShoot() {
        return new Rectangle2D.Double(shootPosition - 1, positionY - shootHeight, 3, shootHeight);

    }

    public void setScore(int i) {
        score = i;
    }

    public int getScore() {
        return score;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
        isShooting = false;
    }

    public String getName() {
        return name;
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
