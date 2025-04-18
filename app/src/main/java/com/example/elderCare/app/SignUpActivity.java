package com.example.elderCare.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    static final String FIRESTORE_WRITE_TAG = "ADDITION TO DATABASE: ";
    static final String SUCCESS = "SUCCESS";
    static final String FAILURE = "FAILURE";

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private GoogleSignInAccount account;
    private Button yesButton;
    private Button noButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        account = GoogleSignIn.getLastSignedInAccount(this);
        db = FirebaseFirestore.getInstance();
        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        yesButton.setOnClickListener(view -> setData(true));

        noButton.setOnClickListener(view -> setData(false));
    }

    /**
     * Get data from user Google account and input and store in firestore
     * @param isCarer
     */
    private void setData(boolean isCarer) {
        String givenName = account.getGivenName() == null ? " " : account.getGivenName();
        String lastName = account.getFamilyName() == null ? " " : account.getFamilyName();
        String email = account.getEmail() == null ? " " : account.getEmail();
        DocumentReference newUser = db.collection("users").document(currentUser.getUid());

        Log.d("register user", "setdata called");
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", givenName);
        user.put("lastName", lastName);
        user.put("email", email);
        user.put("isOnline", true);
        user.put("isCarer", isCarer);
        user.put("connectedUser",null);
        newUser.set(user).
                addOnSuccessListener(aVoid -> Log.d(FIRESTORE_WRITE_TAG, SUCCESS)).addOnFailureListener(e -> Log.d(FIRESTORE_WRITE_TAG, FAILURE));
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }
}
