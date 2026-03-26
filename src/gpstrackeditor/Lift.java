package gpstrackeditor;

import java.awt.Color;
import java.awt.Graphics;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A lift represents any machine that gets people up the mountain.
 * They can be chairlifts, ski lifts, gondola
 *
 * @author arthu
 */
public class Lift {

    public enum LiftType {
        CHAIRLIFT, SKILIFT, GONDOLA, TELEMIX
    }
    private static String IS_START = "isStart";
    private static String IS_END = "isEnd";

    private LiftType type;
    private boolean isStart;
    private double longitude, latitude;
    private String name;
    private double radius;

    public Lift(LiftType newType, double lon, double lat, String name, boolean isStart) {
        this.type = newType;
        this.longitude = lon;
        this.latitude = lat;
        this.name = name;
        this.isStart = isStart;
        this.radius = 0.002;
    }

    public Lift(String text) {
        this(null, 0, 0, null, true);
        String tab[] = text.split(" ");
        this.type = LiftType.valueOf(tab[0]);
        this.longitude = Double.valueOf(tab[1]);
        this.latitude = Double.valueOf(tab[2]);
        this.name = tab[3];
        this.isStart = tab[4].equals(IS_START);
    }

    public void paint(Graphics g, double x0, double y0, double zoom) {
        int xApp = (int) (x0 + zoom * longitude);
        int yApp = (int) (g.getClipBounds().height - (y0 + zoom * latitude));

        // Display a triangle to show start/end
        int rApp = (int) ((radius / 3) * zoom);
        g.setColor(Color.red);
        drawTriangle(g, xApp, yApp, rApp, isStart);

        // Draw the circle of influence
        rApp = (int) (radius * zoom);
        g.drawOval(xApp - rApp / 2, yApp - rApp / 2, rApp, rApp);

        g.drawString(this.name, xApp + rApp, yApp);
    }

    private void drawTriangle(Graphics g, int xApp, int yApp, int rApp, boolean start) {

        int[] xPoints = {xApp - rApp / 2, xApp, xApp + rApp / 2};
        int dy = (start ? 1 : -1);

        int[] yPoints = {yApp + dy * rApp / 2, yApp - dy * rApp / 2, yApp + dy * rApp / 2};
        g.drawPolygon(xPoints, yPoints, 3);
    }

    @Override
    public String toString() {
        return this.type + " "
                + longitude + " " + latitude + " "
                + name + " "
                + (isStart ? IS_START : IS_END);
    }

    public void save(FileWriter writer) {
        try {
            writer.write(this.toString() + "\n");
        } catch (IOException ex) {
            Logger.getLogger(Lift.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
