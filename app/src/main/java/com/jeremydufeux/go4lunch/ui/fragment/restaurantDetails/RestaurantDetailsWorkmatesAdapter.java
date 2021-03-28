package com.jeremydufeux.go4lunch.ui.fragment.restaurantDetails;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.jeremydufeux.go4lunch.databinding.AdapterWorkmateItemBinding;
import com.jeremydufeux.go4lunch.models.Workmate;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsWorkmatesAdapter extends RecyclerView.Adapter<RestaurantDetailsWorkmatesAdapter.WorkmateViewHolder> {

    private final RequestManager mGlide;
    private final List<Workmate> mWorkmateList;

    public RestaurantDetailsWorkmatesAdapter(RequestManager glide) {
        mGlide = glide;
        mWorkmateList = new ArrayList<>();
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterWorkmateItemBinding binding = AdapterWorkmateItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkmateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        holder.updateViewHolder(mGlide, mWorkmateList.get(position));

    }

    @Override
    public int getItemCount() {
        return mWorkmateList.size();
    }

    public void updateList(List<Workmate> workmates) {
        mWorkmateList.clear();
        mWorkmateList.addAll(workmates);
        notifyDataSetChanged();
    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {
        private final AdapterWorkmateItemBinding mBinding;

        public WorkmateViewHolder(@NonNull AdapterWorkmateItemBinding itemBinding) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
        }

        public void updateViewHolder(RequestManager glide, Workmate workmate){
            mBinding.workmateItemNameTv.setText(workmate.getUserName());

            glide.load(workmate.getPictureUrl())
                    .centerCrop()
                    .into(mBinding.workmateItemPictureIv);

        }
    }
}
