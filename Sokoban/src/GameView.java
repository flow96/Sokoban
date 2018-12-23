import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameView extends JPanel implements LevelCallbacks {

    private Level level;
    private ImageLoader imageLoader;
    public static int SIZE;
    private Player player;

    public GameView(Level level){
        this.level = level;
        this.imageLoader = ImageLoader.getInstance();
        setBackground(new Color(64, 64, 64));

        this.player = this.level.getPlayer();

        this.level.addLevelFinishedCallback(this);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(!level.isInputBlocked()) {
                    int x = player.getPosX() - (e.getX() / SIZE);
                    int y = player.getPosY() - (e.getY() / SIZE);
                    // Wenn eine von beiden richtungen nicht 0 ist
                    if(x != 0 || y != 0) {
                        // X Richtung
                        if (Math.max(Math.abs(x), Math.abs(y)) == Math.abs(x)) {
                            if(x > 0)
                                level.moveLeft();
                            else
                                level.moveRight();
                        } else {  // Y Richtung
                            if(y < 0)
                                level.moveDown();
                            else
                                level.moveUp();
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        SIZE = Math.min(getWidth() / level.getWidth(), getHeight() / level.getHeight());
        int centerOffset = (getWidth() - (level.getWidth() * SIZE)) / 2;

        g.setColor(new Color(117, 140, 142));
        g.fillRect(centerOffset, 0, level.getWidth() * SIZE, level.getHeight() * SIZE);

        for (int i = 0; i < level.getHeight(); i++) {
            for (int j = 0; j < level.getWidth(); j++) {
                Field field = level.getFields()[j][i];

                int posX = j * SIZE + centerOffset;
                int posY = i * SIZE;

                switch (field.getFieldType()){
                    case BOX:
                    case EMPTY:
                        if(field.isTarget())
                            g.drawImage(imageLoader.getImgTarget(), posX, posY, SIZE, SIZE, null);
                        else
                            g.drawImage(imageLoader.getImgGround(), posX, posY, SIZE, SIZE, null);
                        break;
                    case WALL:
                        g.drawImage(imageLoader.getImgWall(), posX, posY, SIZE, SIZE, null);
                        break;
                }
            }
        }

        // Draw player
        g.drawImage(imageLoader.getPlayerImage(player), player.getPosX() * SIZE + player.getxOffset() + 3 + centerOffset, player.getPosY() * SIZE + player.getyOffset() + 3, SIZE - 6, SIZE - 6, null);
        level.getBoxes().stream().forEach(b -> g.drawImage(imageLoader.getImgBox(), b.getPosX() * SIZE + b.getxOffset() + 7 + centerOffset, b.getPosY() * SIZE + b.getyOffset() + 7, SIZE - 14, SIZE - 14, null));
    }

    @Override
    public void levelFinished() {}

    @Override
    public void moveCounterChanged(int counter) {}

    @Override
    public void repaintView() {
        repaint();
    }
}
