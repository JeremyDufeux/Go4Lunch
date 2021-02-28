package com.jeremydufeux.go4lunch;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.jeremydufeux.go4lunch.databinding.FragmentLoginBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED;
import static com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR;
import static com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT;

public class LoginFragment extends BaseFragment implements FacebookCallback<LoginResult> {
    private static final int RC_SIGN_IN = 1000;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    private FragmentLoginBinding mBinding;

    public LoginFragment() {
        // Required empty public constructor
    }
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onStart() {
        super.onStart();

        if (isCurrentUserLoggedIn()) {
            navigateToMainFragment();
        }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Debug", "onActivityResult");
        if (requestCode == RC_SIGN_IN) { // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            firebaseAuthWithGoogle(data);
        } else {
            // Used for facebook sign in
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void navigateToMainFragment() {
        showSnackBar("Login ok");
    }

    // ---------------
    // Google Auth
    // ---------------

    private void configureGoogleSignIn() {
        mBinding.loginActivityFirebaseAuthGoogleBtn.setOnClickListener(v -> signInWithGoogle());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_request_id_token))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

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
                        .addOnCompleteListener(getActivity(), taskComplete -> {
                            if (taskComplete.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                navigateToMainFragment();
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
        mBinding.loginActivityFrameLayoutFacebookButton.loginActivityFirebaseAuthFacebookBtn.setOnClickListener(
                v -> mBinding.loginActivityFrameLayoutFacebookButton.loginActivityFacebookLoginButton.performClick());

        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = mBinding.loginActivityFrameLayoutFacebookButton.loginActivityFacebookLoginButton;
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
        if(error.getMessage().equals(getString(R.string.error_facebook_connection_failure))) {
            showSnackBar(getString(R.string.error_no_internet));
        } else {
            showSnackBar(getString(R.string.error_unknown_error));
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        getAuth().signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        navigateToMainFragment();
                    } else {
                        // If sign in fails, display a message to the user.
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                });
    }

    // ---------------
    // Utils
    // ---------------
    private void showSnackBar(String message){
        Log.d("Debug", "showSnackBar: message : " + message);
        Snackbar.make(mBinding.loginFragmentCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }
}