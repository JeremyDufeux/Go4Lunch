package com.jeremydufeux.go4lunch.ui.fragment.workmates;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentWorkmateItemBinding;
import com.jeremydufeux.go4lunch.models.Workmate;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.WorkmateViewHolder>{

    private final RequestManager mGlide;
    private final List<Workmate> mWorkmateList = new ArrayList<>();

    public WorkmatesAdapter(RequestManager glide) {
        mGlide = glide;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentWorkmateItemBinding binding = FragmentWorkmateItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
        private final FragmentWorkmateItemBinding mBinding;

        public WorkmateViewHolder(@NonNull FragmentWorkmateItemBinding itemBinding) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
        }

        public void updateViewHolder(RequestManager glide, Workmate workmate){
            Context context = mBinding.getRoot().getContext();

            String workmateChosen = context.getResources().getString(
                    R.string.workmate_is_eating_at_restaurant,
                    workmate.getFirstName(),
                    workmate.getChosenRestaurantName());

            String workmateNotChosen = context.getResources().getString(
                    R.string.workmate_has_not_decided_yet,
                    workmate.getFirstName());

            mBinding.workmateItemChosenTv.setText(workmateChosen);
            mBinding.workmateItemChosenTv.setVisibility(workmate.getWorkmateChosenTvVisibility());
            mBinding.workmateItemNotChosenTv.setText(workmateNotChosen);
            mBinding.workmateItemNotChosenTv.setVisibility(workmate.getWorkmateNotChosenTvVisibility());

            glide.load(workmate.getPictureUrl())
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mBinding.workmateItemPictureIv);

        }
    }
}
