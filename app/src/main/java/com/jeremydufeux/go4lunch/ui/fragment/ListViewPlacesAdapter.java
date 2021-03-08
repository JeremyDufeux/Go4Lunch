package com.jeremydufeux.go4lunch.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewPlaceItemBinding;
import com.jeremydufeux.go4lunch.models.GooglePlaceResult.Result;
import com.jeremydufeux.go4lunch.models.Place;

import java.util.ArrayList;
import java.util.List;

public class ListViewPlacesAdapter extends RecyclerView.Adapter<ListViewPlacesAdapter.PlacesViewHolder> {

    List<Place> mPlaceList;

    public ListViewPlacesAdapter() {
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
        if(place.getOpeningHours() != null) {
            if (place.getOpeningHours().getOpenNow()) {
                holder.mBinding.placeItemOpenTv.setText(R.string.open_now);
            } else {
                holder.mBinding.placeItemOpenTv.setText(R.string.closed);
            }
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
