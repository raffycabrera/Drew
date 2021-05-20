package edu.dlsu.drew;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {
    private String Disaster;
    private String longitude, latitude;
    private Date date;
    private String personSub;
    ArrayList<String> responders = new ArrayList<String>();
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    public String getPerson() {
        return personSub;
    }

    public void setPerson(String person) {
        this.personSub = person;
    }
    
    public ArrayList<String> getResponders(){
        return responders;
    }
    public void addResponders( String responder){
        responders.add(responder);
    }
    
    
}
