import java.io.Serializable;

public class Field implements Cloneable, Serializable {

    private char value;
    private boolean isTarget, hasPlayer;
    private FieldType fieldType;


    public Field(char value){
        this.value = value;
        this.isTarget = false;
        this.hasPlayer = false;

        switch (value){
            case ' ':
                this.fieldType = FieldType.EMPTY;
                break;
            case '@':
                this.fieldType = FieldType.EMPTY;
                hasPlayer = true;
                break;
            case '$':
                this.fieldType = FieldType.BOX;
                break;
            case '#':
                this.fieldType = FieldType.WALL;
                break;
            case '.':
                this.fieldType = FieldType.EMPTY;
                isTarget = true;
                break;
            case '+':
                this.fieldType = FieldType.EMPTY;
                hasPlayer = true;
                isTarget = true;
                break;
            case '*':
                this.fieldType = FieldType.BOX;
                isTarget = true;
                break;
            default:
                this.fieldType = FieldType.EMPTY;
                break;
        }
    }

    public boolean isTarget() {
        return isTarget;
    }

    public void isTarget(boolean target) {
        isTarget = target;
    }

    public boolean hasPlayer() {
        return hasPlayer;
    }

    public void hasPlayer(boolean hasPlayer) {
        this.hasPlayer = hasPlayer;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    @Override
    protected Field clone() {
        return new Field(value);
    }
}
