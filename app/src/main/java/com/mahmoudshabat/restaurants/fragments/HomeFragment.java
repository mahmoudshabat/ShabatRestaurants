package com.mahmoudshabat.restaurants.fragments;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mahmoudshabat.restaurants.Activites.MainActivity;
import com.mahmoudshabat.restaurants.Activites.RestaurantDetailsActivity;
import com.mahmoudshabat.restaurants.model.RestaurantsModel;
import com.mahmoudshabat.restaurants.utils.GpsUtils;
import com.mahmoudshabat.restaurants.utils.PermissionUtils;
import com.mahmoudshabat.restaurants.utils.Utils;
import com.mahmoudshabat.restaurants.utils.customMap.CustomMapFragment;
import com.mahmoudshabat.restaurants.utils.customMap.MapWrapperLayout;
import com.mahmoudshabat.restaurants.viewModel.HomeViewModel;
import com.mahmoudshabat.restaurants.R;

import java.util.ArrayList;

import static com.mahmoudshabat.restaurants.utils.Constant.GPS_REQUEST;
import static com.mahmoudshabat.restaurants.utils.Constant.LOCATION_REQUEST;

public class HomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    View view;
    private HomeViewModel mViewModel;
    Boolean isGPSEnabled = false;
    GpsUtils gpsUtils;
    LatLng start, center;
    ImageView markerDot, marker;
    FloatingActionButton addRestaurant;
    ArrayList<RestaurantsModel> restaurantsList = new ArrayList<RestaurantsModel>();
    ArrayList<Marker> markersList = new ArrayList<Marker>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.home_fragment, container, false);

        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        observeRestaurants();
        initView();
        gpsUtils = new GpsUtils(getContext(), this);
        gpsUtils.turnGPSOn(isGPSEnable -> isGPSEnabled = isGPSEnable);
        return view;
    }


    private void observeRestaurants() {
        mViewModel.restaurants().observe(this, queryDocumentSnapshots -> {
            if(mMap==null){
                return;
            }
            mMap.clear();
            restaurantsList.clear();
            int i = 0 ;
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                RestaurantsModel restaurantsModel = document.toObject(RestaurantsModel.class);
                restaurantsModel.setId(document.getId());
                restaurantsList.add(restaurantsModel) ;
                LatLng latLng = new LatLng(restaurantsModel.getLocation().getLatitude(), restaurantsModel.getLocation().getLongitude());
                Marker  mMarker =mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_res)));
                mMarker.setTag(i);
                markersList.add(mMarker) ;
                i++ ;
            }
        });
    }

    private void initView() {
        markerDot = view.findViewById(R.id.markerDot);
        marker = view.findViewById(R.id.marker);
        addRestaurant = view.findViewById(R.id.addRestaurant);

        markerDot.setVisibility(View.INVISIBLE);
        marker.setVisibility(View.VISIBLE);
        addRestaurant.setOnClickListener(this);

    }


    @Override
    public void onStart() {
        super.onStart();
        invokeLocationAction();
        if (isGPSEnabled)
            setProgressDialog();
    }

    CustomMapFragment mapFragment;

    private void initMap() {
        if (isGPSEnabled) {
            mapFragment = (CustomMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            mapFragment.setOnTouchesListener(new MapWrapperLayout.OnTouchesListener() {
                @Override
                public void onTouchEnded(MotionEvent motionEvent) {


                }

                @Override
                public void onTouchStarted(MotionEvent motionEvent) {
                    markerDot.setVisibility(View.VISIBLE);
                    marker.setVisibility(View.GONE);
                }
            });


            if (view != null &&
                    view.findViewById(Integer.parseInt("1")) != null) {
                View locationButton = ((View) view.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 0, Utils.dpToPx(getContext(), 90));
            }

        }

    }

    private void invokeLocationAction() {
        if (isGPSEnabled && !PermissionUtils.hasPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);

        } else {

            startLocationUpdate();
        }

    }

    private void startLocationUpdate() {
        mViewModel.getLocationData().observe(this, locationModel -> {
            start = new LatLng(locationModel.getLat(), locationModel.getLng());
            center = start ;
            initMap();
            dismissProgressDialog();
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true;
                setProgressDialog();
                invokeLocationAction();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST: {
                invokeLocationAction();
            }
        }
    }

    GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap mMap) {
        if (this.mMap != null) {
            mapFragment.setGoogleMap(mMap);
            return;
        }
        this.mMap = mMap;
        mapFragment.setGoogleMap(mMap);
        mMap.setMyLocationEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 17.0f));

        mMap.setOnCameraIdleListener(() -> {
            mViewModel.getRestaurants(center) ;
            markerDot.setVisibility(View.INVISIBLE);
            marker.setVisibility(View.VISIBLE);
        });


        mMap.setOnCameraMoveListener(() -> center = mMap.getCameraPosition().target);


        mMap.setOnMarkerClickListener(marker -> {
            HomeFragmentDirections.ActionHomeFragmentToRestaurantDetailsActivity action =
                    HomeFragmentDirections.actionHomeFragmentToRestaurantDetailsActivity(restaurantsList.get((int)marker.getTag())) ;
            Navigation.findNavController(view).navigate(action);
            return true;
        });
    }


    AlertDialog dialog;

    private void setProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(getContext());
        tvText.setText(getString(R.string.locating));
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);

        builder.setView(ll);

        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);

        }
    }

    private void dismissProgressDialog() {
        dialog.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addRestaurant:
                HomeFragmentDirections.ActionHomeFragmentToAddRestaurantFragment action =
                    HomeFragmentDirections.actionHomeFragmentToAddRestaurantFragment() ;
                action.setLat((float)center.latitude) ;
                action.setLng((float)center.longitude) ;
                Navigation.findNavController(view).navigate(action);
                break;
        }
    }
}
