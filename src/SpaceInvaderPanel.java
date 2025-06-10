import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

public class SpaceInvaderPanel extends JPanel implements ActionListener, KeyListener, MouseListener{
    public Timer timer1, timer2, timer3, timer4;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private List<Enemy> enemiesToAdd = new ArrayList<>();
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private List<Bullet> bulletsToAdd = new ArrayList<>();
    private ArrayList<Explode> explodes = new ArrayList<>();
    private ArrayList<Coin> coins = new ArrayList<>();
    public Player player;
    private CampFire campFire;
    
    private boolean paused = false;
    private boolean playerInput = false;
    private boolean enemyMove = false;
    private boolean isGameOver = false;
    private boolean showBonfireText = false;
    private boolean inBonfire = false;

    private final int spawnEnemiesMaxDelay = 10000;
    private final int spawnEnemiesMinDelay = 9000;
    private int spawnEnemiesDelay = 10000;
    private int spawnEnemiesStep = 500;

    private final int bossSpawnDelay = 5000;

    private boolean stopSpawmEnemies = false;
    private boolean enemyFelled = false;

    private boolean wPressed = false;
    private boolean aPressed = false;
    private boolean sPressed = false;
    private boolean dPressed = false;
    private boolean ePressed = false;
    private boolean escPressed = false;
    private int rPressed = 0;
    private int spacePressed = 0;

    private int mouseLeftClicked = 0;
    private int mouseRightClicked = 0;

    private int leftClickX = 0;
    private int leftClickY = 0;

    private JLayeredPane layeredPane;
    private JPanel settingsPanel;
    private GameOverPanel gameOverPanel;
    private LevelUpPanel levelUpPanel;
    private MusicPlayer musicPlayer;

