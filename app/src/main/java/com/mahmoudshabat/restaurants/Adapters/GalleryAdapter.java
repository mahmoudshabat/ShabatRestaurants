package com.mahmoudshabat.restaurants.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.mahmoudshabat.restaurants.R;
import com.mahmoudshabat.restaurants.model.ImageModel;
import com.mahmoudshabat.restaurants.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ADD_VIEW = 1;
    List<ImageModel> imagesList = new ArrayList<>();
    private OnItemClickListener listener;
    private OnAddClickListener addListener;
    private OnRemoveClickListener removeListener;
    Context mContext ;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        mContext = parent.getContext() ;
        if (viewType == ADD_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_add_image, parent, false);
            AddViewHolder vh = new AddViewHolder(v);
            return vh;
        }

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);

        ImagesHolder vh = new ImagesHolder(v);

        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ImagesHolder) {
                ImagesHolder vh = (ImagesHolder) holder;

                vh.bindView(position-1);
            } else if (holder instanceof AddViewHolder) {
                AddViewHolder vh = (AddViewHolder) holder;
                vh.bindView(position-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public int getItemViewType(int position) {

        if (position == 0){
            // This is where we'll add header.
            return ADD_VIEW;
        }

        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {


        if (imagesList.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }


        return imagesList.size()+1;
    }


    public void setImagesList(List<ImageModel> mImages) {
        this.imagesList = mImages;
        notifyDataSetChanged();
    }

    public void removeImage(int position) {
        imagesList.remove(position-1);
        notifyItemRemoved(position);

    }



    public class ImagesHolder extends RecyclerView.ViewHolder {

        ImageView ivImage,icDelete ;
        public ImagesHolder(View itemView) {
            super(itemView);


            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            icDelete = (ImageView) itemView.findViewById(R.id.icDelete);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(imagesList.get(position-1), position);
                }
            });

            icDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (removeListener != null && position != RecyclerView.NO_POSITION) {
                    removeListener.onRemoveClick(position);
                }
            });


        }
        public void bindView(int position) {
            Glide.with(mContext)
                    .load(imagesList.get(position).getUrl())
                    .transform(new RoundedCorners(Utils.dpToPx(mContext,8)))
                    .into(ivImage);
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> addListener.onAddClick());
        }

        public void bindView(int position) {

        }
    }
    public interface OnItemClickListener {
        void onItemClick(ImageModel imageModel, int position);
    }
    public interface OnAddClickListener {
        void onAddClick();
    }

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public void setOnAddClickListener(OnAddClickListener listener) {
        this.addListener = listener;
    }
    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        this.removeListener = listener;
    }
}
