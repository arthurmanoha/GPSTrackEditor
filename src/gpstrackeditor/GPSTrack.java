package gpstrackeditor;

import colorramp.ColorRamp;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author arthu
 */
public class GPSTrack {

    private ArrayList<Marker> markerList;

    private static colorramp.ColorRamp ramp = null;

    private double maxSpeed;
    private double totalDistance;

    public GPSTrack(String filename) {

        markerList = new ArrayList<>();
        maxSpeed = 0;
        totalDistance = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));

            String line = "";

            if (filename.endsWith(".gpx")) {
                System.out.println("GPX file");
                while (!(line = reader.readLine()).startsWith("  <trkpt")) {
                    // Ignore file header
                }
                do {
                    try {
                        //   <trkpt lat="44.57250167" lon="6.67954000"><ele>1907.700</ele><time>2026-03-25T09:20:54.008Z</time><speed>0.152</speed><sat>16</sat></trkpt>
                        // Split on any of these characters:
                        String tab[] = line.split("<|>|\"|-|T|Z|:");

                        double latitude = Double.valueOf(tab[2]);
                        double longitude = Double.valueOf(tab[4]);
                        int year = Integer.valueOf(tab[12]);
                        int month = Integer.valueOf(tab[13]);
                        int day = Integer.valueOf(tab[14]);
                        int hour = Integer.valueOf(tab[15]);
                        int min = Integer.valueOf(tab[16]);
                        String secString = tab[17];
                        int sec = Integer.valueOf(secString.split("\\.")[0]);

                        markerList.add(new Marker(longitude, latitude, year, month, day, hour, min, sec));

                    } catch (ArrayIndexOutOfBoundsException e) {
                        // File footer, ignore this
                    }
                } while ((line = reader.readLine()) != null);
                computeTotalDistance();
                String distanceString = totalDistance / 1000 + "";
                if (distanceString.contains(".")) {
                    distanceString = distanceString.substring(0, distanceString.indexOf(".") + 3);
                }
                System.out.println("Track created: " + distanceString + " km.");
            }

            // XML File
