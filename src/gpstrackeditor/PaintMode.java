package gpstrackeditor;

/**
 *
 * @author arthu
 */
public class PaintMode {

    public enum MODE {
        SPEED, ACCELERATION, HEADING
    };

    private MODE currentMode;

    public PaintMode() {
        currentMode = MODE.SPEED;
    }

    public void toggle() {
        switch (currentMode) {
            case SPEED:
                currentMode = MODE.ACCELERATION;
                break;

            case ACCELERATION:
                currentMode = MODE.HEADING;
                break;
            default:
                currentMode = MODE.SPEED;
                break;
        }
    }

    public MODE getMode() {
        return currentMode;
    }
}
