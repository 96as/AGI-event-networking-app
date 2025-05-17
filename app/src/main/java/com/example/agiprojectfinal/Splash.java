package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class Splash extends AppCompatActivity {
    private static final String TAG = "Splash";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        // Clear any existing session
        UserSession.getInstance().clearSession();
        
        // Initialize UserSession
        UserSession.getInstance().init(this);
        
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Force sign out to ensure fresh login
                mAuth.signOut();
                
                // Clear any existing session again
                UserSession.getInstance().clearSession();
                
                Log.d(TAG, "Forcing login page");
                startActivity(new Intent(Splash.this, Login.class));
                finish();
            }
        }, 2000);
    }
}
