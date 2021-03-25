package com.jeremydufeux.go4lunch.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.jeremydufeux.go4lunch.BaseFragment;
import com.jeremydufeux.go4lunch.MainNavDirections;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentListViewBinding;
import com.jeremydufeux.go4lunch.injection.Injection;
import com.jeremydufeux.go4lunch.injection.ViewModelFactory;
import com.jeremydufeux.go4lunch.models.Restaurant;
import com.jeremydufeux.go4lunch.ui.SharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class ListViewFragment extends BaseFragment implements ListViewPlacesAdapter.OnPlaceListener {

    private SharedViewModel mSharedViewModel;
    private ListViewViewModel mViewModel;

    private FragmentListViewBinding mBinding;
    private ListViewPlacesAdapter mAdapter;

    private List<Restaurant> mRestaurantList = new ArrayList<>();
    private final Subject<Location> mObservableLocation = PublishSubject.create();

    private final CompositeDisposable mDisposable = new CompositeDisposable();

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
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mSharedViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(SharedViewModel.class);
        mViewModel = new ViewModelProvider(this, viewModelFactory).get(ListViewViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentListViewBinding.inflate(getLayoutInflater());
        configureObservers();
        configureRecyclerView();

        return mBinding.getRoot();
    }

    private void configureObservers() {
        mDisposable.add(mViewModel.observeRestaurantList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onRestaurantListChanged()));

        mSharedViewModel.observeLocationPermissionGranted().observe(getViewLifecycleOwner(), this::onLocationPermissionGranted);
    }

    private void onLocationPermissionGranted(Boolean granted) {
        if (granted){
            mSharedViewModel.observeUserLocation().observe(this, this::onUserPositionChanged);
        }
    }

    private void onUserPositionChanged(Location location) {
        mObservableLocation.onNext(location);
    }

    private Consumer<List<Restaurant>> onRestaurantListChanged() {
        return restaurantList -> {
            mRestaurantList = restaurantList;
            mAdapter.updateList(mRestaurantList);
        };
    }

    private void configureRecyclerView() {
        mAdapter = new ListViewPlacesAdapter(requireContext(), Glide.with(this), mSharedViewModel.getLocation(), mObservableLocation, this);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSharedViewModel.observeLocationPermissionGranted().removeObservers(this);
        mSharedViewModel.observeUserLocation().removeObservers(this);
        mDisposable.clear();
    }
}