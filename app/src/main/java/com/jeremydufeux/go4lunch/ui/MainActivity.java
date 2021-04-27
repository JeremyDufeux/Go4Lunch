package com.jeremydufeux.go4lunch.ui;

import android.animation.Animator;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.ActivityMainBinding;
import com.jeremydufeux.go4lunch.databinding.ActivityMainDrawerHeaderBinding;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.SignOutLiveEvent;

import dagger.hilt.android.AndroidEntryPoint;

import static com.jeremydufeux.go4lunch.utils.Utils.isToday;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        SearchView.OnQueryTextListener{

    public static final String CURSOR_MATRIX_NAME = "name";
    public static final String CURSOR_MATRIX_ID = "id";

    private MainActivityViewModel mViewModel;
    private ActivityMainBinding mBinding;
    private ActivityMainDrawerHeaderBinding mHeaderBinding;

    private NavController mNavController;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private MenuItem mSearchItem;
    private SimpleCursorAdapter mAdapter;

    private Workmate mWorkmate;
    private Boolean mSearchVisible = false;

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
        configureSearchView();
    }

    // ---------------
    // Configuration
    // ---------------

    private void configureViewModels() {
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mViewModel.startObservers();
        mViewModel.observeEvents().observe(this, onEventReceived());
        mViewModel.observeCurrentUser().observe(this, this::onUserDataChange);
        mViewModel.observeAutocomplete().observe(this, this::onAutocompleteChange);
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
        mToolbar = mBinding.mainActivityToolbar;
        mToolbar.setTitle(getString(R.string.i_m_hungry));

        setSupportActionBar(mToolbar);

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
                mSearchItem.setVisible(false);
                hideSearch();
            } else if(destination.getId() == R.id.restaurant_details_fragment) {
                mToolbar.setVisibility(View.GONE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mBottomNavigationView.setVisibility(View.GONE);
            } else {
                mToolbar.setVisibility(View.VISIBLE);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mBottomNavigationView.setVisibility(View.VISIBLE);
                if(mSearchItem != null) {
                    if(destination.getId() != R.id.workmates_fragment) {
                        mSearchItem.setVisible(true);
                    } else {
                        mSearchItem.setVisible(false);
                        hideSearch();
                    }
                }
            }
        });
    }

    private void configureSearchView(){
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        SearchView searchView = mBinding.mainActivitySearchView;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showInputMethod(view.findFocus());
            }
        });

        final String[] from = new String[] {CURSOR_MATRIX_NAME, CURSOR_MATRIX_ID};
        final int[] to = new int[] {android.R.id.text1};

        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_dropdown_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchView.setSuggestionsAdapter(mAdapter);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                onAutocompleteSelection(position);
                return false;
            }
        });

        mBinding.mainActivityCloseSearchButton.setOnClickListener(v -> hideSearch());
    }

    // ---------------
    // Search queries
    // ---------------

    @Override
    public boolean onQueryTextSubmit(String query) {
        onAutocompleteSelection(0);
        return false;
    }

    private void onAutocompleteSelection(int position){
        if(mAdapter.getCount() != 0) {
            Cursor cursor = (Cursor) mAdapter.getItem(position);
            String restaurantId = cursor.getString(cursor.getColumnIndex(CURSOR_MATRIX_ID));
            navigateToRestaurantDetails(restaurantId);
            hideSearch();
        } else {
            showSnackBar(R.string.no_restaurants_found);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mViewModel.onQueryTextChange(newText);
        return false;
    }

    private void onAutocompleteChange(MatrixCursor matrixCursor) {
        mAdapter.changeCursor(matrixCursor);
    }

    // ---------------
    // Firebase Auth
    // ---------------

    private void onUserDataChange(Workmate workmate) {
        mWorkmate = workmate;

        Glide.with(this)
                .load(mWorkmate.getPictureUrl())
                .error(R.drawable.ic_default_workmate_picture)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mHeaderBinding.drawerProfilePicIv);

        mHeaderBinding.drawerProfilePicIv.setVisibility(View.VISIBLE);
        mHeaderBinding.drawerNameTv.setText(mWorkmate.getFullName());
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
    // Navigation
    // ---------------

    private void navigateToRestaurantDetails(String restaurantId){
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.arg_restaurant_id), restaurantId);
        mNavController.navigate(R.id.restaurant_details_fragment, bundle);
    }

    // ---------------
    // Activity Overrides
    // ---------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        mSearchItem = menu.findItem(R.id.search);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.restaurant_details_fragment){
            openCurrentUserTodayRestaurantDetails();
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

    private void openCurrentUserTodayRestaurantDetails(){
        if(!mWorkmate.getChosenRestaurantId().isEmpty() && isToday(mWorkmate.getChosenRestaurantDate())) {
            navigateToRestaurantDetails(mWorkmate.getChosenRestaurantId());
        } else {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            showSnackBar(R.string.you_didnt_chose_restaurant);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.search && !mSearchVisible){
            showSearch();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            mBinding.mainActivitySearchView.setQuery(query, false);
        }
    }

    @Override
    public void onBackPressed() {
        if(mSearchVisible){
            hideSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.savePreferences();
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
    // View animations
    // ---------------

    private void showSearch() {
        int duration = 500;
        mBinding.mainActivitySearchCard.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mSearchVisible = true;
                mBinding.mainActivitySearchCard.setVisibility(View.VISIBLE);
                mBinding.mainActivityCloseSearchButton.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mBinding.mainActivitySearchCard.requestFocus();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        }).alpha(1).setDuration(duration).start();

        mBinding.mainActivityCloseSearchButton.animate().alpha(1).setDuration(duration).start();
    }

    private void hideSearch() {
        int duration = 500;
        mBinding.mainActivitySearchCard.animate().setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                mBinding.mainActivitySearchCard.setVisibility(View.GONE);
                mBinding.mainActivityCloseSearchButton.setVisibility(View.GONE);
                mSearchVisible = false;
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        }).alpha(0).setDuration(duration).start();

        mBinding.mainActivityCloseSearchButton.animate().alpha(0).setDuration(duration).start();
    }

    // ---------------
    // Utils
    // ---------------

    private void showInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.mainActivityCoordinator, getString(stringId), Snackbar.LENGTH_LONG).show();
    }
}