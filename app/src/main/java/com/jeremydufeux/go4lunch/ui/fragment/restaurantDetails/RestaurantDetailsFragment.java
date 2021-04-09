package com.jeremydufeux.go4lunch.ui.fragment.restaurantDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.jeremydufeux.go4lunch.databinding.FragmentRestaurantDetailsBinding;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantDetailsFragment extends Fragment {

    private RestaurantDetailsViewModel mViewModel;
    private FragmentRestaurantDetailsBinding mBinding;
    private RestaurantDetailsWorkmatesAdapter mAdapter;

    private Restaurant mRestaurant;
    private Workmate mCurrentUser;

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
        mViewModel = new ViewModelProvider(requireActivity()).get(RestaurantDetailsViewModel.class);
        mViewModel.observeEvents().observe(this, onEventReceived());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentRestaurantDetailsBinding.inflate(getLayoutInflater());
        mViewModel.startObservers();

        assert getArguments() != null;
        String restaurantId = RestaurantDetailsFragmentArgs.fromBundle(getArguments()).getRestaurantId();

        mViewModel.initViewModel(restaurantId);
        mViewModel.observeRestaurant().observe(getViewLifecycleOwner(), observeRestaurant());
        mViewModel.observeWorkmates().observe(getViewLifecycleOwner(), observeWorkmates());
        mViewModel.observeCurrentUser().observe(getViewLifecycleOwner(), observeCurrentUser());

        configureListener();
        configureRecyclerView();

        return mBinding.getRoot();
    }

    private Observer<Restaurant> observeRestaurant(){
        return restaurant -> {
            mRestaurant = restaurant;
            updateViewRestaurant();
        };
    }

    private Observer<List<Workmate>> observeWorkmates(){
        return workmateList -> mAdapter.updateList(workmateList);
    }

    private Observer<Workmate> observeCurrentUser(){
        return workmate -> {
            mCurrentUser = workmate;
            updateViewWithWorkmate();
        };
    }

    private Observer<LiveEvent> onEventReceived(){
        return event -> {
            if(event instanceof ShowSnackbarLiveEvent){
                showSnackBar(((ShowSnackbarLiveEvent) event).getStingId());
            }
        };
    }

    private void configureRecyclerView() {
        mAdapter = new RestaurantDetailsWorkmatesAdapter(Glide.with(this));
        mAdapter.updateList(new ArrayList<>());
        mBinding.fragmentRestaurantDetailsWorkmatesRv.setAdapter(mAdapter);
        mBinding.fragmentRestaurantDetailsWorkmatesRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void configureListener(){
        mBinding.fragmentRestaurantDetailsGoFab.setOnClickListener(v -> choseRestaurant());
        mBinding.fragmentRestaurantDetailsLikeLl.setOnClickListener(v -> likeRestaurant());

        mBinding.fragmentRestaurantDetailsCallLl.setOnClickListener(v -> callRestaurant());
        mBinding.fragmentRestaurantDetailsWebsiteLl.setOnClickListener(v -> visitRestaurantWebsite());
    }

    private void updateViewWithWorkmate() {
        mBinding.fragmentRestaurantDetailsGoFab.setColorFilter(getResources().getColor(mCurrentUser.getWorkmateGoFabColor()));
        mBinding.fragmentRestaurantDetailsLikeTv.setText(getString(mCurrentUser.getWorkmateLikedRestaurantTvText()));
        mBinding.fragmentRestaurantDetailsLikeIv.setColorFilter(getResources().getColor(mCurrentUser.getWorkmateLikedRestaurantTvColor()));
    }

    private void updateViewRestaurant() {
        mBinding.fragmentRestaurantDetailsNameTv.setText(mRestaurant.getName());
        mBinding.fragmentRestaurantDetailsAddressTv.setText(mRestaurant.getAddress());

        Glide.with(this)
                .load(mRestaurant.getPhotoUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(mBinding.fragmentRestaurantDetailsPhotoIv);

        mBinding.fragmentRestaurantDetailsStar1Iv.setVisibility(mRestaurant.getStar1IvVisibility());
        mBinding.fragmentRestaurantDetailsStar2Iv.setVisibility(mRestaurant.getStar2IvVisibility());
        mBinding.fragmentRestaurantDetailsStar3Iv.setVisibility(mRestaurant.getStar3IvVisibility());

        mBinding.fragmentRestaurantDetailsCallLl.setVisibility(mRestaurant.getDetailsCallLlVisibility());
        mBinding.fragmentRestaurantDetailsWebsiteLl.setVisibility(mRestaurant.getDetailsWebsiteLlVisibility());
    }

    private void choseRestaurant() {
        mViewModel.choseRestaurant(mRestaurant, mCurrentUser);
    }

    private void callRestaurant() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + mRestaurant.getPhoneNumber()));
        startActivity(callIntent);
    }

    private void likeRestaurant() {
        mViewModel.likeRestaurant(mRestaurant, mCurrentUser);
    }

    private void visitRestaurantWebsite() {
        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
        openBrowserIntent.setData(Uri.parse(mRestaurant.getWebsite()));
        startActivity(openBrowserIntent);
    }

    // ---------------
    // Utils
    // ---------------

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.fragmentRestaurantDetailsMotionLayout, getString(stringId), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.clearDisposables();
    }
}