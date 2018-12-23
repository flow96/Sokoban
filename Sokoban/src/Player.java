

public class Player extends MoveableObject {

    private int imgIndex;   // Used for animation
    private PlayerDirection playerDirection;

    public Player(int x, int y){
        super(x, y);

        this.imgIndex = 0;
        this.playerDirection = PlayerDirection.DOWN;
    }


    public void setImgIndex(int imgIndex) {
        this.imgIndex = imgIndex;
    }

    public int getImgIndex() {
        return imgIndex;
    }

    public void setPlayerDirection(PlayerDirection playerDirection) {
        this.playerDirection = playerDirection;
    }

    public PlayerDirection getPlayerDirection() {
        return playerDirection;
    }

}
