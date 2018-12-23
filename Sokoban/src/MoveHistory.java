
public class MoveHistory {

    private int deltaX, deltaY;
    private MoveableObject box;
    private PlayerDirection dir;

    public MoveHistory(int x, int y, MoveableObject box, PlayerDirection direction){
        this.deltaX = x;
        this.deltaY = y;
        this.box = box;
        this.dir = direction;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public MoveableObject getBox() {
        return box;
    }

    public PlayerDirection getDir() {
        return dir;
    }
}
