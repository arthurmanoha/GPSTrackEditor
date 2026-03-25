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
    private boolean isDragging;
    private int mouseX = -1, mouseY = -1;

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
        isDragging = false;
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
    }

    public void toggleDisplayMode() {
        displayMode.toggle();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
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
}
