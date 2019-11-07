package ratingapp.ddey.com.testratingapp.ui.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseHelper;
import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.User;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText tieEmail, tiePassword, tieConfirmPassword, tieName;
    private ProgressBar progressBar;

    private DatabaseHelper databaseHelper;
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeComponents();
    }

    public void initializeComponents() {
        databaseHelper = new DatabaseHelper(this);
        firebaseHelper = FirebaseHelper.getInstance();

        tieName = findViewById(R.id.register_tie_name);
        tieEmail = findViewById(R.id.register_tie_email);
        tiePassword = findViewById((R.id.register_tie_password));
        tieConfirmPassword = findViewById((R.id.register_tie_confirm_password));
        progressBar = findViewById(R.id.progressbar);
        TextView tvReturn = findViewById(R.id.textViewLogin);
        Button btnRegister = findViewById(R.id.buttonSignUp);

        tvReturn.setOnClickListener(returnToLoginEvent());
        btnRegister.setVisibility(View.VISIBLE);
        btnRegister.setOnClickListener(registerEvent());
    }

    @NonNull
    private View.OnClickListener returnToLoginEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        };
    }

    @NonNull
    private View.OnClickListener registerEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        };
    }

    private void registerUser() {
        if (isValid()) {
            String email = tieEmail.getText().toString();
            String password = tiePassword.getText().toString();
            String name = tieName.getText().toString();

            User newUser = new User(email, password, name, null, null, null, null, false);
            if (verifyConnection()) {
                firebaseRegister(newUser);
            } else {
                Toast.makeText(getApplicationContext(), "You need internet connection to register an account", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseRegister(final User user) {
        if (user != null) {
            firebaseHelper.openConnection();
            progressBar.setVisibility(View.VISIBLE);
            firebaseHelper.getAuth().createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                user.setFirebaseToken(firebaseHelper.getAuth().getCurrentUser().getUid());
                                firebaseHelper.getUsersReference()
                                        .child(firebaseHelper.getAuth().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("FirebaseHelper: ", "User inserted " + user.toString());
                                            Toast.makeText(getApplicationContext(), "Successfully registered!", Toast.LENGTH_SHORT).show();
                                            databaseHelper.insertUser(user);
                                            finish();
                                        } else {
                                            Log.e("FirebaseHelper: ", "User not inserted");
                                        }
                                    }
                                });
                            } else {
                                Log.e("FirebaseHelper: ", "User creation failed!");

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getApplicationContext(), "This e-mail is already registered", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Error: user null", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isValid() {
        if (tieName.getText() == null || tieName.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.signup_error_name, Toast.LENGTH_SHORT).show();
            tieName.setError(getString(R.string.signup_error_name));
            tieName.requestFocus();
            return false;
        }
        if (tieEmail.getText() == null || tieEmail.getText().toString().trim().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(tieEmail.getText().toString()).matches()) {
            Toast.makeText(getApplicationContext(), R.string.error_email_login, Toast.LENGTH_SHORT).show();
            tieEmail.setError(getString(R.string.profile_activity_email_error));
            tieEmail.requestFocus();
            return false;
        } else if (tiePassword.getText() == null || tiePassword.getText().toString().trim().isEmpty() || tiePassword.getText().toString().length() < 6) {
            Toast.makeText(getApplicationContext(), R.string.signup_error_password, Toast.LENGTH_SHORT).show();
            tiePassword.setError(getString(R.string.signup_error_password));
            tiePassword.requestFocus();
            return false;
        } else if (!tiePassword.getText().toString().equals(tieConfirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.signup_error_password_match, Toast.LENGTH_SHORT).show();
            tieConfirmPassword.setError(getString(R.string.signup_error_password_match));
            tieConfirmPassword.requestFocus();
            return false;
        } else if (tieConfirmPassword.getText() == null || tieConfirmPassword.getText().toString().trim().isEmpty() || tieConfirmPassword.getText().toString().length() < 6) {
            Toast.makeText(getApplicationContext(), R.string.signup_error_password, Toast.LENGTH_SHORT).show();
            tieConfirmPassword.setError(getString(R.string.signup_error_password));
            tieConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(getApplicationContext());
        return connection.isOnline();
    }
}
