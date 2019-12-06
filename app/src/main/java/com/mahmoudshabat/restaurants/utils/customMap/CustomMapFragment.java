package com.mahmoudshabat.restaurants.utils.customMap;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;


public class CustomMapFragment extends SupportMapFragment {
    private MapWrapperLayout mMapWrapperLayout;
    private View mOriginalView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOriginalView = super.onCreateView(inflater, container, savedInstanceState);
        mMapWrapperLayout = new MapWrapperLayout(getActivity());
        mMapWrapperLayout.addView(mOriginalView);

        return this.mMapWrapperLayout;
    }

    public View getView() {
        return this.mOriginalView;
    }

    public void setOnTouchesListener(MapWrapperLayout.OnTouchesListener onTouchesListener) {
        mMapWrapperLayout.setOnTouchesListener(onTouchesListener);
    }

    public void setGoogleMap(GoogleMap map) {
        this.mMapWrapperLayout.init(map);
    }
}