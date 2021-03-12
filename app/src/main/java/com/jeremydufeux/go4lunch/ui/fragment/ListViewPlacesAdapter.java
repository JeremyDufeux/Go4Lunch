package com.jeremydufeux.go4lunch.ui.fragment;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jeremydufeux.go4lunch.BuildConfig;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewPlaceItemBinding;
import com.jeremydufeux.go4lunch.models.Place;

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
    private RequestManager mGlide;
    private final List<Place> mPlaceList;
    private final CompositeDisposable mDisposable;
    private final Observable<Location> mObservableLocation;
    private final Location mLocation;

    public ListViewPlacesAdapter(Context context, RequestManager glide, Location location, Observable<Location> observableLocation) {
        mContext = context;
        mGlide = glide;
        mObservableLocation = observableLocation;
        mPlaceList = new ArrayList<>();
        mDisposable = new CompositeDisposable();
        mLocation = location;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentListViewPlaceItemBinding mBinding = FragmentListViewPlaceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        PlacesViewHolder viewHolder = new PlacesViewHolder(mBinding, mLocation);
        mDisposable.add(viewHolder.setPositionObservable(mObservableLocation));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        holder.updateViewHolder(mContext, mGlide, mPlaceList.get(position));
    }

    @Override
    public int getItemCount() {
        return mPlaceList.size();
    }

    public void updateList(List<Place> places) {
        mPlaceList.clear();
        mPlaceList.addAll(places);
        notifyDataSetChanged();
    }


    static class PlacesViewHolder extends RecyclerView.ViewHolder {
        private final FragmentListViewPlaceItemBinding mBinding;
        private Place mPlace;
        private Location mLocation;

        public PlacesViewHolder(@NonNull FragmentListViewPlaceItemBinding itemBinding, Location location) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
            mLocation = location;
        }

        public void updateViewHolder(Context context, RequestManager glide, Place place){
            mPlace = place;
            mBinding.placeItemNameTv.setText(mPlace.getName());
            mBinding.placeItemTypeAndAddressTv.setText(mPlace.getAddress());

            if(!place.getPhotoUrl().isEmpty()) {
                glide.load(place.getPhotoUrl())
                        .centerCrop()
                        .into(mBinding.placeItemPictureIv);
            }

            // TODO Display "Open until" depending on time
            if(mPlace.getOpeningHours() != null) {
                if (!mPlace.getOpeningHours().getOpenNow()) {
                    mBinding.placeItemOpenTv.setText(R.string.closed);
                    mBinding.placeItemOpenTv.setTextColor(context.getResources().getColor(R.color.red));
                } else {
                    mBinding.placeItemOpenTv.setText(R.string.open_now);
                    mBinding.placeItemOpenTv.setTextColor(context.getResources().getColor(R.color.grey));
                }
            }

            if (mLocation != null) {
                String distance = (int) mLocation.distanceTo(mPlace.getLocation()) + "m";
                mBinding.placeItemDistanceTv.setText(distance);
            }

            if(mPlace.getWorkmatesInterested()>0){
                mBinding.placeItemWorkmateIv.setVisibility(View.VISIBLE);
                String workmatesInterested = "(" + mPlace.getWorkmatesInterested() + ")";
                mBinding.placeItemWorkmateAmountTv.setText(workmatesInterested);
            }

            if (mPlace.getRating() > 0) {
                mBinding.placeItemStar1Iv.setVisibility(View.VISIBLE);
            }
            if (mPlace.getRating() > 1.66) {
                mBinding.placeItemStar2Iv.setVisibility(View.VISIBLE);
            }
            if (mPlace.getRating() > 3.33) {
                mBinding.placeItemStar3Iv.setVisibility(View.VISIBLE);
            }
        }

        Disposable setPositionObservable(Observable<Location> location){
            return location.debounce(1000, TimeUnit.MILLISECONDS)
                    .distinctUntilChanged()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Location>() {
                        @Override
                        public void onNext(@NonNull Location location) {
                            String distance = (int) location.distanceTo(mPlace.getLocation()) + "m";
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
}
