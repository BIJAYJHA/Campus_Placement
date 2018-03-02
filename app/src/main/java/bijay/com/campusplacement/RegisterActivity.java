package bijay.com.campusplacement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText mInputName;
    private EditText Input_Email_editText;
    private EditText Input_Password_editText;
    private FirebaseAuth mAuth;
    private ProgressBar Progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Input_Email_editText = findViewById(R.id.Input_Email_editText);
        Input_Password_editText = findViewById(R.id.Input_Password_editText);
        Progress = findViewById(R.id.ProgressBar);
        mAuth = FirebaseAuth.getInstance();

    }
   //yet to do sign in activity when sign in xml and code will be complete
   /* @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }*/

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }
       Progress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

      private void updateUI(FirebaseUser user) {

        if (user != null) {
            Toast.makeText(RegisterActivity.this, "Email is registered", Toast.LENGTH_SHORT).show();
           //  intent to sign in
            Intent i=new Intent(RegisterActivity.this,SigninActivity.class);
           startActivity(i);


        } else {
            Toast.makeText(RegisterActivity.this, "Email is not registered", Toast.LENGTH_SHORT).show();
        }
        Progress.setVisibility(View.GONE);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = Input_Email_editText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Input_Email_editText.setError("Required.");
            valid = false;
        } else {
            Input_Email_editText.setError(null);
        }

        String password = Input_Password_editText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Input_Password_editText.setError("Required.");
            valid = false;
        } else {
            Input_Password_editText.setError(null);
        }

        return valid;
    }


    public void registration(View view) {
        createAccount(Input_Email_editText.getText().toString(), Input_Password_editText.getText().toString());
    }
}
