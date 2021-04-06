package com.jeremydufeux.go4lunch.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.jeremydufeux.go4lunch.MainNavDirections;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.ActivityMainBinding;
import com.jeremydufeux.go4lunch.databinding.ActivityMainDrawerHeaderBinding;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.SignOutLiveEvent;

import dagger.hilt.android.AndroidEntryPoint;

import static com.jeremydufeux.go4lunch.utils.Utils.isToday;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private MainActivityViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private ActivityMainDrawerHeaderBinding mHeaderBinding;

    private NavController mNavController;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Workmate mWorkmate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Go4Lunch);
        super.onCreate(savedInstanceState);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        View headerView = mBinding.activityMainNavView.getHeaderView(0);
        mHeaderBinding = ActivityMainDrawerHeaderBinding.bind(headerView);

        configureViewModels();
        configureNavController();
        configureBottomNavigation();
        configureToolbar();
        configureDrawer();
        configureNavControllerListener();
    }

    // ---------------
    // Configuration
    // ---------------

    private void configureViewModels() {
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mViewModel.startObservers();
        mViewModel.observeEvents().observe(this, onEventReceived());
        mViewModel.observeCurrentUser().observe(this, this::onUserDataChange);
    }

    private void configureNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_nav_host);
        assert navHostFragment != null;
        mNavController = navHostFragment.getNavController();
    }

    private void configureBottomNavigation() {
        mBottomNavigationView = mBinding.mainActivityBottomNavView;
        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);
    }

    private void configureToolbar() {
        setSupportActionBar(mToolbar);

        mToolbar = mBinding.mainActivityToolbar;
        mToolbar.setTitle(getString(R.string.i_m_hungry));

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.map_view_fragment, R.id.list_view_fragment, R.id.workmates_fragment)
                        .setOpenableLayout(mBinding.getRoot())
                        .build();

        NavigationUI.setupWithNavController( mToolbar, mNavController, appBarConfiguration);
    }

    private void configureDrawer() {
        NavigationView navigationView = findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerLayout = mBinding.activityMainDrawerLayout;
    }

    private void configureNavControllerListener() {
        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            if(destination.getId() == R.id.login_fragment) {
                mToolbar.setVisibility(View.GONE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mBottomNavigationView.setVisibility(View.GONE);
            } else if(destination.getId() == R.id.settings_fragment) {
                mToolbar.setVisibility(View.VISIBLE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mBottomNavigationView.setVisibility(View.GONE);
            } else if(destination.getId() == R.id.restaurant_details_fragment) {
                mToolbar.setVisibility(View.GONE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mBottomNavigationView.setVisibility(View.GONE);
            } else {
                mToolbar.setVisibility(View.VISIBLE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mBottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    // ---------------
    // Firebase Auth
    // ---------------

    private void onUserDataChange(Workmate workmate) {
        mWorkmate = workmate;
        Glide.with(this).load(mWorkmate.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(mHeaderBinding.drawerProfilePicIv);
        mHeaderBinding.drawerProfilePicIv.setVisibility(View.VISIBLE);
        mHeaderBinding.drawerNameTv.setText(mWorkmate.getDisplayName());
        mHeaderBinding.drawerNameTv.setVisibility(View.VISIBLE);
        mHeaderBinding.drawerEmailTv.setText(mWorkmate.getEmail());
        mHeaderBinding.drawerEmailTv.setVisibility(View.VISIBLE);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        mNavController.navigate(R.id.action_global_login_fragment);
    }

    // ---------------
    // Activity Overrides
    // ---------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //2 - Inflate the menu and add it to the Toolbar
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.restaurant_details_fragment){
            navigateToRestaurantDetails();
        }
        else if(item.getItemId() == R.id.settings_fragment){
            mNavController.navigate(R.id.action_global_settings_fragment);
        }
        else if(item.getItemId() == R.id.logout){
            logout();
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void navigateToRestaurantDetails(){
        if(!mWorkmate.getChosenRestaurantId().isEmpty() && isToday(mWorkmate.getChosenRestaurantDate())) {
            MainNavDirections.ActionGlobalRestaurantDetailsFragment directions = MainNavDirections.actionGlobalRestaurantDetailsFragment();
            directions.setRestaurantId(mWorkmate.getChosenRestaurantId());

            mNavController.navigate(directions);
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            showSnackBar(R.string.you_didnt_chose_restaurant);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.clearDisposables();
    }

    // ---------------
    // Live Events
    // ---------------

    private Observer<LiveEvent> onEventReceived() {
        return event -> {
            if(event instanceof ShowSnackbarLiveEvent){
                showSnackBar(((ShowSnackbarLiveEvent) event).getStingId());
            } else if(event instanceof SignOutLiveEvent){
                logout();
            }
        };
    }

    // ---------------
    // Utils
    // ---------------

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.mainActivityCoordinator, getString(stringId), Snackbar.LENGTH_LONG).show();
    }
}