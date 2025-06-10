import java.awt.*;

public class Player{
    private double x, y;
    private final int width, height;
    private final Color color;

    private int speed = 5;
    private int currentHealth = 0;
    private double currentEnergy = 0;
    private int exp = 0;
    private int estus = 0;

    private boolean invincible = false;
    private boolean attacking = false;
    private boolean healing = false;
    private int attackCoolDown = 0;

    private int healingTimer = 0;
    private int healingCoolDown = 0;

    private boolean dodging = false;
    private int dodgeTimer = 0;
    private int dodgeCoolDown = 0;
    private double dodgedx = 0;
    private double dodgedy = 0;
    private double dodgeSpeed = 0;
    
    private boolean knockBacking = false;
    private double knockBackdx = 0;
    private double knockBackdy = 0;
    private int knockBackSpeed = 0;
    private int knockBackTimer = 0;
    private int knockBackCoolDown = 0;

    Player(int x, int y, int w, int h, Color c, int health, int energy) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.color = c;
        this.currentHealth = health;
        this.currentEnergy = energy;
        this.exp = Constants.PLAYERINITIALEXP;
        this.estus = Constants.PLAYERESTUSNUM;
    }

    public void move(boolean wPressed, boolean aPressed, boolean sPressed, boolean dPressed) {
        if (!knockBacking) {
            if (!dodging && !attacking && !healing) {
                double dx = 0;
                double dy = 0;

                if (wPressed) dy -= 1;
                if (aPressed) dx -= 1;
                if (sPressed) dy += 1;
                if (dPressed) dx += 1;

                double dxdy = Math.sqrt(dx * dx + dy * dy);
                if (dxdy != 0) {
                    dx /= dxdy;
                    dy /= dxdy;
                    x += dx * Constants.playerActualSpeed;
                    y += dy * Constants.playerActualSpeed;
                }
            }
            else{
                if (dodgeTimer > 0) {
                    x += dodgedx * dodgeSpeed;
                    y += dodgedy * dodgeSpeed;
                    dodgeTimer -= 1;
                }
                else {
                    dodging = false;
                    invincible = false;
                }
            }
        }
        else {
            if (knockBackCoolDown > 0) {
                if (knockBackTimer > 0){
                    x += knockBackdx * knockBackSpeed;
                    y += knockBackdy * knockBackSpeed;
                    knockBackTimer -= 1;
                }
                knockBackCoolDown -= 1;
            }
            else {
                knockBacking = false;
            }
        }

        if (x > Constants.FRAMEWIDTH - width) {
            x = Constants.FRAMEWIDTH - width;
        }
        else if (x < 0) {
            x = 0;
        }
        if (y > Constants.FRAMEHEIGHT - height) {
            y = Constants.FRAMEHEIGHT - height;
        }
        else if (y < 0) {
            y = 0;
        }

        if (attackCoolDown > 0) {
            attackCoolDown -= 1;
        }
        else {
            attacking = false;
        }

        if (dodgeCoolDown > 0) {
            dodgeCoolDown -= 1;
        }

        if (healingCoolDown > 0){
            if (healingTimer > 0) {
                currentHealth = Math.min(currentHealth + (int) (Constants.getActualHP() * 0.01), (int) Constants.playerActualHP);
                healingTimer -= 1;
            }
            healingCoolDown -= 1;
        }
        else {
            healing = false;
        }

        if (!attacking && !dodging && currentEnergy < Constants.playerActualEnergy) {
            currentEnergy += 0.5 + 0.25 * Constants.playerActualDEX;
        }
    }

    public void dodge(boolean wPressed, boolean aPressed, boolean sPressed, boolean dPressed) {
        if (!attacking && !dodging && !knockBacking && !healing && currentEnergy > 0){
            dodging = true;
            dodgeTimer = 10;
            dodgeCoolDown = 50;
            currentEnergy -= 30;
            dodgeSpeed = 10;
        
            dodgedx = 0;
            dodgedy = 0;
        
            if (wPressed) dodgedy -= 1;
            if (aPressed) dodgedx -= 1;
            if (sPressed) dodgedy += 1;
            if (dPressed) dodgedx += 1;
        
            double dxdy = Math.sqrt(dodgedx * dodgedx + dodgedy * dodgedy);
            if (dxdy != 0) {
                dodgedx /= dxdy;
                dodgedy /= dxdy;
            }
        
            invincible = true;
        }
    }

    public void knockBack(double x, double y, int s, int t) {
        knockBackdx = getCenterX() - x;
        knockBackdy = getCenterY() - y;
        double dxdy = Math.sqrt(knockBackdx * knockBackdx + knockBackdy * knockBackdy);
        knockBackdx /= dxdy;
        knockBackdy /= dxdy;
        knockBackSpeed = s;
        knockBackTimer = t / 2;
        knockBackCoolDown = t;
        knockBacking = true;
    }

    public void attack() {
        if (!attacking && !dodging && !knockBacking && !healing && currentEnergy > 0) {
            attacking = true;
            attackCoolDown = 30;
            currentEnergy -= 30;
        }
    }

    public void getHurt(int damage) {
        if (!invincible) {
            currentHealth -= damage;
        }
    }

    public boolean isAttacking() {
        return attacking;
    }

    public boolean isKnockBacking() {
        return knockBacking;
    }

    public boolean isDodging() {
        return dodging;
    }

    public boolean isHealing() {
        return healing;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        double centerX = getCenterX();
        double centerY = getCenterY();
        double healthPercant = (double) currentHealth / Constants.playerActualHP;
        double w;
        double h;
        if (dodging) {
            w = width / 1.2;
            h = height / 1.2;
            g.drawOval((int) (centerX - w / 2 - dodgedx * 10), (int) (centerY - w / 2 - dodgedy * 10), (int) w, (int) h);
            w = width / 1.4;
            h = height / 1.4;
            g.drawOval((int) (centerX - w / 2 - dodgedx * 20), (int) (centerY - w / 2 - dodgedy * 20), (int) w, (int) h);
            w = width / 1.6;
            h = height / 1.6;
            g.drawOval((int) (centerX - w / 2 - dodgedx * 30), (int) (centerY - w / 2 - dodgedy * 30), (int) w, (int) h);
        }
        g.drawOval((int) x, (int) y, width, height);
        w = healthPercant * width;
        h = healthPercant * height;
        g.fillOval((int) (centerX - w / 2), (int) (centerY - h / 2), (int) w, (int) h);
    }

    public int getHealth() {
        return currentHealth;
    }

    public void increaseHealth() {
        if (estus > 0) {
            if (!attacking && !dodging && !knockBacking && !healing) {
                estus -= 1;
                healing = true;
                healingTimer = 40;
                healingCoolDown = 50;
            }
        }
    }

    public void restoreHealth() {
        currentHealth = (int) Constants.playerActualHP;
    }

    public void restoreEnergy() {
        currentEnergy = (int) Constants.playerActualEnergy;
    }

    public int getEstus() {
        return estus;
    }

    public void restoreEstus() {
        estus = Constants.PLAYERESTUSNUM;
    }

    public void increaseExp(int exp) {
        this.exp += exp;
    }

    public void decreaseExp(int exp) {
        this.exp -= exp;
    }

    public int getExp() {
        return exp;
    }

    public int getEnergy() {
        return (int) currentEnergy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public double getCenterX() {
        return (int) (x + (double) width / 2);
    }

    public double getCenterY() {
        return y + (double) height / 2;
    }

    public int getWidth() {
        return width;
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }
}
