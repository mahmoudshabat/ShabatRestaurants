package com.mahmoudshabat.restaurants.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mahmoudshabat.restaurants.R;

public class SplashFragment extends Fragment {

    View view ;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.splash_fragment, container, false);
        return view ;
    }

    private void goToHome() {
        new Handler().postDelayed(() ->
                Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_homeFragment),  1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        goToHome();
    }
}
