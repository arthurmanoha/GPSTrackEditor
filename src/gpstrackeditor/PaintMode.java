package gpstrackeditor;

/**
 *
 * @author arthu
 */
public class PaintMode {

    public enum MODE {
        SPEED, ACCELERATION, HEADING, ALTITUDE
    };

    private MODE currentMode;

    public PaintMode() {
        currentMode = MODE.SPEED;
    }

    public void toggle() {
        switch (currentMode) {
        case SPEED:
            currentMode = MODE.ALTITUDE;
            break;

        case ALTITUDE:
            currentMode = MODE.SPEED;
            break;

        default:
            currentMode = MODE.SPEED;
            break;
        }
    }

    public MODE getMode() {
        return currentMode;
    }

    @Override
    public String toString() {
        return currentMode.name();
    }
}
