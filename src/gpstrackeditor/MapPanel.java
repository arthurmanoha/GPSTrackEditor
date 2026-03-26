package gpstrackeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * Display visual information about a list of coordinates.
 *
 * @author arthu
 */
public class MapPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private GPSTrack track;
    int panelWidth = 1000;
    int panelHeight = 1000;

    private PaintMode displayMode;

    private double x0, y0, zoom;
    private double zoomMultiplicator;
    private int mouseX = -1, mouseY = -1;
    private int mouseClickX = -1, mouseClickY = -1;

    private ArrayList<Lift> allLifts;
    private String currentLiftName;
    private Lift.LiftType currentLiftType;
    private String liftsFile = "lifts.txt";

    public enum MapPanelMode {
        ADDING_START, ADDING_END
    }
    private MapPanelMode currentMode;

    public MapPanel(GPSTrack t) {
        track = t;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        displayMode = new PaintMode();
        x0 = 300;
        y0 = -5049;
        zoom = 1;
        zoomMultiplicator = 1.1;
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        allLifts = new ArrayList<>();
        currentMode = null;
        currentLiftType = null;
    }

    public void actionPerformed(KeyEvent e) {
        switch (e.getKeyChar()) {
        case '.':
            resetZoom();
            break;
        default:
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        // Paint blank
        g.setColor(Color.white);
        panelWidth = g.getClipBounds().width;
        panelHeight = g.getClipBounds().height;
        g.fillRect(0, 0, panelWidth, panelHeight);

        track.paint(g, displayMode, x0, y0, zoom);

        for (Lift lift : allLifts) {
            lift.paint(g, x0, y0, zoom);
        }
    }

    public void toggleDisplayMode() {
        displayMode.toggle();
        track.prepareColors(displayMode);
        repaint();
    }

    public String getDisplayMode() {
        return displayMode + "";
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseClickX = e.getX();
        mouseClickY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        // Add a lift only if click and release at the same location
        if (currentMode != null && mouseX == mouseClickX && mouseY == mouseClickY) {
            double longitude = (mouseX - x0) / zoom;
            double latitude = (panelHeight - mouseY - y0) / zoom;
            boolean isStart = (this.currentMode == MapPanelMode.ADDING_START);

            allLifts.add(new Lift(currentLiftType, longitude, latitude, currentLiftName, isStart));
            currentMode = null;
        }
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int dx = e.getX() - mouseX;
        int dy = e.getY() - mouseY;

        x0 += dx;
        y0 -= dy;
        repaint();

        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double zoomBefore = zoom;
        double rotation = e.getPreciseWheelRotation();

        if (rotation < 0) {
            zoom = zoom * (-zoomMultiplicator * rotation);
        } else {
            zoom = zoom / (zoomMultiplicator * rotation);
        }
        x0 = e.getX() - (e.getX() - x0) / (zoomBefore / zoom);
        y0 = panelHeight - (e.getY() - (e.getY() - (panelHeight - y0)) / (zoomBefore / zoom));
        repaint();
    }

    /**
     * Set the zoom and pan so that all the data is visible, at the highest
     * possible zoom.
     */
    public void resetZoom() {
        double xMin = track.getLongitudeMin();
        double xMax = track.getLongitudeMax();
        double xCenter = (xMin + xMax) / 2;
        double yMin = track.getLatitudeMin();
        double yMax = track.getLatitudeMax();
        double yCenter = (yMin + yMax) / 2;

        int margin = 10;
        double zoomFitX = (panelWidth - 2 * margin) / (xMax - xMin);
        double zoomFitY = (panelHeight - 2 * margin) / (yMax - yMin);
        zoom = Math.min(zoomFitX, zoomFitY);

        x0 = panelWidth / 2 - zoom * xCenter;
        y0 = panelHeight / 2 - zoom * yCenter;

        repaint();
    }

    public void addLift(Lift newLift) {
        allLifts.add(newLift);
    }

    protected void resetLifts() {
        allLifts.clear();
    }

    protected void setMode(MapPanelMode newMode) {
        this.currentMode = newMode;
    }

    protected void setLiftName(String text) {
        currentLiftName = text;
    }

    protected void setLiftType(Lift.LiftType liftType) {
        currentLiftType = liftType;
    }

    protected void loadLifts() {

        allLifts.clear();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(new File(liftsFile)));
            String line = reader.readLine();
            while (line != null) {
                Lift newLift = new Lift(line);
                allLifts.add(newLift);
                line = reader.readLine(); // Next line
            }
            reader.close();
            repaint();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MapPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void saveLifts() {
        try {
            FileWriter writer = new FileWriter(new File(liftsFile));

            for (Lift lift : allLifts) {
                lift.save(writer);
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(MapPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
