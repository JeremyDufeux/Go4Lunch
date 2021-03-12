package com.jeremydufeux.go4lunch.ui.fragment;

import android.content.Context;
import android.location.Location;
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
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListViewPlacesAdapter extends RecyclerView.Adapter<ListViewPlacesAdapter.PlacesViewHolder> {

    Context mContext;
    List<Place> mPlaceList;
    CompositeDisposable mDisposable;
    Observable<Location> mObservableLocation;
    Location mLocation;

    public ListViewPlacesAdapter(Context context, Observable<Location> observableLocation, Location location) {
        mContext = context;
        mObservableLocation = observableLocation;
        mPlaceList = new ArrayList<>();
        mDisposable = new CompositeDisposable();
        mLocation = location;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentListViewPlaceItemBinding mBinding = FragmentListViewPlaceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        PlacesViewHolder viewHolder = new PlacesViewHolder(mBinding, mContext, mLocation);
        mDisposable.add(viewHolder.setPositionObservable(mObservableLocation));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        holder.updateViewHolder(mPlaceList.get(position));
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
        FragmentListViewPlaceItemBinding mBinding;
        Context mContext;
        Place mPlace;
        Location mLocation;

        public PlacesViewHolder(@NonNull FragmentListViewPlaceItemBinding itemBinding, Context context, Location location) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
            mContext = context;
            mLocation = location;
        }

        public void updateViewHolder(Place place){
            mPlace = place;
            mBinding.placeItemNameTv.setText(mPlace.getName());
            mBinding.placeItemTypeAndAddressTv.setText(mPlace.getAddress());

            // TODO Load place photo

            // TODO Display "Open until" depending on time
            if(mPlace.getOpeningHours() != null) {
                if (!mPlace.getOpeningHours().getOpenNow()) {
                    mBinding.placeItemOpenTv.setText(R.string.closed);
                    mBinding.placeItemOpenTv.setTextColor(mContext.getResources().getColor(R.color.red));
                } else {
                    mBinding.placeItemOpenTv.setText(R.string.open_now);
                    mBinding.placeItemOpenTv.setTextColor(mContext.getResources().getColor(R.color.grey));
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
