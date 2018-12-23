import javax.swing.*;
import java.awt.*;

public class PreviewLevel extends JComponent {

    static int SIZE = 18;
    private Level level;
    private ImageLoader imageLoader;
    private boolean isSelected;

    public PreviewLevel(Level level, boolean selected){
        this.level = level;
        this.isSelected = selected;
        this.imageLoader = ImageLoader.getInstance();
        setLayout(null);
        setSize(new Dimension(getWidth(), SIZE * level.getHeight()));
        setPreferredSize(new Dimension(getWidth(), SIZE * level.getHeight() + SIZE));
        setBackground(new Color(64, 64,64));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(isSelected)
            g.setColor(new Color(85, 85, 85));
        else
            g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());


        int xOffset = SIZE * 2;
        int yOffset = SIZE / 2;
        g.setColor(new Color(117, 140, 142));
        for (int i = 0; i < level.getHeight(); i++) {
            for (int j = 0; j < level.getWidth(); j++) {
                Field field = level.getFields()[j][i];

                int posX = j * SIZE + xOffset;
                int posY = i * SIZE + yOffset;

                switch (field.getFieldType()){
                    case BOX:
                        g.drawImage(imageLoader.getImgBox(), posX, posY, SIZE, SIZE, null);
                        break;
                    case EMPTY:
                        if(field.isTarget())
                            g.drawImage(imageLoader.getImgTarget(), posX, posY, SIZE, SIZE, null);
                        else
                            g.drawImage(imageLoader.getImgGround(), posX, posY, SIZE, SIZE, null);
                        if(field.hasPlayer())
                            g.drawImage(imageLoader.getPlayerImage(PlayerDirection.DOWN, 0), posX, posY, SIZE, SIZE, null);
                        break;
                    case WALL:
                        g.drawImage(imageLoader.getImgWall(), posX, posY, SIZE, SIZE, null);
                        break;
                }
            }
        }
        g.setColor(new Color(143, 143, 143));
        g.drawRect(xOffset, yOffset, level.getWidth() * SIZE, level.getHeight() * SIZE);
        g.setColor(Color.WHITE);
        g.drawString(level.toString(), level.getWidth() * SIZE + xOffset + SIZE, level.getHeight() * SIZE / 2 + yOffset);
    }
}
