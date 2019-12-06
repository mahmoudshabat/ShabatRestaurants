package com.mahmoudshabat.restaurants.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mahmoudshabat.restaurants.R;
import com.mahmoudshabat.restaurants.viewModel.LocationLiveData;

import static com.mahmoudshabat.restaurants.utils.Constant.GPS_REQUEST;


public class GpsUtils {
    SettingsClient settingsClient;
    LocationSettingsRequest locationSettingsRequest;
    LocationManager locationManager ;
    Context context ;
    Fragment home ;
    public GpsUtils(Context context,Fragment t) {
        this.context = context ;
        this.home = t ;

        settingsClient = LocationServices.getSettingsClient(context);
        locationManager =(LocationManager) context.getSystemService(Context.LOCATION_SERVICE) ;
        locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationLiveData.locationRequest).setAlwaysShow(true).build();


    }



    public void turnGPSOn(OnGpsListener onGpsListener){
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onGpsListener.gpsStatus(true) ;
        } else {
            mResult(onGpsListener);
        }
    }


    private void mResult(final OnGpsListener onGpsListener){
        Task<LocationSettingsResponse> pendingResult =
                settingsClient.checkLocationSettings(locationSettingsRequest);

        pendingResult.addOnSuccessListener((Activity) context, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                onGpsListener.gpsStatus(true) ;
            }
        });



        pendingResult.addOnFailureListener((Activity) context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if((((ResolvableApiException) e)).getStatusCode()== LocationSettingsStatusCodes.RESOLUTION_REQUIRED){
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        ResolvableApiException rae = ((ResolvableApiException) e) ;
                   //   rae.startResolutionForResult((Activity) context, GPS_REQUEST) ;

                     (home).startIntentSenderForResult((rae.getResolution().getIntentSender()), GPS_REQUEST, null, 0, 0, 0, null);

                    } catch (IntentSender.SendIntentException ee) {

                    } catch (ClassCastException ee) {

                    }
                }

            }
        }) ;
    }

    public interface OnGpsListener {
        void gpsStatus(Boolean isGPSEnable);
    }


}
