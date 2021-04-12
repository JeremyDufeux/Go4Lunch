package com.jeremydufeux.go4lunch.ui.fragment.listView;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewBinding;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.StopRefreshLiveEvent;

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
            mBinding.listViewFragmentSrl.setRefreshing(false);

            mRestaurantList = restaurantList;
            mAdapter.updateList(mRestaurantList);

            if(restaurantList.size()==0){
                mBinding.listViewFragmentRecyclerView.setVisibility(View.INVISIBLE);
                mBinding.listViewFragmentNoRestaurantTv.setVisibility(View.VISIBLE);
                mBinding.listViewFragmentSrl.setEnabled(false);
            } else {
                mBinding.listViewFragmentRecyclerView.setVisibility(View.VISIBLE);
                mBinding.listViewFragmentNoRestaurantTv.setVisibility(View.INVISIBLE);
                mBinding.listViewFragmentSrl.setEnabled(true);
            }
        };
    }

    private Observer<LiveEvent> onEventReceived() {
        return event -> {
            if(event instanceof ShowSnackbarLiveEvent){
                showSnackBar(((ShowSnackbarLiveEvent) event).getStingId());
            } else if(event instanceof StopRefreshLiveEvent){
                mBinding.listViewFragmentSrl.setRefreshing(false);
                showSnackBar(R.string.no_more_restaurants_found);
            }
        };
    }

    private void configureRecyclerView() {
        mAdapter = new ListViewPlacesAdapter(this);
        RecyclerView recyclerView = mBinding.listViewFragmentRecyclerView;
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        mBinding.listViewFragmentSrl.setColorSchemeResources(R.color.orange);
        mBinding.listViewFragmentSrl.setOnRefreshListener(() -> mViewModel.loadNextPage());
    }

    @Override
    public void onPlaceClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.arg_restaurant_id), mRestaurantList.get(position).getUId());
        Navigation.findNavController(mBinding.getRoot()).navigate(R.id.restaurant_details_fragment, bundle);
    }

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.getRoot(), getString(stringId), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.clearDisposables();
    }
}