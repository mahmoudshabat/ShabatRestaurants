package com.mahmoudshabat.restaurants.viewModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.mahmoudshabat.restaurants.model.LocationModel;

public class LocationLiveData  extends LiveData<LocationModel> {
    FusedLocationProviderClient fusedLocationClient ;
    private static LocationLiveData instance;
    public static LocationRequest locationRequest;
    public LocationLiveData(Context context) {
       fusedLocationClient = LocationServices.getFusedLocationProviderClient(context) ;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(10 * 1000);

    }
    public static LocationLiveData getInstance(Context appContext) {
        if (instance == null) {
            instance = new LocationLiveData(appContext);
        }
        return instance;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        super.onActive();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if(location!=null){
                        setLocationData(location);
                    }else{
                        startLocationUpdates() ;
                    }

                }) ;
        }


    private void setLocationData(Location location ) {
        LocationModel  mLocationModel =new LocationModel(location.getLatitude(),location.getLongitude());
        setValue(mLocationModel);
        stopUpdateLocation() ;
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            super.onLocationResult(result);
                setLocationData(result.getLastLocation()) ;
        }
    };
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
        );
    }

    private void stopUpdateLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    @Override
    protected void onInactive() {
        super.onInactive();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }









}
