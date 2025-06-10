import java.awt.*;

public class CampFire {
    private int x, y;
    private int width = 10;
    private int interactWidth = 50;
    private int height = 10;

    public CampFire(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g) {
        g.setColor(Color.PINK);
        g.fillOval(x, y, width, height);
    }

    public void drawText(Graphics g) {
        g.setColor(Color.PINK);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.drawString("按E休息", x - 10, y - 10);
    }
    
    public int getCenterX() {
        return (int) (x + (double) width / 2);
    }

    public int getCenterY() {
        return (int) (y + (double) height / 2);
    }

    public int getWidth() {
        return interactWidth;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}
