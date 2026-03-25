package gpstrackeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author arthu
 */
public class GPSTrackEditor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        String folder = "D:\\GPS_Logger\\";
        String filename = folder + "20260325-102054 - Ski Vars 2026-03-25.gpx";

        GPSTrack t = new GPSTrack(filename);
        JFrame frame = new JFrame();
        int DEFAULT_WIDTH = 1000, DEFAULT_HEIGHT = 800;
        frame.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // mainPanel contains the buttons (north) and the map (south).
        frame.setLayout(new BorderLayout());

        MapPanel mapPanel = new MapPanel(t);
        frame.add(new ControlPanel(mapPanel), BorderLayout.NORTH);
        frame.add(mapPanel, BorderLayout.CENTER);

        frame.setVisible(true);
        mapPanel.resetZoom();
    }

}
