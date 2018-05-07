package com.app.fakegps.model;

public class FavLocation {

    String locationName;
    double lat;
    double lang;


    public FavLocation(String locationName, double lat, double lang) {
        this.locationName = locationName;
        this.lat = lat;
        this.lang = lang;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLang() {
        return lang;
    }

    public void setLang(double lang) {
        this.lang = lang;
    }
}
