import javax.imageio.ImageIO;
import java.awt.*;

public class ImageLoader {

    private Image[][] playerImages;
    private Image imgBox, imgBoxTarget, imgGround, imgTarget, imgWall;
    private static ImageLoader instance;


    public static ImageLoader getInstance(){
        if(instance == null)
            instance = new ImageLoader();
        return instance;
    }

    private ImageLoader(){
        imgBox = loadImage("box");
        imgBoxTarget = loadImage("box_target");
        imgGround = loadImage("ground");
        imgTarget = loadImage("target");
        imgWall = loadImage("wall");

                            // dir, img
        playerImages = new Image[4][3];
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j <= 3; j++) {
                String prefix = i == 0 ? "up" : i == 1 ? "right" : i == 2 ? "down" : "left";
                playerImages[i][j - 1] = loadImage(prefix + j);
            }
        }
    }


    private Image loadImage(String filename){
        String path = "/img/" + filename + ".png";

        try{
            return ImageIO.read(ImageLoader.class.getResource(path));
        }catch (Exception e){
            return null;
        }

    }

    public Image getImgBox() {
        return imgBox;
    }

    public Image getImgBoxTarget() {
        return imgBoxTarget;
    }

    public Image getImgGround() {
        return imgGround;
    }

    public Image getPlayerImage(Player player){
        return getPlayerImage(player.getPlayerDirection(), player.getImgIndex());
    }

    public Image getPlayerImage(PlayerDirection dir, int index){
        Image img = null;
        switch (dir){
            case UP:
                img = playerImages[0][index];
                break;
            case RIGHT:
                img = playerImages[1][index];
                break;
            case DOWN:
                img = playerImages[2][index];
                break;
            case LEFT:
                img = playerImages[3][index];
                break;
        }
        return img;
    }

    public Image getImgTarget() {
        return imgTarget;
    }

    public Image getImgWall() {
        return imgWall;
    }

}