    public SpaceInvaderPanel(JLayeredPane lp) {
        setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        this.layeredPane = lp;
        setPreferredSize(new Dimension(Constants.FRAMEWIDTH, Constants.FRAMEHEIGHT));

        player = new Player(Constants.FRAMEWIDTH / 2, 
                            Constants.FRAMEHEIGHT - 2 * Constants.PLAYERHEIGHT, 
                            Constants.PLAYERWIDTH, 
                            Constants.PLAYERHEIGHT, 
                            Color.green, 
                            (int) Constants.playerActualHP, 
                            (int) Constants.playerActualEnergy);

        timer1 = new Timer(15, this);
        timer2 = new Timer(bossSpawnDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Constants.nRuns % 2 == 0) {
                    enemies.add(new BigTriangle(610, -100, 
                                                60, (int) (60 * 1.732 / 2), 
                                                Color.red, 
                                                (int) (Constants.BIGTRIANGLEBASEATTACKDAMAGE * Math.pow(1.25, Constants.nRuns + 1)), 
                                                (int) (Constants.BIGTRIANGLEBASEHEALTH * Math.pow((Constants.nRuns + 1), 1.2)), 
                                                Constants.BIGTRIANGLEDETECTZONE, 
                                                musicPlayer));
                }
                else if (Constants.nRuns % 2 == 1) {
                    enemies.add(new BigSquare(610, -100, 
                                                60, (int) (60 * 1.732 / 2), 
                                                Color.red, 
                                                (int) (Constants.BIGSQUAREBASEATTACKDAMAGE * Math.pow(1.25, Constants.nRuns + 1)), 
                                                (int) (Constants.BIGSQUAREBASEHEALTH * Math.pow((Constants.nRuns + 1), 1.15)), 
                                                Constants.BIGTRIANGLEDETECTZONE, 
                                                musicPlayer));
                }
                timer2.stop();
            }
        });
        timer3 = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Iterator<Enemy> enemy = enemies.iterator();
                while (enemy.hasNext()) {
                    Enemy ee = enemy.next();
                    ee.getHurt((int) (ee.getHealth() * 10));
                    if (ee.getHealth() < 0) {
                        enemy.remove();
                        coins.add(new Coin(ee.getCenterX(), 
                                           ee.getCenterY(), 
                                           (int) ((8 + Math.random() * 4) * Math.pow(1.25, Constants.nRuns + 1))));
                    }
                }
                musicPlayer.adjustVolume(-1);
                if (musicPlayer.getCurrentVolume() < 1){
                    musicPlayer.stopById("BigTriangle");
                    musicPlayer.stopById("BigSquare");
                    musicPlayer.adjustVolume(10);
                    timer3.stop();
                }
            }
        });
        timer4 = new Timer(spawnEnemiesDelay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (spawnEnemiesDelay == spawnEnemiesMinDelay && enemies.isEmpty()) {
                    stopSpawmEnemies = true;
                    timer4.stop();
                    timer2.start();
                }
                else if (spawnEnemiesDelay > spawnEnemiesMinDelay) {
                    spawnEnemies();
                    spawnEnemiesDelay -= spawnEnemiesStep;
                }
            }
        });
    }

    public void spawnEnemies() {
        // if (enemies.isEmpty()) {
        //     enemies.add(new BigSquare(610, -100, 
        //                                         60, (int) (60 * 1.732 / 2), 
        //                                         Color.red, 
        //                                         (int) (Constants.BIGSQUAREBASEATTACKDAMAGE * Math.pow(1.25, Constants.nRuns + 1)), 
        //                                         (int) (Constants.BIGSQUAREBASEHEALTH / 5 * Math.pow((Constants.nRuns + 1), 1.15)), 
        //                                         Constants.BIGTRIANGLEDETECTZONE, 
        //                                         musicPlayer));
        // }
        int x = 0, y = 0;
        int times = 11 - spawnEnemiesDelay / 1000 + (int) (Math.random() * 3) + Constants.nRuns;

        for (int a = 0; a < times; a++) {
            int side = (int) (Math.random() * 3); // 0 = top, 1 = bottom, 2 = left, 3 = right

            switch (side) {
                case 0: // Top
                    x = (int) (Math.random() * Constants.FRAMEWIDTH);
                    y = -Constants.TRIANGLEHEIGHT - 100;
                    break;
                case 1: // Bottom
                    x = (int) (Math.random() * Constants.FRAMEWIDTH);
                    y = Constants.FRAMEHEIGHT + 100;
                    break;
                case 2: // Left
                    x = -Constants.TRIANGLEWIDTH - 100;
                    y = (int) (Math.random() * Constants.FRAMEHEIGHT);
                    break;
                case 3: // Right
                    x = Constants.FRAMEWIDTH + 100;
                    y = (int) (Math.random() * Constants.FRAMEHEIGHT);
                    break;
            }

            if (Math.random() < 0.7) {
                enemies.add(new Triangle(x, y, 
                                         Constants.TRIANGLEWIDTH, 
                                         Constants.TRIANGLEHEIGHT, 
                                         Color.red, 
                                         (int) (Constants.TRIANGLEBASEATTACKDAMAGE * Math.pow(1.25, Constants.nRuns + 1)), 
                                         (int) (Constants.TRIANGLEBASEHEALTH * Math.pow(1.1, Constants.nRuns)), 
                                         Constants.TRIANGLEDETECTZONE));
            }
            else {
                enemies.add(new Square(x, y,  
                                       Constants.SQUAREWIDTH, 
                                       Constants.SQUAREHEIGHT, 
                                       Color.red, 
                                       (int) (Constants.SQUAREBASEATTACKDAMAGE * Math.pow(1.25, Constants.nRuns + 1)), 
                                       (int) (Constants.SQUAREBASEHEALTH * Math.pow(1.1, Constants.nRuns)), 
                                       Constants.SQUAREDETECTZONE, 
                                       musicPlayer));
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        campFire.draw(g);
        if (showBonfireText) campFire.drawText(g);
        player.draw(g);
        PlayerUI.draw(g, 
                      (int) Constants.playerActualHP, 
                      player.getHealth(), 
                      (int) Constants.playerActualEnergy, 
                      player.getEnergy(), 
                      player.getExp(), 
                      player.getEstus());
        for(Enemy enemy : enemies) {
            enemy.draw(g);
            drawEnemyIndicator(g, (int) player.getCenterX(), (int) player.getCenterY(), (int) enemy.getCenterX(), (int) enemy.getCenterY());
        }
        for(Bullet bullet : bullets) {
            bullet.draw(g);
        }
        for(Explode explode : explodes) {
            explode.draw(g);
        }
        for(Coin coin : coins) {
            coin.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        requestFocusInWindow();
        if(!paused) {
            player.move(wPressed, aPressed, sPressed, dPressed);

            if (enemyMove) {
                for(Enemy enemy : enemies) {
                    enemy.move(player.getCenterX(), player.getCenterY());
                }
            }

            for (Enemy enemy : enemies) {
                if (enemy instanceof BigTriangle bt) {
                    enemiesToAdd.addAll(bt.enemiesToAdd);
                    bt.enemiesToAdd.clear();
                }
                else if (enemy instanceof Square s) {
                    bulletsToAdd.addAll(s.bulletsToAdd);
                    s.bulletsToAdd.clear();
                }
                else if (enemy instanceof BigSquare s) {
                    bulletsToAdd.addAll(s.bulletsToAdd);
                    s.bulletsToAdd.clear();
                }
            }
            enemies.addAll(enemiesToAdd);
            enemiesToAdd.clear();
            bullets.addAll(bulletsToAdd);
            bulletsToAdd.clear();

            for(Bullet bullet : bullets) {
                bullet.move();
            }
            
            for(Coin coin : coins) {
                coin.move(player.getCenterX(), player.getCenterY());
            }
            
            deleteOutOfScreenBullets();

            if (mouseLeftClicked == 2 && player.getEnergy() > 0) {
                if (!player.isAttacking() && !player.isDodging() && !player.isKnockBacking() && !player.isHealing()){
                    double bulletDx = (double) (leftClickX - player.getCenterX());
                    double bulletDy = (double) (leftClickY - player.getCenterY());
                    double dxdy = Math.sqrt(bulletDx * bulletDx + bulletDy * bulletDy);
                    if (dxdy != 0) {
                        bulletDx /= dxdy;
                        bulletDy /= dxdy;
                    }
                    bullets.add(new Bullet((int) player.getCenterX() - 10, 
                                           (int) player.getCenterY() - 10, 
                                           20, 20, 
                                           Color.YELLOW, 
                                           bulletDx, 
                                           bulletDy, 
                                           7, 
                                           (int) (Constants.PLAYERBASEATTACK + Constants.playerActualSTR * 10), 
                                           100, 
                                           "Player", 
                                           musicPlayer));
                    player.attack();
                }
                mouseLeftClicked = 0;
            }

            if (spacePressed == 2 && (wPressed || aPressed || sPressed || dPressed)) {
                player.dodge(wPressed, aPressed, sPressed, dPressed);
                spacePressed = 0;
            }

            if(rPressed == 2) {
                player.increaseHealth();
                if (player.getEstus() > 0) musicPlayer.playSegment("Estus", 0.0f, 2f, false);
                rPressed = 0;
            }

            if(escPressed) {
                pauseGame();
                escPressed = false;
                settingsPanel.setOpaque(false);
                for (Component comp : layeredPane.getComponents()) {
                    if (comp instanceof SettingsPanel) {
                        comp.setVisible(true);
                    }
                }
            }
                
            checkCollisions();
        }
        repaint();
    }

    public void checkCollisions() {
        bonfireCheck();

        bulletCollisions();

        explodeCollisions();
        
        enemyPlayerCollisions();
        
        coinPlayerCollisions();
    }

    public void bonfireCheck() {
        if (!inBonfire) {
            double dx = (double) (player.getCenterX() - campFire.getCenterX());
            double dy = (double) (player.getCenterY() - campFire.getCenterY());
            if (Math.sqrt(dx * dx + dy * dy) <= campFire.getWidth()) {
                showBonfireText = true;
                if (ePressed) {
                    pauseGame();
                    showBonfireText = false;
                    inBonfire = true;
                    levelUpPanel.setOpaque(false);
                    levelUpPanel.setVisible(true);
                    musicPlayer.stopAll();
                    musicPlayer.playSegment("Bonfire", 0f, 4f, false);

                    player.restoreHealth();
                    player.restoreEnergy();
                    player.restoreEstus();

                    enemies.clear();
                    bullets.clear();
                    coins.clear();

                    if (!stopSpawmEnemies && spawnEnemiesDelay < spawnEnemiesMaxDelay) {
                        spawnEnemiesDelay += spawnEnemiesStep;
                    }

                    timer2.stop();
                    timer4.stop();
                }
            }
            else {
                showBonfireText = false;
            }
        }
        else {
            double dx = (double) (player.getCenterX() - campFire.getCenterX());
            double dy = (double) (player.getCenterY() - campFire.getCenterY());
            if (Math.sqrt(dx * dx + dy * dy) > (player.getWidth() + campFire.getWidth()) / 2) {
                inBonfire = false;
                levelUpPanel.setOpaque(true);
                levelUpPanel.setVisible(false);
                player.restoreHealth();
            }
        }
    }

    public void bulletCollisions() {
        Iterator<Bullet> bullet = bullets.iterator(); // bullet & enemy
        while (bullet.hasNext()) {
            Bullet b = bullet.next();
            Rectangle bRect = b.getBounds();
            
            b.getHurt(1);
            if (b.getHealth() <= 0) {
                bullet.remove();
                break;
            }

            if ("Player".equals(b.getTag())) {
                Iterator<Enemy> enemy = enemies.iterator();
                while (enemy.hasNext()) {
                    Enemy e = enemy.next();
    
                    Rectangle eRect = e.getBounds();
                    if (bRect.intersects(eRect)) {
                        Rectangle intersection = bRect.intersection(eRect);
                        int collisionX = intersection.x;
                        int collisionY = intersection.y;
    
                        explodes.add(new Explode(collisionX, 
                                                 collisionY, 
                                                 b.getWidth(), 
                                                 b.getHeight(), 
                                                 Color.RED, 
                                                 5, 
                                                 b.getDamage(), 
                                                 "Player"));
                        bullet.remove();
                        break;
                    }
                }
            }
            else if ("Enemy".equals(b.getTag())) {
                Rectangle playerRect = player.getBounds();
                if (bRect.intersects(playerRect)) {
                    if (!player.isInvincible()) {
                        Rectangle intersection = bRect.intersection(playerRect);
                        int collisionX = intersection.x;
                        int collisionY = intersection.y;

                        explodes.add(new Explode(collisionX, 
                                                collisionY, 
                                                b.getWidth(), 
                                                b.getHeight(), 
                                                Color.RED, 
                                                5, 
                                                b.getDamage(), 
                                                "Enemy"));
                        bullet.remove();
                        break;
                    }
                }
            }
            
        }
    }

    public void explodeCollisions() {
        Iterator<Explode> explode = explodes.iterator(); // explode & enemy
        while (explode.hasNext()) {
            Explode b = explode.next();
            b.getHurt(1);
            if (b.getHealth() <= 0) {
                explode.remove();
            }

            Rectangle bRect = b.getBounds();
            if ("Player".equals(b.getTag())) {
                Iterator<Enemy> enemy = enemies.iterator();
                while (enemy.hasNext()) {
                    Enemy e = enemy.next();
                    
                    Rectangle eRect = e.getBounds();
                    if (bRect.intersects(eRect)) {
                        int damage = b.getDamage();
                        e.getHurt(damage);
                        e.knockBack(b.getCenterX(), b.getCenterY(), Math.min(20, damage / 3), 10);
                        if (e.getHealth() <= 0) {
                            for (int a = (int) (e.getMaxHealth()); a > 0; a-= 20) {
                                int offsetX = (int)(Math.random() * 100 - 50);
                                int offsetY = (int)(Math.random() * 100 - 50);
                                coins.add(new Coin(e.getCenterX() + offsetX, 
                                                   e.getCenterY() + offsetY, 
                                                   (int) ((8 + Math.random() * 4) * Math.pow(1.25, Constants.nRuns + 1))));
                            }
                            if (e instanceof BigTriangle || e instanceof BigSquare) {
                                timer3.start();
                                enemyFelled = true;
                                Constants.nRuns = Math.min(Constants.nRuns + 1, 9);
                            }
                            enemy.remove();
                            musicPlayer.playSegment("Kill", 0.0f, 1f, false);
                        }
                    }
                }
            }
            else if ("Enemy".equals(b.getTag())) {
                Rectangle playerRect = player.getBounds();
                if (bRect.intersects(playerRect)) {
                    if (!player.isKnockBacking() && !player.isInvincible()){
                        int damage = b.getDamage();
                        player.getHurt(damage);
                        player.knockBack(b.getCenterX(), b.getCenterY(), Math.min(20, damage / 3), 30);
                        if (player.getHealth() <= 0 && !isGameOver) {
                            disablePlayerInput();
                            isGameOver = true;
                            gameOverPanel.triggerFadeIn();
                        }
                    }
                }
            }
        }
    }

    public void enemyPlayerCollisions() {
        Iterator<Enemy> enemy = enemies.iterator();
        while (enemy.hasNext()) {
            Enemy e = enemy.next();

            if (e.isAttacking() && e.isMeleeAttack()) {
                Rectangle pRect = player.getBounds();
                Rectangle eRect = e.getBounds();
                if (pRect.intersects(eRect)) {
                    if (!player.isKnockBacking() && !player.isInvincible()){
                        int damage = e.getDamage();
                        player.getHurt(damage);
                        player.knockBack(e.getCenterX(), e.getCenterY(), Math.min(20, damage / 3), 30);
                        if (player.getHealth() <= 0 && !isGameOver) {
                            disablePlayerInput();
                            isGameOver = true;
                            gameOverPanel.triggerFadeIn();
                        }
                    }
                }
            }
        }
    }

    public void coinPlayerCollisions() {
        Iterator<Coin> coin = coins.iterator();
        while (coin.hasNext()) {
            Coin c = coin.next();

            Rectangle pRect = player.getBounds();
            Rectangle cRect = c.getBounds();
            if (pRect.intersects(cRect)) {
                player.increaseExp(c.getExp());
                coin.remove();
            }
        }
    }

    public void drawEnemyIndicator(Graphics g, int playerX, int playerY, int enemyX, int enemyY) {
        if (enemyX < 0 || enemyX > Constants.FRAMEWIDTH || enemyY < 0 || enemyY > Constants.FRAMEHEIGHT) {
            double angle = Math.atan2(enemyY - playerY, enemyX - playerX);

            int margin = -10;

            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            double tX = (cos > 0) ?
                (Constants.FRAMEWIDTH - playerX) / cos :
                (0 - playerX) / cos;
            double tY = (sin > 0) ?
                (Constants.FRAMEHEIGHT - playerY) / sin :
                (0 - playerY) / sin;

            double t = Math.min(tX, tY);

            int arrowX = (int)(playerX + cos * (t + margin));
            int arrowY = (int)(playerY + sin * (t + margin));

            int size = 15;
            int[] xPoints = new int[3];
            int[] yPoints = new int[3];

            xPoints[0] = arrowX;
            yPoints[0] = arrowY;
            xPoints[1] = (int)(arrowX - Math.cos(angle - Math.PI / 6) * size);
            yPoints[1] = (int)(arrowY - Math.sin(angle - Math.PI / 6) * size);
            xPoints[2] = (int)(arrowX - Math.cos(angle + Math.PI / 6) * size);
            yPoints[2] = (int)(arrowY - Math.sin(angle + Math.PI / 6) * size);

            g.setColor(Color.YELLOW);
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    public void deleteOutOfScreenBullets() {
        Iterator<Bullet> bullet = bullets.iterator();
        while (bullet.hasNext()) {
            Bullet b = bullet.next();
            if (b.getCenterX() < -500 || b.getCenterX() > Constants.FRAMEWIDTH + 500 || 
                b.getCenterY() < -500 || b.getCenterY() > Constants.FRAMEHEIGHT + 500) {
                bullet.remove();
            }
        }
    }

    public void pauseGame() {
        paused = true;
        timer2.stop();
        timer4.stop();
    }

    public void resumeGame() {
        paused = false;
        // System.out.println(stopSpawmEnemies);
        // System.out.println(enemyFelled);
        // System.out.println();
        if (enemies.isEmpty()) {
            if (stopSpawmEnemies && !enemyFelled) {
                timer2.start();
            }
            else if (!stopSpawmEnemies && !enemyFelled) {
                timer4.start();
            }
            else if (stopSpawmEnemies && enemyFelled) {
                timer4.start();
                spawnEnemiesDelay = spawnEnemiesMaxDelay;
                stopSpawmEnemies = false;
                enemyFelled = false;
            }
        }
    }

    public void restartGame() {
        Constants.getActualHP();
        Constants.getActualSTR();
        Constants.getActualDEX();
        Constants.getActualEnergy();

        player = new Player(Constants.FRAMEWIDTH / 2, 
                            Constants.FRAMEHEIGHT - 2 * Constants.PLAYERHEIGHT, 
                            Constants.PLAYERWIDTH, 
                            Constants.PLAYERHEIGHT, 
                            Color.green, 
                            (int) Constants.playerActualHP, 
                            (int) Constants.playerActualEnergy);
        
        campFire = new CampFire(640, 600);
    
        enemies.clear();
        bullets.clear();
        coins.clear();
        
        paused = false;
        playerInput = true;
        enemyMove = true;
        isGameOver = false;
        showBonfireText = false;
        inBonfire = false;

        spawnEnemiesDelay = spawnEnemiesMaxDelay;
        stopSpawmEnemies = false;
        enemyFelled = false;

        wPressed = false;
        aPressed = false;
        sPressed = false;
        dPressed = false;
        ePressed = false;
        escPressed = false;
        spacePressed = 0;
    
        mouseLeftClicked = 0;
        mouseRightClicked = 0;
    
        leftClickX = 0;
        leftClickY = 0;

        timer1.start();
        timer4.start();
    }

    public void disablePlayerInput() {
        playerInput = false;

        wPressed = false;
        aPressed = false;
        sPressed = false;
        dPressed = false;
        ePressed = false;
        escPressed = false;
        rPressed = 0;
        spacePressed = 0;
    
        mouseLeftClicked = 0;
        mouseRightClicked = 0;
    
        leftClickX = 0;
        leftClickY = 0;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (playerInput) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> wPressed = true;
                case KeyEvent.VK_A -> aPressed = true;
                case KeyEvent.VK_S -> sPressed = true;
                case KeyEvent.VK_D -> dPressed = true;
                case KeyEvent.VK_E -> ePressed = true;
                case KeyEvent.VK_R -> rPressed = 1;
                case KeyEvent.VK_SPACE -> spacePressed = 1;
                case KeyEvent.VK_ESCAPE -> escPressed = true;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (playerInput) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W -> wPressed = false;
                case KeyEvent.VK_A -> aPressed = false;
                case KeyEvent.VK_S -> sPressed = false;
                case KeyEvent.VK_D -> dPressed = false;
                case KeyEvent.VK_E -> ePressed = false;
                case KeyEvent.VK_R -> {
                    if(rPressed == 1) rPressed = 2;
                }
                case KeyEvent.VK_SPACE -> {
                    if(spacePressed == 1) spacePressed = 2;
                }
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (playerInput) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                mouseLeftClicked = 1;
            }
            if (SwingUtilities.isRightMouseButton(e)) {
                mouseRightClicked = 1;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (playerInput) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                leftClickX = e.getX();
                leftClickY = e.getY();
                mouseLeftClicked = 2;
            }
            if (SwingUtilities.isRightMouseButton(e)) {
                mouseRightClicked = 2;
            }
        }
    }

    public void setSettingsPanel(SettingsPanel settingPanel) {
        this.settingsPanel = settingPanel;
    }

    public void setGameOverPanel(GameOverPanel gameOverPanel) {
        this.gameOverPanel = gameOverPanel;
    }

    public void setLevelUpPanel(LevelUpPanel levelUpPanel) {
        this.levelUpPanel = levelUpPanel;
    }

    public void setMusicPlayer(MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }
}
