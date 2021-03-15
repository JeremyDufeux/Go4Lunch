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
    private final RequestManager mGlide;
    private final OnPlaceListener mPlaceListener;
    private final List<Place> mPlaceList;
    private final CompositeDisposable mDisposable;
    private final Observable<Location> mObservableLocation;
    private final Location mLocation;

    public ListViewPlacesAdapter(Context context, RequestManager glide, Location location, Observable<Location> observableLocation, OnPlaceListener placeListener) {
        mContext = context;
        mGlide = glide;
        mPlaceListener = placeListener;
        mObservableLocation = observableLocation;
        mPlaceList = new ArrayList<>();
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
        holder.updateViewHolder(mContext, mGlide, mPlaceList.get(position), mPlaceListener);

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

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mDisposable.clear();
    }

    static class PlacesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final AdapterPlaceItemBinding mBinding;
        private Place mPlace;
        private Location mLocation;
        OnPlaceListener mPlaceListener;

        public PlacesViewHolder(@NonNull AdapterPlaceItemBinding itemBinding, Location location) {
            super(itemBinding.getRoot());
            mBinding = itemBinding;
            mLocation = location;
        }

        public void updateViewHolder(Context context, RequestManager glide, Place place, OnPlaceListener placeListener){
            mPlace = place;
            mPlaceListener = placeListener;
            mBinding.placeItemNameTv.setText(mPlace.getName());
            mBinding.placeItemTypeAndAddressTv.setText(mPlace.getAddress());

            glide.load(place.getPhotoUrl())
                    .centerCrop()
                    .into(mBinding.placeItemPictureIv);

            if(mPlace.isOpeningHoursAvailable()) {
                if (mPlace.isOpenNow()) {
                    String closingSoonTime = mPlace.getClosingSoonTime();
                    if (!closingSoonTime.isEmpty()) {
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
                String distance = (int) mLocation.distanceTo(mPlace.getLocation()) + "m";
                mBinding.placeItemDistanceTv.setText(distance);
            } else {
                mBinding.placeItemDistanceTv.setVisibility(View.INVISIBLE);
            }

            // TODO Display the amount of workmates who liked the place

            if(mPlace.getWorkmatesInterested()>0){
                mBinding.placeItemWorkmateIv.setVisibility(View.VISIBLE);
                String workmatesInterested = "(" + mPlace.getWorkmatesInterested() + ")";
                mBinding.placeItemWorkmateAmountTv.setText(workmatesInterested);
            }else {
                mBinding.placeItemWorkmateIv.setVisibility(View.INVISIBLE);
            }

            if(mPlace.getRating() > 0) {
                mBinding.placeItemStar1Iv.setVisibility(View.VISIBLE);
            } else {
                mBinding.placeItemStar1Iv.setVisibility(View.INVISIBLE);
            }
            if(mPlace.getRating() > 1.66) {
                mBinding.placeItemStar2Iv.setVisibility(View.VISIBLE);
            } else {
                mBinding.placeItemStar2Iv.setVisibility(View.INVISIBLE);
            }
            if(mPlace.getRating() > 3.33) {
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

    public interface OnPlaceListener{
        void onPlaceClick(int position);
    }
}
