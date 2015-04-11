import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Bubble {
    private int size;
    private double speedX, speedY;
    private double gravity;
    private int positionX, positionY, minHeight, maxHeight, minWidth, maxWidth;
    private Color[] colors;


    public Bubble(int positionX, int positionY, int minHeight, int maxHeight, int minWidth, int maxWidth) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;

        speedX = 0;
        speedY = 0;

    }

    public void setSpeedX(double i) {
        speedX = i;
    }

    public void setSpeedY(double i) {
        speedY = i;
    }

    public void setSize(int i) {
        size = i;
    }

    public void setGravity(double i) {
        gravity = i;
    }

    public void setColor(Color[] colors) {
        this.colors = colors;
    }

    public void display(Graphics2D g) {

        // Assume x, y, and diameter are instance variables.
        Ellipse2D.Double circle = new Ellipse2D.Double(positionX, positionY, size, size);
        g.setColor(colors[0]);
        g.fill(circle);
    }

    public Shape getShape() {
        return new Ellipse2D.Double(positionX, positionY, size, size);
    }

    public void update() {
        positionX += (int) speedX;
        positionY += (int) speedY;

        positionBounded();
        gravity();
    }

    private void positionBounded() {
        if (positionX <= minWidth) {
            speedX = - speedX;
        } else if (positionX + size >= maxWidth) {
            speedX = - speedX;
        }
        if (positionY + size >= maxHeight && speedY > 0) {
            speedY = - speedY - gravity;
        }
    }

    private void gravity() {
        speedY += gravity;
    }

    public void destroy(Game game, int hit) {
        if (colors.length > 1) {
            Color[] childColors = new Color[colors.length-1];
            System.arraycopy(colors, 1, childColors, 0, colors.length - 1);

            Bubble childLeft = new Bubble(positionX, positionY, minHeight, maxHeight, minWidth, maxWidth);
            Bubble childRight = new Bubble(positionX, positionY, minHeight, maxHeight, minWidth, maxWidth);
            childLeft.setSpeedX(- speedX);
            childRight.setSpeedX(speedX);
            childLeft.setSpeedY(- 2 * hit / 3);
            childRight.setSpeedY(- 2 * hit / 3);
            childLeft.setSize((int) (size/Math.sqrt(2)));
            childRight.setSize((int) (size/Math.sqrt(2)));
            childLeft.setGravity(gravity);
            childRight.setGravity(gravity);
            childLeft.setColor(childColors);
            childRight.setColor(childColors);

            game.bubbles.add(childLeft);
            game.bubbles.add(childRight);
        }

        game.bubbles.remove(this);
    }
}
