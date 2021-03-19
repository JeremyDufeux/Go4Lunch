package com.jeremydufeux.go4lunch.ui.fragment;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.AdapterPlaceItemBinding;
import com.jeremydufeux.go4lunch.models.Restaurant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListViewPlacesAdapter extends RecyclerView.Adapter<ListViewPlacesAdapter.PlacesViewHolder> {

    private final Context mContext;
    private final RequestManager mGlide;
    private final OnPlaceListener mPlaceListener;
    private final List<Restaurant> mRestaurantList;
    private final CompositeDisposable mDisposable;
    private final Observable<Location> mObservableLocation;
    private final Location mLocation;

    public ListViewPlacesAdapter(Context context, RequestManager glide, Location location, Observable<Location> observableLocation, OnPlaceListener placeListener) {
        mContext = context;
        mGlide = glide;
        mPlaceListener = placeListener;
        mObservableLocation = observableLocation;
        mRestaurantList = new ArrayList<>();
        mDisposable = new CompositeDisposable();
        mLocation = location;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterPlaceItemBinding binding = AdapterPlaceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        PlacesViewHolder viewHolder = new PlacesViewHolder(binding, mLocation);
        mDisposable.add(viewHolder.setPositionObservable(mObservableLocation));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        holder.updateViewHolder(mContext, mGlide, mRestaurantList.get(position), mPlaceListener);

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

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mDisposable.clear();
    }

    static class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AdapterPlaceItemBinding mBinding;
        private Restaurant mRestaurant;
        private Location mLocation;
        OnPlaceListener mPlaceListener;

        public PlacesViewHolder(@NonNull AdapterPlaceItemBinding itemBinding, Location location) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
            mLocation = location;
        }

        public void updateViewHolder(Context context, RequestManager glide, Restaurant restaurant, OnPlaceListener placeListener){
            mRestaurant = restaurant;
            mPlaceListener = placeListener;
            mBinding.placeItemNameTv.setText(mRestaurant.getName());
            mBinding.placeItemTypeAndAddressTv.setText(mRestaurant.getAddress());

            glide.load(restaurant.getPhotoUrl())
                    .centerCrop()
                    .into(mBinding.placeItemPictureIv);

            // TODO Move in viewmodel and get a string
            if(mRestaurant.isOpeningHoursAvailable()) {
                if (mRestaurant.isOpenNow()) {
                    String closingSoonTime = mRestaurant.getClosingSoonTime();
                    if (!closingSoonTime.isEmpty() && !mRestaurant.isAlwaysOpen()) {
                        String openUntil = context.getString(R.string.open_until) + " " + closingSoonTime;
                        mBinding.placeItemOpenTv.setText(openUntil);
                    } else {
                        mBinding.placeItemOpenTv.setText(R.string.open_now);
                    }
                    mBinding.placeItemOpenTv.setTextColor(context.getResources().getColor(R.color.grey));
                } else {
                    mBinding.placeItemOpenTv.setText(R.string.closed);
                    mBinding.placeItemOpenTv.setTextColor(context.getResources().getColor(R.color.red));
                }
                mBinding.placeItemOpenTv.setVisibility(View.VISIBLE);
            } else {
                mBinding.placeItemOpenTv.setVisibility(View.INVISIBLE);
            }

            if(mLocation != null) {
                mBinding.placeItemDistanceTv.setVisibility(View.VISIBLE);
                String distance = (int) mLocation.distanceTo(mRestaurant.getLocation()) + "m";
                mBinding.placeItemDistanceTv.setText(distance);
            } else {
                mBinding.placeItemDistanceTv.setVisibility(View.INVISIBLE);
            }

            // TODO Display the amount of workmates who liked the place

            if(mRestaurant.getWorkmatesInterested()>0){
                mBinding.placeItemWorkmateIv.setVisibility(View.VISIBLE);
                String workmatesInterested = "(" + mRestaurant.getWorkmatesInterested() + ")";
                mBinding.placeItemWorkmateAmountTv.setText(workmatesInterested);
            }else {
                mBinding.placeItemWorkmateIv.setVisibility(View.INVISIBLE);
            }

            if(mRestaurant.getRating() > 0) {
                mBinding.placeItemStar1Iv.setVisibility(View.VISIBLE);
            } else {
                mBinding.placeItemStar1Iv.setVisibility(View.INVISIBLE);
            }
            if(mRestaurant.getRating() > 1.66) {
                mBinding.placeItemStar2Iv.setVisibility(View.VISIBLE);
            } else {
                mBinding.placeItemStar2Iv.setVisibility(View.INVISIBLE);
            }
            if(mRestaurant.getRating() > 3.33) {
                mBinding.placeItemStar3Iv.setVisibility(View.VISIBLE);
            } else {
                mBinding.placeItemStar3Iv.setVisibility(View.INVISIBLE);
            }

            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mPlaceListener.onPlaceClick(getAdapterPosition());
        }

        Disposable setPositionObservable(Observable<Location> location){
            return location.debounce(1000, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Location>() {
                        @Override
                        public void onNext(@NonNull Location location) {
                            String distance = (int) location.distanceTo(mRestaurant.getLocation()) + "m";
                            mBinding.placeItemDistanceTv.setText(distance);
                            mLocation = location;
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                        }
                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }

    public interface OnPlaceListener{
        void onPlaceClick(int position);
    }
}
