/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gpstrackeditor;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author arthu
 */
public class Marker {

    private double longitude, latitude;
    private double altitude;
    private Color color;
    private static Color defaultColor = Color.black;
    private Date date;
    private Calendar calendar;

    public Marker(double newLongitude, double newLatitude) {
        this(newLongitude, newLatitude, 0, 0, 0, 0, 0, 0, 0);
    }

    public Marker(double newLongitude, double newLatitude, double newAltitude, int newYear, int newMonth, int newDay,
            int newHour, int newMin, int newSec) {
        longitude = newLongitude;
        latitude = newLatitude;
        altitude = newAltitude;
        color = defaultColor;
        calendar = Calendar.getInstance();
        calendar.set(newYear, newMonth, newDay, newHour, newMin, newSec);
    }

    public void setColor(Color newColor) {
        color = newColor;
    }

    public Color getColor() {
        return color;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Compute and return the time elapsed between previousMarker and this, in
     * seconds.
     *
     * @param prev
     * @return
     */
    double getTimeDelay(Marker prev) {
        double delay = (calendar.getTimeInMillis() - prev.calendar.getTimeInMillis()) / 1000;
        return delay;
    }

    double getAltitude() {
        return altitude;
    }
}
