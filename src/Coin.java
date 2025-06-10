import java.awt.*;

public class Coin {
    private double x, y;
    private final int width = 10;
    private final int height = 10;
    private final int exp;
    private final int speed = 10;
    private final Color color = Color.YELLOW;
    private int[] xPoints = new int[4];
    private int[] yPoints = new int[4];
    private double dx, dy;

    private int moveTimer = 0;
    
    public Coin(double x, double y, int exp) {
        this.x = x;
        this.y = y;
        this.exp = exp;
        this.dx = (Math.random() - 0.5) / 1.5;
        this.dy = (Math.random() - 0.5) / 1.5;
        this.moveTimer = 100;
    }

    public void move(double playerX, double playerY) {
        x += dx;
        y += dy;
        xPoints[0] = (int) (x - width / 2);
        xPoints[1] = (int) (x);
        xPoints[2] = (int) (x + width / 2);
        xPoints[3] = (int) (x);
        yPoints[0] = (int) (y);
        yPoints[1] = (int) (y - width / 2);
        yPoints[2] = (int) (y);
        yPoints[3] = (int) (y + width / 2);
        if (moveTimer > 0) {
            moveTimer -= 1;
        }
        else {
            dx = playerX - x;
            dy = playerY - y;

            double dxdy = Math.sqrt(dx * dx + dy * dy);
            if (dxdy != 0) {
                dx /= dxdy;
                dy /= dxdy;
                x += dx * speed;
                y += dy * speed;
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillPolygon(xPoints, yPoints, 4);
    }

    public int getExp() {
        return exp;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}
