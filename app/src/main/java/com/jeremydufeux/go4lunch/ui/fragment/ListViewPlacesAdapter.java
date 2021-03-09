package com.jeremydufeux.go4lunch.ui.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewPlaceItemBinding;
import com.jeremydufeux.go4lunch.models.Place;

import java.util.ArrayList;
import java.util.List;

public class ListViewPlacesAdapter extends RecyclerView.Adapter<ListViewPlacesAdapter.PlacesViewHolder> {

    Context mContext;
    List<Place> mPlaceList;

    public ListViewPlacesAdapter(Context context) {
        mContext = context;
        mPlaceList = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentListViewPlaceItemBinding mBinding = FragmentListViewPlaceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlacesViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        Place place = mPlaceList.get(position);

        holder.mBinding.placeItemNameTv.setText(place.getName());
        holder.mBinding.placeItemTypeAndAddressTv.setText(place.getAddress());

        if(place.getOpeningHours() != null) {
            if (!place.getOpeningHours().getOpenNow()) {
                holder.mBinding.placeItemOpenTv.setText(R.string.closed);
                holder.mBinding.placeItemOpenTv.setTextColor(mContext.getResources().getColor(R.color.red));
            } else {
                holder.mBinding.placeItemOpenTv.setText(R.string.open_now);
                holder.mBinding.placeItemOpenTv.setTextColor(mContext.getResources().getColor(R.color.grey));
            }
        }
        String distance = place.getDistanceFromUser() + "m";
        holder.mBinding.placeItemDistanceTv.setText(distance);

        if(place.getWorkmatesInterested()>0){
            holder.mBinding.placeItemWorkmateIv.setVisibility(View.VISIBLE);
            String workmatesInterested = "(" + place.getWorkmatesInterested() + ")";
            holder.mBinding.placeItemWorkmateAmountTv.setText(workmatesInterested);
        }

        if (place.getRating() > 1.66) {
            holder.mBinding.placeItemStar2Iv.setVisibility(View.VISIBLE);
        }
        if (place.getRating() > 3.33) {
            holder.mBinding.placeItemStar3Iv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }

    public void updateList(List<Place> places) {
        mPlaceList = places;
        notifyDataSetChanged();
    }

    static class PlacesViewHolder extends RecyclerView.ViewHolder {
        FragmentListViewPlaceItemBinding mBinding;
        public PlacesViewHolder(@NonNull FragmentListViewPlaceItemBinding itemBinding) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
        }
    }
}
