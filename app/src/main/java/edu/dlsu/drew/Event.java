package edu.dlsu.drew;

public class Event {
    private String Disaster;
    private String longitude, latitude;

    public Event() {
    }

    public String getName() {
        return Disaster;
    }

    public void setName(String name) {
        Disaster = name;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude (String longi) {
        longitude = longi;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLatitude (String lati) {
        latitude = lati;
    }
}
