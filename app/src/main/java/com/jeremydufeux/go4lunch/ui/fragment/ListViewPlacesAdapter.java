package com.jeremydufeux.go4lunch.ui.fragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jeremydufeux.go4lunch.databinding.FragmentListViewPlaceItemBinding;
import com.jeremydufeux.go4lunch.models.GooglePlaceResult.Result;

import java.util.ArrayList;
import java.util.List;

public class ListViewPlacesAdapter extends RecyclerView.Adapter<ListViewPlacesAdapter.PlacesViewHolder> {

    List<Result> mResultList;

    public ListViewPlacesAdapter() {
        mResultList = new ArrayList<>();
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentListViewPlaceItemBinding mBinding = FragmentListViewPlaceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PlacesViewHolder(mBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        Result result = mResultList.get(position);
        holder.mBinding.placeItemNameTv.setText(result.getName());
        if(result.getOpeningHours() != null){
            holder.mBinding.placeItemOpenTv.setText(result.getOpeningHours().getOpenNow().toString());
        }
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    public void updateList(List<Result> results) {
        mResultList = results;
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
