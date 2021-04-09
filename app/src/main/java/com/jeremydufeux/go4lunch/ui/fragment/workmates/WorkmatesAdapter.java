package com.jeremydufeux.go4lunch.ui.fragment.workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentWorkmateItemBinding;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.ui.fragment.listView.ListViewPlacesAdapter;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmateViewHolder>{

    private final OnWorkmateListener mWorkmateListener;
    private final AsyncListDiffer<Workmate> mWorkmateList;

    public WorkmatesAdapter(OnWorkmateListener workmateListener) {
        mWorkmateListener = workmateListener;
        mWorkmateList = new AsyncListDiffer<>(this, new DifferCallback());
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentWorkmateItemBinding binding = FragmentWorkmateItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkmateViewHolder(binding, mWorkmateListener);
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

    static class WorkmateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final FragmentWorkmateItemBinding mBinding;
        OnWorkmateListener mWorkmateListener;

        public WorkmateViewHolder(@NonNull FragmentWorkmateItemBinding itemBinding, OnWorkmateListener onWorkmateListener) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
            mWorkmateListener = onWorkmateListener;
        }

        public void updateViewHolder(Workmate workmate){
            Context context = mBinding.getRoot().getContext();

            String workmateChosen = context.getResources().getString(
                    R.string.workmate_is_eating_at_restaurant,
                    workmate.getNickname(),
                    workmate.getChosenRestaurantName());

            String workmateNotChosen = context.getResources().getString(
                    R.string.workmate_has_not_decided_yet,
                    workmate.getNickname());

            mBinding.workmateItemChosenTv.setText(workmateChosen);
            mBinding.workmateItemChosenTv.setVisibility(workmate.getWorkmateChosenTvVisibility());
            mBinding.workmateItemNotChosenTv.setText(workmateNotChosen);
            mBinding.workmateItemNotChosenTv.setVisibility(workmate.getWorkmateNotChosenTvVisibility());

            Glide.with(context).load(workmate.getPictureUrl())
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mBinding.workmateItemPictureIv);

            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mWorkmateListener.onWorkmateClick(getAdapterPosition());
        }

    }

    public interface OnWorkmateListener {
        void onWorkmateClick(int position);
    }

    public static class DifferCallback extends DiffUtil.ItemCallback<Workmate> {
        public boolean areItemsTheSame(Workmate oldItem, Workmate newItem) {
            return oldItem.getUId().equals(newItem.getUId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Workmate oldItem, @NonNull Workmate newItem) {
            return oldItem.getNickname().equals(newItem.getNickname())
                    && oldItem.getChosenRestaurantName().equals(newItem.getChosenRestaurantName())
                    && oldItem.getPictureUrl().equals(newItem.getPictureUrl());
        }
    }
}
