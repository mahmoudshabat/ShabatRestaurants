package com.mahmoudshabat.restaurants.viewModel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mahmoudshabat.restaurants.model.RestaurantsModel;
import com.mahmoudshabat.restaurants.viewModel.LocationLiveData;

import java.util.concurrent.Executor;

import ch.hsr.geohash.GeoHash;

import static com.mahmoudshabat.restaurants.utils.Constant.DYNAMIC_LINKS_PREFIX;


public class RestaurantDetailsViewModel extends AndroidViewModel {
    FirebaseFirestore db;
    CollectionReference restaurantsRef ;
    MutableLiveData<RestaurantsModel> restaurant = new MutableLiveData<RestaurantsModel>();
    MutableLiveData<String> error = new MutableLiveData<String>();
    public RestaurantDetailsViewModel(@NonNull Application application) {
        super(application);
        db = FirebaseFirestore.getInstance();
        restaurantsRef= db.collection("restaurants") ;
    }
    public LiveData<RestaurantsModel> restaurant(){
        return  restaurant ;
    }

    public void getRestaurant(String id){
        restaurantsRef
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            RestaurantsModel restaurantsModel = document.toObject(RestaurantsModel.class);
                            if (document.exists()) {
                                restaurant.setValue(restaurantsModel);
                            } else {

                            }
                        } else {

                        }
                    }
                });
    }

}
