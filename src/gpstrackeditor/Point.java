package gpstrackeditor;

/**
 *
 * This class represents a point with cartesian coordinates in a 3-dimensional
 * space.
 *
 * @author arthu
 */
public class Point {

    // Coordinates in meters.
    private double x, y, z;

    public Point(double newX, double newY, double newZ) {
        x = newX;
        y = newY;
        z = newZ;
    }

    /**
     * Create a point from spherical coordinates.
     *
     * @param longitude
     * @param latitude
     */
    public Point(double longitude, double latitude) {
        double earthRadius = 6400000; // Meters
        double latitudeInRadians = latitude * Math.PI / 180;
        double longitudeInRadians = longitude * Math.PI / 180;
        x = earthRadius * Math.cos(latitudeInRadians) * Math.cos(longitudeInRadians);
        y = earthRadius * Math.cos(latitudeInRadians) * Math.sin(longitudeInRadians);
        z = earthRadius * Math.sin(latitudeInRadians);
    }

    /**
     * Compute the distance in meters between this point and another one.
     *
     * @param otherPoint
     * @return the distance in meters
     */
    double getDistance(Point otherPoint) {
        double dx = this.x - otherPoint.x;
        double dy = this.y - otherPoint.y;
        double dz = this.z - otherPoint.z;
        return Math.sqrt((dx * dx + dy * dy + dz * dz));
    }
}
