package com.jeremydufeux.go4lunch.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.jeremydufeux.go4lunch.BaseFragment;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewBinding;
import com.jeremydufeux.go4lunch.databinding.FragmentRestaurantDetailsBinding;
import com.jeremydufeux.go4lunch.injection.Injection;
import com.jeremydufeux.go4lunch.injection.ViewModelFactory;
import com.jeremydufeux.go4lunch.models.Place;
import com.jeremydufeux.go4lunch.ui.SharedViewModel;

import org.jetbrains.annotations.NotNull;

public class RestaurantDetailsFragment extends BaseFragment {

    private SharedViewModel mSharedViewModel;
    private FragmentRestaurantDetailsBinding mBinding;
    private RestaurantDetailsWorkmatesAdapter mAdapter;

    private Place mPlace;

    public RestaurantDetailsFragment() {}

    public static RestaurantDetailsFragment newInstance() {
        return new RestaurantDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mSharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentRestaurantDetailsBinding.inflate(getLayoutInflater());

        assert getArguments() != null;
        String placeId = RestaurantDetailsFragmentArgs.fromBundle(getArguments()).getPlaceId();

        mPlace = mSharedViewModel.getPlaceWithId(placeId);

        configureRecyclerView();
        updateView();

        return mBinding.getRoot();
    }

    private void configureRecyclerView() {
        mAdapter = new RestaurantDetailsWorkmatesAdapter(Glide.with(this));
        mBinding.fragmentRestaurantDetailsWorkmatesRv.setAdapter(mAdapter);
        mBinding.fragmentRestaurantDetailsWorkmatesRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void updateView() {
        mBinding.fragmentRestaurantDetailsNameTv.setText(mPlace.getName());
        mBinding.fragmentRestaurantDetailsAddressTv.setText(mPlace.getAddress());

        Glide.with(this)
                .load(mPlace.getPhotoUrl())
                .centerCrop()
                .into(mBinding.fragmentRestaurantDetailsPhotoIv);

        if (mPlace.getRating() > 0) {
            mBinding.fragmentRestaurantDetailsStar1Iv.setVisibility(View.VISIBLE);
        } else {
            mBinding.fragmentRestaurantDetailsStar1Iv.setVisibility(View.INVISIBLE);
        }
        if (mPlace.getRating() > 1.66) {
            mBinding.fragmentRestaurantDetailsStar2Iv.setVisibility(View.VISIBLE);
        } else {
            mBinding.fragmentRestaurantDetailsStar2Iv.setVisibility(View.INVISIBLE);
        }
        if (mPlace.getRating() > 3.33) {
            mBinding.fragmentRestaurantDetailsStar3Iv.setVisibility(View.VISIBLE);
        } else {
            mBinding.fragmentRestaurantDetailsStar3Iv.setVisibility(View.INVISIBLE);
        }

        // TODO Check if user is going to this place for lunch
        mBinding.fragmentRestaurantDetailsGoFab.setOnClickListener(v -> choseRestaurant());

        if(mPlace.getPhoneNumber() != null && !mPlace.getPhoneNumber().isEmpty()) {
            mBinding.fragmentRestaurantDetailsCallIv.setOnClickListener(v -> callRestaurant());
        } else {
            mBinding.fragmentRestaurantDetailsCallLl.setVisibility(View.GONE);
        }

        // TODO Check if user as liked the place
        mBinding.fragmentRestaurantDetailsLikeIv.setOnClickListener(v -> likeRestaurant());

        if(mPlace.getWebsite() != null && !mPlace.getWebsite().isEmpty()) {
            mBinding.fragmentRestaurantDetailsWebsiteIv.setOnClickListener(v -> visitRestaurantWebsite());
        } else {
            mBinding.fragmentRestaurantDetailsWebLl.setVisibility(View.GONE);
        }
    }

    private void choseRestaurant() {
        // TODO save to Firebase
    }

    private void callRestaurant() {
        // TODO open phone Dialer
    }

    private void likeRestaurant() {
        // TODO save to Firebase
    }

    private void visitRestaurantWebsite() {
        // TODO open browser
    }
}