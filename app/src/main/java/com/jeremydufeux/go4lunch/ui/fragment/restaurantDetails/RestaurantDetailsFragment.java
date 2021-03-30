package com.jeremydufeux.go4lunch.ui.fragment.restaurantDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.jeremydufeux.go4lunch.databinding.FragmentRestaurantDetailsBinding;
import com.jeremydufeux.go4lunch.models.Restaurant;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantDetailsFragment extends Fragment {

    private RestaurantDetailsViewModel mViewModel;
    private FragmentRestaurantDetailsBinding mBinding;
    private RestaurantDetailsWorkmatesAdapter mAdapter;

    private Restaurant mRestaurant;

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
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentRestaurantDetailsBinding.inflate(getLayoutInflater());

        assert getArguments() != null;
        String restaurantId = RestaurantDetailsFragmentArgs.fromBundle(getArguments()).getRestaurantId();

        mRestaurant = mViewModel.getRestaurantWithId(restaurantId);

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
        mBinding.fragmentRestaurantDetailsNameTv.setText(mRestaurant.getName());
        mBinding.fragmentRestaurantDetailsAddressTv.setText(mRestaurant.getAddress());

        Glide.with(this)
                .load(mRestaurant.getPhotoUrl())
                .centerCrop()
                .into(mBinding.fragmentRestaurantDetailsPhotoIv);

        if (mRestaurant.getRating() > 0) {
            mBinding.fragmentRestaurantDetailsStar1Iv.setVisibility(View.VISIBLE);
        } else {
            mBinding.fragmentRestaurantDetailsStar1Iv.setVisibility(View.INVISIBLE);
        }
        if (mRestaurant.getRating() > 1.66) {
            mBinding.fragmentRestaurantDetailsStar2Iv.setVisibility(View.VISIBLE);
        } else {
            mBinding.fragmentRestaurantDetailsStar2Iv.setVisibility(View.INVISIBLE);
        }
        if (mRestaurant.getRating() > 3.33) {
            mBinding.fragmentRestaurantDetailsStar3Iv.setVisibility(View.VISIBLE);
        } else {
            mBinding.fragmentRestaurantDetailsStar3Iv.setVisibility(View.INVISIBLE);
        }

        // TODO Check if user is going to this place for lunch
        mBinding.fragmentRestaurantDetailsGoFab.setOnClickListener(v -> choseRestaurant());

        if(mRestaurant.getPhoneNumber() != null && !mRestaurant.getPhoneNumber().isEmpty()) {
            mBinding.fragmentRestaurantDetailsCallIv.setOnClickListener(v -> callRestaurant());
        } else {
            mBinding.fragmentRestaurantDetailsCallLl.setVisibility(View.GONE);
        }

        // TODO Check if user as liked the place
        mBinding.fragmentRestaurantDetailsLikeIv.setOnClickListener(v -> likeRestaurant());

        if(mRestaurant.getWebsite() != null && !mRestaurant.getWebsite().isEmpty()) {
            mBinding.fragmentRestaurantDetailsWebsiteIv.setOnClickListener(v -> visitRestaurantWebsite());
        } else {
            mBinding.fragmentRestaurantDetailsWebLl.setVisibility(View.GONE);
        }
    }

    private void choseRestaurant() {
        // TODO save to Firebase
    }

    private void callRestaurant() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + mRestaurant.getPhoneNumber()));
        startActivity(callIntent);
    }

    private void likeRestaurant() {
        // TODO save to Firebase
    }

    private void visitRestaurantWebsite() {
        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
        openBrowserIntent.setData(Uri.parse(mRestaurant.getWebsite()));
        startActivity(openBrowserIntent);
    }
}