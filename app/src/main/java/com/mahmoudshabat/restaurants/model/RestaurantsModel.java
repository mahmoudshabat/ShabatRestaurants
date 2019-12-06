package com.mahmoudshabat.restaurants.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class RestaurantsModel implements Parcelable {
    String id ;
    String name ;
    String description ;
    String geohash ;
    GeoPoint location ;
    ArrayList<String> images;

    public RestaurantsModel() {

    }


    protected RestaurantsModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        geohash = in.readString();
        images = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(geohash);
        dest.writeStringList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RestaurantsModel> CREATOR = new Creator<RestaurantsModel>() {
        @Override
        public RestaurantsModel createFromParcel(Parcel in) {
            return new RestaurantsModel(in);
        }

        @Override
        public RestaurantsModel[] newArray(int size) {
            return new RestaurantsModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
