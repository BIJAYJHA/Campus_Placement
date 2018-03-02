package bijay.com.campusplacement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView sign_Email, signed_mail;
    private static final String TAG = "GoogleSignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //Edit text
        sign_Email = findViewById(R.id.sign_Email);
        signed_mail = findViewById(R.id.signed_mail);
        progressBar = findViewById(R.id.progressBar2);
        //Button
        findViewById(R.id.signin_Button).setOnClickListener(this);
        findViewById(R.id.signout_Button).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //Google SignIn object initialization
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //FirEBASE auth initialization
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
                updateUI(null);
            }
        }
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
        Snackbar.make(findViewById(R.id.signin_layout), "Sign Out", Snackbar.LENGTH_SHORT).show();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        progressBar.setVisibility(View.VISIBLE);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.signin_Button) {
            signIn();

        }
        else if(i == R.id.signout_Button){
            signOut();
        }
    }

    private void updateUI(FirebaseUser user) {
        progressBar.setVisibility(View.GONE);
        if (user != null) {
            signed_mail.setText(getString(R.string.google_status_fmt, user.getEmail()));
            sign_Email.setText(getString(R.string.firebase_status_fmt, user.getDisplayName()));
            Snackbar.make(findViewById(R.id.signin_layout), "you are signed In!! ", Snackbar.LENGTH_SHORT).show();
            findViewById(R.id.signin_Button).setVisibility(View.GONE);
            findViewById(R.id.signout_Button).setVisibility(View.VISIBLE);
          //  sign_Email.setText("User is signed");
        }
       else {
            sign_Email.setText(R.string.sign_out);
            signed_mail.setText(null);

            findViewById(R.id.signin_Button).setVisibility(View.VISIBLE);
            findViewById(R.id.signout_Button).setVisibility(View.GONE);
          //  sign_Email.setText("User is now signed Out");
       }
    }
}
