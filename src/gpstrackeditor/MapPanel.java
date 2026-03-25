package gpstrackeditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
    private int HEIGHT = 1000, WIDTH = 800;

    private PaintMode displayMode;

    private double x0, y0, zoom;
    private int zoomLevel;
    private double zoomMultiplicator;
    private boolean isDragging;
    private int mouseX = -1, mouseY = -1;

    public MapPanel(GPSTrack t) {
        track = t;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        displayMode = new PaintMode();
        x0 = 300;
        y0 = -5049;
        zoomLevel = -48;
        zoomMultiplicator = 1.1;
        computeZoom();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        isDragging = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        // Paint blank
        g.setColor(Color.white);
        g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
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
        zoomLevel += e.getPreciseWheelRotation();
        double zoomBefore = zoom;
        computeZoom();
        double zoomAfter = zoom;
        x0 = e.getX() - (e.getX() - x0) / (zoomBefore / zoomAfter);
        int panelHeight = this.getHeight();
        y0 = panelHeight - (e.getY() - (e.getY() - (panelHeight - y0)) / (zoomBefore / zoomAfter));
        repaint();
    }

    private void computeZoom() {
        zoom = 1;
        if (zoomLevel > 0) {
            for (int i = 1; i <= zoomLevel; i++) {
                zoom = zoom / zoomMultiplicator;
            }
        } else {
            for (int i = 1; i >= zoomLevel; i--) {
                zoom = zoom * zoomMultiplicator;
            }
        }
    }
}
