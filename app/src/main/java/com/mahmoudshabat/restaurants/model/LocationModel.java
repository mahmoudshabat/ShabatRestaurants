package com.mahmoudshabat.restaurants.model;

public class LocationModel {
    double lat ;
    double lng ;

    public LocationModel(double latitude, double longitude) {
        lat = latitude ;
        lng = longitude ;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
