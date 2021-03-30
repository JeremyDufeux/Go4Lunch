package com.jeremydufeux.go4lunch.ui.fragment.login;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentLoginBinding;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.LiveEvent.CreateWorkmateErrorLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.CreateWorkmateSuccessLiveEvent;
import com.jeremydufeux.go4lunch.utils.LiveEvent.LiveEvent;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED;
import static com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR;
import static com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT;

@AndroidEntryPoint
public class LoginFragment extends Fragment implements FacebookCallback<LoginResult> {
    private static final int RC_SIGN_IN = 1000;

    private LoginViewModel mViewModel;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    private FragmentLoginBinding mBinding;

    public LoginFragment() { }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureViewModels();
    }

    private void configureViewModels() {
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mViewModel.observeResult().observe(this, this::firestoreResultObserver);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = FragmentLoginBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        configureGoogleSignIn();
        configureFacebookSignIn();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isCurrentUserLoggedIn()) {
            navigateToMapFragment();
        } else {
            showLoginButtons();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) { // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            firebaseAuthWithGoogle(data);
        } else {
            // Used for facebook sign in
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void navigateToMapFragment() {
        Navigation.findNavController(mBinding.getRoot()).navigate(R.id.action_login_fragment_to_map_view_fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.observeResult().removeObservers(this);
        mBinding = null;
    }

    // ---------------
    // Google Auth
    // ---------------

    private void configureGoogleSignIn() {
        mBinding.loginFragmentFirebaseAuthGoogleBtn.setOnClickListener(v -> signInWithGoogle());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_request_id_token))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }


    @SuppressWarnings("deprecation")
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                getAuth().signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity(), taskComplete -> {
                            if (taskComplete.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                createUserInFireStore();
                            } else {
                                // If sign in fails, display a message to the user.
                                showSnackBar(getString(R.string.error_unknown_error));
                            }
                        });
            }
        } catch (ApiException e) {
            // Google Sign In failed, display a message to the user
            switch (e.getStatusCode()){
                case NETWORK_ERROR:
                    showSnackBar(getString(R.string.error_no_internet));
                    break;
                case TIMEOUT:
                    showSnackBar(getString(R.string.error_timeout));
                    break;
                case SIGN_IN_CANCELLED:
                    showSnackBar(getString(R.string.error_authentication_canceled));
                    break;
                default:
                    showSnackBar(getString(R.string.error_unknown_error));
                    break;
            }
        }
    }

    // ---------------
    // Facebook Auth
    // ---------------

    private void configureFacebookSignIn() {
        mBinding.loginFragmentFrameLayoutFacebookButton.frameFirebaseAuthFacebookBtn.setOnClickListener(
                v -> mBinding.loginFragmentFrameLayoutFacebookButton.frameFacebookLoginButton.performClick());

        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = mBinding.loginFragmentFrameLayoutFacebookButton.frameFacebookLoginButton;
        loginButton.setPermissions(Arrays.asList("email", "public_profile"));
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, this);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        showSnackBar(getString(R.string.error_authentication_canceled));
    }

    @Override
    public void onError(FacebookException error) {
        if(Objects.equals(error.getMessage(), getString(R.string.error_facebook_connection_failure))) {
            showSnackBar(getString(R.string.error_no_internet));
        } else {
            showSnackBar(getString(R.string.error_unknown_error));
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        getAuth().signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        createUserInFireStore();
                    } else {
                        // If sign in fails, display a message to the user.
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                });
    }

    // ---------------
    // FireStore
    // ---------------

    private FirebaseAuth getAuth(){
        return FirebaseAuth.getInstance();
    }

    @Nullable
    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private Boolean isCurrentUserLoggedIn(){
        return getCurrentUser() != null;
    }

    private void createUserInFireStore(){
        if (getCurrentUser() != null){
            List<? extends UserInfo> providerData = getCurrentUser().getProviderData();

            String uid = getCurrentUser().getUid();
            String name = providerData.get(1).getDisplayName();
            String email = providerData.get(1).getEmail();
            String pictureUrl = Objects.requireNonNull(providerData.get(1).getPhotoUrl()).toString();

            Workmate workmate = new Workmate(uid, name, email, pictureUrl);
            mViewModel.createWorkmate(workmate);
        }
    }

    private void firestoreResultObserver(LiveEvent result){

        if(result instanceof CreateWorkmateSuccessLiveEvent) {
            navigateToMapFragment();
        } else if( result instanceof CreateWorkmateErrorLiveEvent){
            showSnackBar(getString(R.string.error_unknown_error));

            Log.d("Debug", "onFirestoreResult : " + ((CreateWorkmateErrorLiveEvent) result).getException().toString());
        }
    }

    // ---------------
    // Utils
    // ---------------

    private void showSnackBar(String message){
        Snackbar.make(mBinding.loginFragmentCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showLoginButtons(){
        mBinding.loginFragmentFirebaseAuthGoogleBtn.animate().alpha(1).setDuration(1000).setStartDelay(500).start();
        mBinding.loginFragmentFrameLayoutFacebookButton.frameFirebaseAuthFacebookBtn.animate().alpha(1).setDuration(1000).setStartDelay(500).start();
    }
}