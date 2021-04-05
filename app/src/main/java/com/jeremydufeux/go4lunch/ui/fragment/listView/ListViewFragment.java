package com.jeremydufeux.go4lunch.ui.fragment.listView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.jeremydufeux.go4lunch.MainNavDirections;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewBinding;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment implements ListViewPlacesAdapter.OnPlaceListener {

    private ListViewViewModel mViewModel;

    private FragmentListViewBinding mBinding;
    private ListViewPlacesAdapter mAdapter;

    private List<Restaurant> mRestaurantList = new ArrayList<>();

    public ListViewFragment() {}

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModel();
    }

    private void configureViewModel() {
        mViewModel = new ViewModelProvider(this).get(ListViewViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentListViewBinding.inflate(getLayoutInflater());
        configureObservers();
        configureRecyclerView();

        return mBinding.getRoot();
    }

    private void configureObservers() {
        mViewModel.startObservers();
        mViewModel.observeRestaurantList().observe(getViewLifecycleOwner(), onRestaurantListChanged());
        mViewModel.observeEvents().observe(getViewLifecycleOwner(), onEventReceived());
    }

    private Observer<List<Restaurant>> onRestaurantListChanged() {
        return restaurantList -> {
            mRestaurantList = restaurantList;
            mAdapter.updateList(mRestaurantList);
        };
    }

    private Observer<LiveEvent> onEventReceived() {
        return event -> {
            if(event instanceof ShowSnackbarLiveEvent){
                showSnackBar(((ShowSnackbarLiveEvent) event).getStingId());
            }
        };
    }

    private void configureRecyclerView() {
        mAdapter = new ListViewPlacesAdapter(Glide.with(this), this);
        mBinding.listViewFragmentRecyclerView.setAdapter(mAdapter);
        mBinding.listViewFragmentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(new ColorDrawable(getResources().getColor(R.color.very_light_grey)));
        mBinding.listViewFragmentRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onPlaceClick(int position) {
        MainNavDirections.ActionGlobalRestaurantDetailsFragment directions = MainNavDirections.actionGlobalRestaurantDetailsFragment();
        directions.setRestaurantId(mRestaurantList.get(position).getUId());

        Navigation.findNavController(mBinding.getRoot()).navigate(directions);
    }

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.listViewFragmentCl, getString(stringId), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.clearDisposables();
    }
}