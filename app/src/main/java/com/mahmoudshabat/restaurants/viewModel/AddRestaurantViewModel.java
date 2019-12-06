package com.mahmoudshabat.restaurants.viewModel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mahmoudshabat.restaurants.model.ImageModel;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ch.hsr.geohash.GeoHash;

public class AddRestaurantViewModel extends AndroidViewModel {
     ;
    StorageReference storageRef  ;
    ArrayList<String> uploadedImages = new ArrayList<String> ();
    int countImages  ;
    DocumentReference restaurantRef ;
    MutableLiveData<Boolean> isAdded = new MutableLiveData<Boolean>();
    public AddRestaurantViewModel(@NonNull Application application) {
        super(application);
        FirebaseStorage  storage = FirebaseStorage.getInstance() ;
        storageRef = storage.getReference().child("restaurantsMedia");
        FirebaseFirestore  db =  FirebaseFirestore.getInstance() ;
        restaurantRef = db.collection("restaurants").document();
    }

    public LiveData<Boolean> isAdded (){
        return  isAdded ;
    }


    public void uploadSingleFile(String path,String name , String desc , GeoPoint geoPoint) {
        Uri file = Uri.fromFile(new File(path));
        StorageReference fileRef  = storageRef.child(file.getLastPathSegment()) ;

        UploadTask uploadTask = fileRef.putFile(file);


        uploadTask.addOnFailureListener(exception -> isAdded.setValue(false)).addOnSuccessListener(taskSnapshot ->
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(uri -> {
                    uploadedImages.add(uri.toString());
                    if(uploadedImages.size() == countImages){
                        saveRestaurant(name,desc,geoPoint) ;
                    }
                }));
    }

    private void saveRestaurant(String name, String desc, GeoPoint geoPoint) {
        Map<String, Object> restaurant = new HashMap<>();
        restaurant.put("name", name);
        restaurant.put("description", desc);
        restaurant.put("location", geoPoint);
        restaurant.put("images", uploadedImages);
        restaurant.put("geohash", GeoHash.withCharacterPrecision(geoPoint.getLatitude(),geoPoint.getLongitude(),10).toBase32());

        restaurantRef
                .set(restaurant)
                .addOnSuccessListener(aVoid -> isAdded.setValue(true))
                .addOnFailureListener(e -> isAdded.setValue(false));

    }

    public void addRestaurant(String name , String desc , GeoPoint geoPoint, ArrayList<ImageModel> images) {
        countImages = images.size() ;
        for (int i =0 ; i < images.size(); i++) {
            uploadSingleFile(images.get(i).getUrl(),name,desc,geoPoint);
        }
    }





}
