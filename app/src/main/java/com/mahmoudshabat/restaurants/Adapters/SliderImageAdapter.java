package com.mahmoudshabat.restaurants.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mahmoudshabat.restaurants.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderImageAdapter extends SliderViewAdapter<SliderImageAdapter.SliderAdapterVH> {

    List<String> imageList = new ArrayList<>();
    public SliderImageAdapter() {

    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

        final String mImage = imageList.get(position);

        Glide.with(viewHolder.itemView)
                .load(mImage)
                .into(viewHolder.imageViewBackground);

    }


    @Override
    public int getCount() {
        return imageList.size();
    }


    public void setImagesList(List<String> mImages ) {
        this.imageList = mImages;
        notifyDataSetChanged();
    }
    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.ivImageSlider);
            this.itemView = itemView;
        }
    }
}
