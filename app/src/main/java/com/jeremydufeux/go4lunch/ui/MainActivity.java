package com.jeremydufeux.go4lunch.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.ActivityMainBinding;
import com.jeremydufeux.go4lunch.databinding.ActivityMainDrawerHeaderBinding;
import com.jeremydufeux.go4lunch.injection.Injection;
import com.jeremydufeux.go4lunch.injection.ViewModelFactory;
import com.jeremydufeux.go4lunch.models.Workmate;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        FirebaseAuth.AuthStateListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private MainActivityViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private ActivityMainDrawerHeaderBinding mHeaderBinding;

    private NavController mNavController;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

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
        configureFirebaseAuthListener();
    }

    // ---------------
    // Configuration
    // ---------------

    private void configureViewModels() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory();
        mViewModel = new ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel.class);
    }

    private void configureNavController() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_activity_nav_host);
        assert navHostFragment != null;
        mNavController = navHostFragment.getNavController();
    }

    private void configureBottomNavigation() {
        mBottomNavigationView = mBinding.bottomNavigationView;
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

    private void configureFirebaseAuthListener() {
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }

    // ---------------
    // Firebase Auth
    // ---------------

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            mViewModel.getWorkmateWithId(firebaseUser.getUid()).observe(this, this::onUserDataChange);
        }
    }

    private void onUserDataChange(Workmate workmate) {
        Glide.with(this).load(workmate.getPictureUrl()).apply(RequestOptions.circleCropTransform()).into(mHeaderBinding.drawerProfilePicIv);
        mHeaderBinding.drawerNameTv.setText(workmate.getUserName());
        mHeaderBinding.drawerEmailTv.setText(workmate.getEmail());
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
            mNavController.navigate(R.id.action_global_restaurant_details_fragment);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }
}