package com.jeremydufeux.go4lunch.ui.fragment.listView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewPlaceItemBinding;
import com.jeremydufeux.go4lunch.models.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class ListViewPlacesAdapter extends RecyclerView.Adapter<ListViewPlacesAdapter.PlacesViewHolder> {

    private final OnPlaceListener mPlaceListener;
    private final List<Restaurant> mRestaurantList;

    public ListViewPlacesAdapter(OnPlaceListener placeListener) {
        mPlaceListener = placeListener;
        mRestaurantList = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentListViewPlaceItemBinding binding = FragmentListViewPlaceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlacesViewHolder(binding, mPlaceListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        holder.updateViewHolder(mRestaurantList.get(position));
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public void updateList(List<Restaurant> restaurants) {
        mRestaurantList.clear();
        mRestaurantList.addAll(restaurants);
        notifyDataSetChanged();
    }

    static class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final FragmentListViewPlaceItemBinding mBinding;
        OnPlaceListener mPlaceListener;

        public PlacesViewHolder(@NonNull FragmentListViewPlaceItemBinding itemBinding, OnPlaceListener placeListener) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
            mPlaceListener = placeListener;
        }

        public void updateViewHolder(Restaurant restaurant){
            Context context = mBinding.getRoot().getContext();
            mBinding.placeItemNameTv.setText(restaurant.getName());
            mBinding.placeItemTypeAndAddressTv.setText(restaurant.getAddress());

            Glide.with(context).load(restaurant.getPhotoUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mBinding.placeItemPictureIv);

            String openString = context.getResources().getString(restaurant.getOpenTvString(), restaurant.getOpenTvCloseTimeString());
            mBinding.placeItemOpenTv.setText(openString);
            mBinding.placeItemOpenTv.setTextColor(context.getResources().getColor(restaurant.getOpenTvColor()));
            mBinding.placeItemOpenTv.setVisibility(restaurant.getOpenTvVisibility());

            mBinding.placeItemDistanceTv.setVisibility(restaurant.getDistanceTvVisibility());
            mBinding.placeItemDistanceTv.setText(context.getResources().getString(R.string.user_distance, restaurant.getDistanceFromUser()));

            mBinding.placeItemWorkmateIv.setVisibility(restaurant.getWorkmateIvVisibility());
            mBinding.placeItemWorkmateAmountTv.setVisibility(restaurant.getWorkmateTvVisibility());
            mBinding.placeItemWorkmateAmountTv.setText(context.getString(R.string.workmates_interested_amount, restaurant.getInterestedWorkmates().size()));

            mBinding.placeItemStar1Iv.setVisibility(restaurant.getStar1IvVisibility());
            mBinding.placeItemStar2Iv.setVisibility(restaurant.getStar2IvVisibility());
            mBinding.placeItemStar3Iv.setVisibility(restaurant.getStar3IvVisibility());

            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mPlaceListener.onPlaceClick(getAdapterPosition());
        }

    }

    public interface OnPlaceListener{
        void onPlaceClick(int position);
    }
}
