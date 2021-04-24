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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.databinding.FragmentLoginBinding;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.utils.liveEvent.LiveEvent;
import com.jeremydufeux.go4lunch.utils.liveEvent.ShowSnackbarLiveEvent;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_CANCELLED;
import static com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR;
import static com.google.android.gms.common.api.CommonStatusCodes.TIMEOUT;
import static com.jeremydufeux.go4lunch.ui.fragment.login.EmailDialog.MODE_RESET_PASSWORD;
import static com.jeremydufeux.go4lunch.ui.fragment.login.EmailDialog.MODE_SIGN_IN;
import static com.jeremydufeux.go4lunch.ui.fragment.login.EmailDialog.MODE_SIGN_UP;

@AndroidEntryPoint
public class LoginFragment extends Fragment implements FacebookCallback<LoginResult> {
    private static final String TAG = "LoginFragment";
    public static final String DEFAULT_PICTURE_URL = "https://firebasestorage.googleapis.com/v0/b/go4lunch-7364a.appspot.com/o/default_picture_url.png?alt=media&token=a30259e7-dcad-4cff-835e-d764bbb2f796";
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
        mViewModel.observeEvents().observe(this, this::onEventReceived);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewModel.startObservers();

        mBinding = FragmentLoginBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        configureTwitterSignIn();
        configureFacebookSignIn();
        configureEmailSignIn();
        configureGoogleSignIn();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getCurrentUser() != null) {
            startCurrentUserObserverAndNavigate();
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
        mViewModel.clearDisposables();
    }

    // ---------------
    // Email Auth
    // ---------------

    private void configureEmailSignIn() {
        mBinding.loginFragmentFirebaseAuthEmailBtn.setOnClickListener(v -> openEmailDialog());
    }

    private void openEmailDialog(){
        EmailDialog emailDialog = new EmailDialog();
        emailDialog.setListener(this::signUpWithEmail);
        emailDialog.show(getActivity().getSupportFragmentManager(), null);
    }

    private void signUpWithEmail(int mode, String email, String password, String name, String nickname) {
        if(mode == MODE_SIGN_UP) {
            signUpWithEmail(email, password, name, email);
        } else if(mode == MODE_SIGN_IN){
            signInWithEmail(email, password);
        } else if(mode == MODE_RESET_PASSWORD){
            sendResetPasswordEmail(email);
        }
    }

    private void signUpWithEmail(String email, String password, String name, String nickname) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = getCurrentUser();
                        Workmate workmate = new Workmate(firebaseUser.getUid(), name, nickname, email, DEFAULT_PICTURE_URL);
                        attemptToCreateWorkmateAndNavigate(workmate);
                    } else {
                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            showSnackBar(R.string.this_email_address_is_already_used);
                        } else {
                            showSnackBar(R.string.error);
                        }
                    }
                });
    }


    private void signInWithEmail(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        startCurrentUserObserverAndNavigate();
                    } else {
                        Log.e(TAG, "signInWithEmail:failure", task.getException());
                        if(task.getException() instanceof FirebaseAuthInvalidUserException
                                || task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                            showSnackBar(R.string.Wrong_email_or_password);
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException){
                            showSnackBar(R.string.this_email_address_is_already_used);
                        } else {
                            showSnackBar(R.string.error);
                        }
                    }
                });
    }

    private void sendResetPasswordEmail(String email){
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showSnackBar(R.string.email_sent);
                    } else {
                        Log.e(TAG, "sendPasswordResetEmail:failure", task.getException());
                        showSnackBar(R.string.error);
                    }
                });
    }

    // ---------------
    // Twitter Auth
    // ---------------

    private void configureTwitterSignIn() {
        mBinding.loginFragmentFirebaseAuthTwitterBtn.setOnClickListener(v -> signInWithTwitter());
    }

    private void signInWithTwitter() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask
                    .addOnSuccessListener( authResult -> signInWithCredential(authResult.getCredential()))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "signInWithTwitter:failure", e);
                        showSnackBar(R.string.error);
                    });
        } else {
            firebaseAuth.startActivityForSignInWithProvider(getActivity(), provider.build())
                    .addOnSuccessListener(authResult -> signInWithCredential(authResult.getCredential()))
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "signInWithTwitter:failure", e);
                        showSnackBar(R.string.error);
                    });
        }
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
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                signInWithCredential(credential);
            }
        } catch (ApiException e) {
            switch (e.getStatusCode()){
                case NETWORK_ERROR:
                    showSnackBar(R.string.error_no_internet);
                    break;
                case TIMEOUT:
                    showSnackBar(R.string.error_timeout);
                    break;
                case SIGN_IN_CANCELLED:
                    showSnackBar(R.string.error_authentication_canceled);
                    break;
                default:
                    showSnackBar(R.string.error);
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
        firebaseAuthWithFacebook(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
        showSnackBar(R.string.error_authentication_canceled);
    }

    @Override
    public void onError(FacebookException error) {
        if(Objects.equals(error.getMessage(), getString(R.string.error_facebook_connection_failure))) {
            showSnackBar(R.string.error_no_internet);
        } else {
            showSnackBar(R.string.error);
        }
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        signInWithCredential(credential);
    }

    // ---------------
    // Sign in
    // ---------------

    private void signInWithCredential(AuthCredential credential){
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), taskComplete -> {
                    if (taskComplete.isSuccessful()) {
                        Workmate workmate = createWorkmateFromFirebase();
                        attemptToCreateWorkmateAndNavigate(workmate);
                    } else {
                        Log.e(TAG, "signInWithCredential: ", taskComplete.getException());
                        showSnackBar(R.string.error);
                    }
                });
    }

    private Workmate createWorkmateFromFirebase(){
        FirebaseUser firebaseUser = getCurrentUser();

        String uId = firebaseUser.getUid();
        String fullName = firebaseUser.getProviderData().get(1).getDisplayName();

        assert fullName != null;
        List<String> parts = Arrays.asList(fullName.split(" "));
        String nickname = parts.get(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < parts.size(); i++) {
            sb.append(parts.get(i));
            if(i != parts.size()-1) sb.append(" ");
        }

        String email = firebaseUser.getProviderData().get(1).getEmail();

        String pictureUrl;
        if(firebaseUser.getProviderData().get(1).getPhotoUrl().toString() != null){
            pictureUrl = firebaseUser.getProviderData().get(1).getPhotoUrl().toString();
        } else {
            pictureUrl = DEFAULT_PICTURE_URL;
        }

        return new Workmate(uId, fullName, nickname, email, pictureUrl);
    }

    private void attemptToCreateWorkmateAndNavigate(Workmate workmate){
        mViewModel.attemptToCreateWorkmate(workmate);
        navigateToMapFragment();
    }

    private void startCurrentUserObserverAndNavigate(){
        FirebaseUser firebaseUser = getCurrentUser();
        mViewModel.startCurrentUserObserver(firebaseUser.getUid());
        navigateToMapFragment();
    }

    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // ---------------
    // Events
    // ---------------

    private void onEventReceived(LiveEvent event){
        if(event instanceof ShowSnackbarLiveEvent){
            showSnackBar(((ShowSnackbarLiveEvent) event).getStingId());
        }
    }

    // ---------------
    // Utils
    // ---------------

    private void showSnackBar(int stringId){
        Snackbar.make(mBinding.loginFragmentCoordinatorLayout, getString(stringId), Snackbar.LENGTH_LONG).show();
    }

    private void showLoginButtons(){
        mBinding.loginFragmentFirebaseAuthGoogleBtn.animate().alpha(1).setDuration(1000).setStartDelay(500).start();
        mBinding.loginFragmentFrameLayoutFacebookButton.frameFirebaseAuthFacebookBtn.animate().alpha(1).setDuration(1000).setStartDelay(500).start();
    }
}