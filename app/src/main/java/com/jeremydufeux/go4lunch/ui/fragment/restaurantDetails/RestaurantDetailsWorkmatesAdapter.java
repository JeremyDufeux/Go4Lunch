package com.jeremydufeux.go4lunch.ui.fragment.restaurantDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentRestaurantDetailsWorkmateItemBinding;
import com.jeremydufeux.go4lunch.models.Workmate;

import java.util.List;

public class RestaurantDetailsWorkmatesAdapter extends RecyclerView.Adapter<RestaurantDetailsWorkmatesAdapter.WorkmateViewHolder> {

    private final AsyncListDiffer<Workmate> mWorkmateList;

    public RestaurantDetailsWorkmatesAdapter() {
        mWorkmateList = new AsyncListDiffer<>(this, new DifferCallback());
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentRestaurantDetailsWorkmateItemBinding binding = FragmentRestaurantDetailsWorkmateItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkmateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        holder.updateViewHolder(mWorkmateList.getCurrentList().get(position));
    }

    @Override
    public int getItemCount() {
        return mWorkmateList.getCurrentList().size();
    }

    public void updateList(List<Workmate> workmates) {
        mWorkmateList.submitList(workmates);
    }

    static class WorkmateViewHolder extends RecyclerView.ViewHolder {
        private final FragmentRestaurantDetailsWorkmateItemBinding mBinding;

        public WorkmateViewHolder(@NonNull FragmentRestaurantDetailsWorkmateItemBinding itemBinding) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
        }

        public void updateViewHolder(Workmate workmate){
            Context context = mBinding.getRoot().getContext();

            mBinding.workmateItemNameTv.setText(context.getResources().getString(R.string.workmate_is_joining, workmate.getNickname()));

            Glide.with(context)
                    .load(workmate.getPictureUrl())
                    .error(R.drawable.ic_default_workmate_picture)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mBinding.workmateItemPictureIv);
        }
    }

    public static class DifferCallback extends DiffUtil.ItemCallback<Workmate> {
        public boolean areItemsTheSame(Workmate oldItem, Workmate newItem) {
            return oldItem.getUId().equals(newItem.getUId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Workmate oldItem, @NonNull Workmate newItem) {
            return oldItem.getChosenRestaurantId().equals(newItem.getChosenRestaurantId());
        }
    }
}
