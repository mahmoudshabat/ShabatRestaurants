package com.mahmoudshabat.restaurants.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mahmoudshabat.restaurants.R;
import com.mahmoudshabat.restaurants.Adapters.SliderImageAdapter;
import com.mahmoudshabat.restaurants.model.RestaurantsModel;
import com.mahmoudshabat.restaurants.utils.CustomProgressbar;
import com.mahmoudshabat.restaurants.utils.Utils;
import com.mahmoudshabat.restaurants.viewModel.RestaurantDetailsViewModel;
import com.smarteist.autoimageslider.SliderView;

import static com.mahmoudshabat.restaurants.utils.Constant.DYNAMIC_LINKS_PREFIX;

public class RestaurantDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvTitle ,tvName ,tvDescription ;
    private ImageView icArrowBack ,icShare ;
    SliderView imageSlider ;
    SliderImageAdapter adapterImage ;
    RestaurantsModel restaurant ;
    RestaurantDetailsViewModel mViewModel ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        mViewModel = ViewModelProviders.of(this).get(RestaurantDetailsViewModel.class);
        initView();
        if(getIntent().getExtras()!=null &&getIntent().getExtras().containsKey("restaurant")){
            restaurant = RestaurantDetailsActivityArgs.fromBundle(getIntent().getExtras()).getRestaurant();
            setData();
        }
        checkDynamicLink() ;
    }

    private void setData() {
        tvTitle.setText(restaurant.getName());
        tvName.setText(restaurant.getName());
        tvDescription.setText(restaurant.getDescription());
        adapterImage.setImagesList(restaurant.getImages());
    }

    private void initView() {
        icArrowBack =  findViewById(R.id.icArrowBack);
        icShare =  findViewById(R.id.icShare);
        tvTitle =  findViewById(R.id.tvTitle);
        tvName =  findViewById(R.id.tvName);
        tvDescription =  findViewById(R.id.tvDescription);
        imageSlider = findViewById(R.id.imageSlider);
        adapterImage = new SliderImageAdapter();
        imageSlider.setSliderAdapter(adapterImage);
        icArrowBack.setOnClickListener(this);
        icShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.icArrowBack :
                onBackPressed();
                break;
            case R.id.icShare :
                generateDynamicLink() ;
                break;

        }
    }

    private void observeRestaurant() {
        mViewModel.restaurant().observe(this, restaurantsModel -> {
            CustomProgressbar.hideProgressBar();
            restaurant = restaurantsModel ;
            setData();
        });
    }

    public void checkDynamicLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.getLink();
                        String restaurantId = deepLink.getQueryParameter("restaurant");
                        observeRestaurant() ;
                        CustomProgressbar.showProgressBar(this, false);
                        mViewModel.getRestaurant(restaurantId);
                    }
                });
    }


    public void generateDynamicLink() {
        if(restaurant==null){
            return;
        }
        CustomProgressbar.showProgressBar(this, true);
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.google.com/"))
                .setDomainUriPrefix(DYNAMIC_LINKS_PREFIX)
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(restaurant.getName())
                                .setDescription(restaurant.getDescription())
                                .build())
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        String dynamicLinkString = dynamicLink.getUri().toString()+"?restaurant="+restaurant.getId() ;
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(dynamicLinkString))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Uri shortLink = task.getResult().getShortLink();
                        String stringToShare =  String.format(getString(R.string.share_format),restaurant.getName(),shortLink.toString());
                        Utils.invokeShare(RestaurantDetailsActivity.this,stringToShare);
                        CustomProgressbar.hideProgressBar();
                    }
                });
    }
}
