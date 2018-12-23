import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class Level implements Cloneable {

    private Timer timer;
    private String levelName;
    private Field[][] fields;
    private int width, height;
    private Player player;
    private int targets = 0;
    private int moveCounter = 0;
    private boolean inputBlocked;
    private HashSet<Field> boxesOnTargets = new HashSet<>();
    private ArrayList<MoveableObject> boxes = new ArrayList<>();
    private ArrayList<LevelCallbacks> listeners = new ArrayList<>();

    private Stack<MoveHistory> moves = new Stack<>();



    public Level(Field[][] fields, String displayName, int width, int height){
        this.fields = fields;
        this.levelName = displayName;
        this.width = width;
        this.height = height;
        this.inputBlocked = false;


        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Field field = fields[j][i];
                if(field.hasPlayer())
                    player = new Player(j, i);
                if(field.isTarget()) {
                    targets++;
                    if(field.getFieldType() == FieldType.BOX)
                        boxesOnTargets.add(field);
                }
                if(field.getFieldType() == FieldType.BOX)
                    boxes.add(new MoveableObject(j, i));


            }
        }
    }


    @Override
    public String toString() {
        return this.levelName;
    }

    public Field[][] getFields() {
        return fields;
    }

    public void setFields(Field[][] fields) {
        this.fields = fields;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public HashSet<Field> getBoxesOnTargets() {
        return boxesOnTargets;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
    }

    public void setBoxesOnTargets(HashSet<Field> boxesOnTargets) {
        this.boxesOnTargets = boxesOnTargets;
    }

    public void setBoxes(ArrayList<MoveableObject> boxes) {
        this.boxes = boxes;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isInputBlocked() {
        return inputBlocked;
    }

    public ArrayList<MoveableObject> getBoxes() {
        return boxes;
    }

    public void moveUp(){
        player.setPlayerDirection(PlayerDirection.UP);
        move(0, -1);
    }

    public void moveDown(){
        player.setPlayerDirection(PlayerDirection.DOWN);
        move(0, 1);
    }

    public void moveLeft(){
        player.setPlayerDirection(PlayerDirection.LEFT);
        move(-1, 0);
    }

    public void moveRight(){
        player.setPlayerDirection(PlayerDirection.RIGHT);
        move(1, 0);
    }

    public boolean move(int x, int y){
        int newX = player.getPosX() + x, newY = player.getPosY() + y;
        Field nextField = fields[newX][newY];

        if(nextField.getFieldType() != FieldType.WALL && nextField.getFieldType() != FieldType.BOX) {
            // Move Player
            moves.push(new MoveHistory(x, y, null, player.getPlayerDirection()));
            animateMovement(x, y, null);
            moveCounter++;
            listeners.forEach(l -> l.moveCounterChanged(moveCounter));
            return true;
        }else if(nextField.getFieldType() == FieldType.BOX
                && fields[newX + x][newY + y].getFieldType() == FieldType.EMPTY){
            // Move Player and Box
            MoveableObject moveBox = null;
            for (MoveableObject box : boxes) {
                if(box.posX == newX && box.posY == newY)
                    moveBox = box;
            }
            moves.push(new MoveHistory(x, y, moveBox, player.getPlayerDirection()));
            animateMovement(x, y, moveBox);
            nextField.setFieldType(FieldType.EMPTY);
            moveCounter++;
            listeners.forEach(l -> l.moveCounterChanged(moveCounter));
            listeners.forEach(l -> l.repaintView());

            if(nextField.isTarget() && boxesOnTargets.contains(nextField))
                boxesOnTargets.remove(nextField);
            if(fields[newX + x][newY + y].isTarget() && !boxesOnTargets.contains(fields[newX + x][newY + y]))
                boxesOnTargets.add(fields[newX + x][newY + y]);
            return true;
        }
        return false;
    }

    private void animateMovement(final int x, final int y, final MoveableObject box){
        inputBlocked = true;


        timer = new Timer(15, e -> {
            if(Math.abs(player.getxOffset()) >= GameView.SIZE || Math.abs(player.getyOffset()) >= GameView.SIZE){
                timer.stop();
                player.setxOffset(0);
                player.setyOffset(0);

                fields[player.getPosX()][player.getPosY()].hasPlayer(false);
                player.setPosX(player.getPosX() + x);
                player.setPosY(player.getPosY() + y);
                fields[player.getPosX()][player.getPosY()].hasPlayer(true);
                player.setImgIndex(0);

                if(box != null){
                    box.setxOffset(0);
                    box.setyOffset(0);
                    box.setPosX(box.getPosX() + x);
                    box.setPosY(box.getPosY() + y);
                    fields[box.getPosX()][box.getPosY()].setFieldType(FieldType.BOX);
                }

                if(boxesOnTargets.size() == targets) {
                    inputBlocked = true;
                    listeners.forEach(l -> l.levelFinished());
                }else
                    inputBlocked = false;
            }else{
                player.setxOffset(player.getxOffset() + (int)(x * (GameView.SIZE / 9f)));
                player.setyOffset(player.getyOffset() + (int)(y * (GameView.SIZE / 9f)));
                player.setImgIndex((player.getImgIndex() + 1) % 3);
                if(box != null){
                    box.setxOffset(box.getxOffset() + (int)(x * (GameView.SIZE / 9f)));
                    box.setyOffset(box.getyOffset() + (int)(y * (GameView.SIZE / 9f)));
                }
            }

            listeners.forEach(l -> l.repaintView());
        });
        timer.start();
    }

    public void undoMove(){
        if(!moves.empty()){
            MoveHistory lastMove = moves.pop();
            player.setPlayerDirection(lastMove.getDir());
            if(lastMove.getBox() != null){
                Field curBoxField = fields[lastMove.getBox().getPosX()][lastMove.getBox().getPosY()];
                Field newField = fields[lastMove.getBox().getPosX() - lastMove.getDeltaX()][lastMove.getBox().getPosY() - lastMove.getDeltaY()];
                curBoxField.setFieldType(FieldType.EMPTY);
                if(curBoxField.isTarget() && boxesOnTargets.contains(curBoxField))
                    boxesOnTargets.remove(curBoxField);

                newField.setFieldType(FieldType.BOX);
                if(newField.isTarget() && !boxesOnTargets.contains(newField))
                    boxesOnTargets.add(newField);
            }
            animateMovement(-lastMove.getDeltaX(), -lastMove.getDeltaY(), lastMove.getBox());
        }
    }

    @Override
    protected Level clone(){
        Field[][] newFields = new Field[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newFields[j][i] = fields[j][i].clone();
            }
        }
        Level copy = new Level(newFields, levelName, width, height);
        copy.listeners = listeners;
        return copy;
    }


    public void addLevelFinishedCallback(LevelCallbacks callback){
        this.listeners.add(callback);
    }

    public void removeLevelFinishedCallback(LevelCallbacks callback){
        this.listeners.remove(callback);
    }
}
