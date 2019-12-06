package com.mahmoudshabat.restaurants.viewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;
import com.mahmoudshabat.restaurants.model.LocationModel;


import ch.hsr.geohash.GeoHash;


public class HomeViewModel extends AndroidViewModel {

    private LiveData<LocationModel> locationData ;
    FirebaseFirestore db;
    MutableLiveData<QuerySnapshot> restaurants = new MutableLiveData<QuerySnapshot>();
    CollectionReference restaurantsRef ;
    public HomeViewModel(@NonNull Application application) {
        super(application);
        locationData = LocationLiveData.getInstance(application);
        db = FirebaseFirestore.getInstance();
        restaurantsRef= db.collection("restaurants") ;
    }

    public LiveData<LocationModel> getLocationData(){
        return  locationData ;
    }
    public LiveData<QuerySnapshot> restaurants(){
        return  restaurants ;
    }


    public void getRestaurants(LatLng center){
     GeoHash mGeoHash = GeoHash.withCharacterPrecision(center.latitude,center.longitude,5) ;
     String lesserGeoHash = GeoHash.withCharacterPrecision(mGeoHash.getBoundingBox().getMinLat(), mGeoHash.getBoundingBox().getMinLon(),5).toBase32() ;
     String greaterGeoHash = GeoHash.withCharacterPrecision(mGeoHash.getBoundingBox().getMaxLat(), mGeoHash.getBoundingBox().getMaxLon(),5).toBase32() ;

        db.collection("restaurants")
                .whereGreaterThan("geohash",lesserGeoHash)
                .whereLessThan("geohash",greaterGeoHash)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //you can get distance here between 2 point to more accuracy .
                        restaurants.setValue(task.getResult());
                    }
                });
    }
}