//            do {
//                line = replace(line, ',', '.');
////                System.out.println("line: <" + line + ">");
//                String tab[] = line.split(" ");
//                double longitude = Double.valueOf(tab[0]);
//                double latitude = Double.valueOf(tab[1]);
//
//                String dateTab[] = tab[2].split("[-:ZT]");
////                System.out.print(tab[2] + " -> ");
////                for (String datePart : dateTab) {
////                    System.out.print("(" + datePart + ")");
////                }
////                System.out.println("");
//                if (!dateTab[0].equals("2020")) {
//                    System.out.println("Not 2020: " + dateTab[0] + " at line " + lineNum + ", " + tab[2]);
//                }
////                System.out.println("Converting " + dateTab[0] + " into a year.");
//                int year = Integer.valueOf(dateTab[0]);
//                int month = Integer.valueOf(dateTab[1]);
//                int day = Integer.valueOf(dateTab[2]);
//                int hour = Integer.valueOf(dateTab[3]);
//                int min = Integer.valueOf(dateTab[4]);
//                int sec = Integer.valueOf(dateTab[5]);
//
//                markerList.add(new Marker(longitude, latitude, year, month, day, hour, min, sec));
//                lineNum++;
        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " not found.");
        } catch (IOException e) {
            System.out.println("IO exception while reading file.");
        }

        // Set the color ramp for speeds.
        if (ramp == null) {
            ramp = new ColorRamp();
            setRampValuesForSpeed();
        }
    }

    /**
     * Replace every occurrence of 'original' by 'replacement' in the given
     * String.
     *
     * @param line
     * @param original
     * @param replacement
     * @return
     */
    private String replace(String line, char original, char replacement) {
        String result = "";
        for (int rank = 0; rank < line.length(); rank++) {
            if (line.charAt(rank) == original) {
                result = result.concat(replacement + "");
            } else {
                result = result.concat(line.charAt(rank) + "");
            }
        }
        return result;
    }

    /**
     * Compute the color of each marker.
     *
     */
    private void prepareColors(PaintMode mode) {

        switch (mode.getMode()) {
        case SPEED:
            for (int markerIndex = 0; markerIndex < markerList.size(); markerIndex++) {
                double speed = computeSpeed(markerIndex);
                Color c = ramp.getValue(speed);
                markerList.get(markerIndex).setColor(c);
            }
            break;
        case ACCELERATION:
            break;
        default:
            break;
        }
    }

    /**
     * Draw each marker with its own color.
     *
     * @param g
     * @param displayMode
     */
    public void paint(Graphics g, PaintMode displayMode, double x0, double y0, double zoom) {
        prepareColors(displayMode);
        int index = 0;

        Marker previous = null;
        float lineWidth = 3;
        Graphics2D g2d = (Graphics2D) g;

        int rank = 0;
        for (Marker m : markerList) {
            g.setColor(m.getColor());
            int xApp = (int) (x0 + zoom * m.getLongitude());
            int yApp = (int) (g.getClipBounds().height - (y0 + zoom * m.getLatitude()));

            index++;

            if (previous != null) {
                int xAppPrev = (int) (x0 + zoom * previous.getLongitude());
                int yAppPrev = (int) (g.getClipBounds().height - (y0 + zoom * previous.getLatitude()));
                // Draw a line between current and previous
                g2d.setStroke(new BasicStroke(lineWidth));
                g2d.drawLine(xApp, yApp, xAppPrev, yAppPrev);
            }
            previous = m;
            rank++;
        }
    }

    /**
     * Compute the speed between the previous and the current marker.
     *
     * @param markerIndex
     * @return
     */
    private double computeSpeed(int markerIndex) {

        int nbPointsForAvg = 10;

        if (markerIndex < nbPointsForAvg) {
            return 0;
        }

        Marker currentMarker = markerList.get(markerIndex);
        Point currentPoint = new Point(currentMarker.getLatitude(), currentMarker.getLongitude());
        Marker previousMarker = markerList.get(markerIndex - nbPointsForAvg);
        Point previousPoint = new Point(previousMarker.getLatitude(), previousMarker.getLongitude());

        double distance = currentPoint.getDistance(previousPoint);

        double delay = currentMarker.getTimeDelay(previousMarker);

        double speed = distance / delay;
        return speed;
    }

    private void setRampValuesForSpeed() {

        ramp.addValue(0.0, Color.black);
        ramp.addValue(0.1, Color.red);
        ramp.addValue(1, Color.orange);
        ramp.addValue(10, Color.yellow);
        ramp.addValue(100, Color.green);
        ramp.addValue(1000, Color.blue);
    }

    /**
     * Compute the total length of the path, in meters.
     *
     * @return
     */
    private double computeTotalDistance() {
        totalDistance = 0;
        Marker previous = null;
        Point previousPoint = null;

        double maxStep = 0;
        for (Marker m : markerList) {
            Point currentPoint = new Point(m.getLongitude(), m.getLatitude());
            if (previous != null) {
                double stepDistance = currentPoint.getDistance(previousPoint);
                totalDistance += stepDistance;
                if (stepDistance > maxStep) {
                    maxStep = stepDistance;
                }
            }
            previous = m;
            previousPoint = currentPoint;
        }
        return totalDistance;
    }

    double getLongitudeMin() {
        double longitudeMin = Double.MAX_VALUE;
        for (Marker m : markerList) {
            if (m.getLongitude() < longitudeMin) {
                longitudeMin = m.getLongitude();
            }
        }
        return longitudeMin;
    }

    double getLongitudeMax() {
        double longitudeMax = Double.MIN_VALUE;
        for (Marker m : markerList) {
            if (m.getLongitude() > longitudeMax) {
                longitudeMax = m.getLongitude();
            }
        }
        return longitudeMax;
    }

    double getLatitudeMin() {
        double latitudeMin = Double.MAX_VALUE;
        for (Marker m : markerList) {
            if (m.getLatitude() < latitudeMin) {
                latitudeMin = m.getLatitude();
            }
        }
        return latitudeMin;
    }

    double getLatitudeMax() {
        double latitudeMin = Double.MIN_VALUE;
        for (Marker m : markerList) {
            if (m.getLatitude() > latitudeMin) {
                latitudeMin = m.getLatitude();
            }
        }
        return latitudeMin;
    }

}
