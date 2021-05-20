package edu.dlsu.drew;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

public class Hospital {
    private String Disaster;
    private GeoPoint longitude;

    public Hospital() {
    }

    public String getName() {
        return Disaster;
    }

    public void setName(String name) {
        Disaster = name;
    }

    public GeoPoint getLongitude() {
        return longitude;
    }

    public void setLongitude (GeoPoint longi) {
        longitude = longi;
    }

}
