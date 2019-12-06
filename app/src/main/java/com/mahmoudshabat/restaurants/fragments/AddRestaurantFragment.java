package com.mahmoudshabat.restaurants.fragments;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.firebase.firestore.GeoPoint;
import com.mahmoudshabat.restaurants.Adapters.GalleryAdapter;
import com.mahmoudshabat.restaurants.model.ImageModel;
import com.mahmoudshabat.restaurants.R;
import com.mahmoudshabat.restaurants.utils.InputValidatorHelper;
import com.mahmoudshabat.restaurants.utils.Utils;
import com.mahmoudshabat.restaurants.utils.VarColumnGridLayoutManager;
import com.mahmoudshabat.restaurants.viewModel.AddRestaurantViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddRestaurantFragment extends Fragment implements View.OnClickListener {

    private AddRestaurantViewModel mViewModel;
    private View view ;
    private EditText etName ,etDescription ;
    private ImageView icArrowBack;
    private Button btnAdd ;
    ProgressBar progressBar ;
    ArrayList<ImageModel> GalleryList;
    GalleryAdapter mGalleryAdapter ;
    double lat , lng ;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_restaurant_fragment, container, false);
        Utils.setupUIForKeyBoard(view.findViewById(R.id.parent),getActivity()) ;
        if(getArguments()!=null){
            lat = AddRestaurantFragmentArgs.fromBundle(getArguments()).getLat();
            lng = AddRestaurantFragmentArgs.fromBundle(getArguments()).getLng();
        }
        initView();
        initImages();
        return view ;
    }

    private void initView() {
        icArrowBack =  view.findViewById(R.id.icArrowBack);
        etName =  view.findViewById(R.id.etName);
        etDescription = view.findViewById(R.id.etDescription);
        btnAdd = view.findViewById(R.id.btnAdd);
        progressBar = view.findViewById(R.id.progressBar);

        icArrowBack.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
    }

    private void initImages() {
        GalleryList = new ArrayList<ImageModel>();
        RecyclerView rvImages =  view.findViewById(R.id.rvImages);
        VarColumnGridLayoutManager layoutManager = new VarColumnGridLayoutManager(getContext(),Utils.dpToPx(getContext(),70));
        rvImages.setLayoutManager(layoutManager);
        mGalleryAdapter = new GalleryAdapter();
        rvImages.setAdapter(mGalleryAdapter);
        mGalleryAdapter.setOnItemClickListener((imageModel, position) -> {

        });
        mGalleryAdapter.setOnAddClickListener(() -> openImagePicker());
        mGalleryAdapter.setOnRemoveClickListener(position -> mGalleryAdapter.removeImage(position));

    }

    public void openImagePicker() {
        ImagePicker.create(this)
                .folderMode(true)
                .theme(R.style.ImagePickerTheme)
                .toolbarFolderTitle(getString(R.string.albums))
                .toolbarImageTitle(getString(R.string.tab_to_select))
                .start();
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            for (int i = 0; i < images.size(); i++) {
                GalleryList.add(new ImageModel(images.get(i).getName(), images.get(i).getPath()));
            }
            mGalleryAdapter.setImagesList(GalleryList);
            images.clear();


        }

    }




    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddRestaurantViewModel.class);
        observeAdd();
    }

    private void observeAdd() {
        mViewModel.isAdded().observe(this, aBoolean -> {
            if(aBoolean){
                Navigation.findNavController(view).popBackStack();
                Toast.makeText(getContext(),getString(R.string.added_successfully),Toast.LENGTH_SHORT).show();
            }else{
                progressBar.setVisibility(View.VISIBLE);
                btnAdd.setEnabled(false);
                Toast.makeText(getContext(),getString(R.string.failed),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icArrowBack :
                Navigation.findNavController(view).popBackStack();
                break;
            case R.id.btnAdd :
                addRestaurant();
                break;

        }
    }

    private void addRestaurant() {
        String name = etName.getText().toString();
        String desc = etDescription.getText().toString();
        if(InputValidatorHelper.isNullOrEmpty(name)){
          etName.requestFocus() ;
          etName.setError(getString(R.string.enter_name));
          return;
        }
        if(InputValidatorHelper.isNullOrEmpty(desc)){
            etDescription.requestFocus() ;
            etDescription.setError(getString(R.string.enter_desc));
            return;
        }

        if(GalleryList.size()==0){
            Toast.makeText(getContext(),getString(R.string.uplaod_image),Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint geoPoint = new GeoPoint(lat,lng);
        progressBar.setVisibility(View.VISIBLE);
        btnAdd.setEnabled(false);
       mViewModel.addRestaurant(name,desc,geoPoint,GalleryList);
    }
}
