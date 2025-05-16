package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {

    private TextView profileName, profileUsername, roleTextView, emailTextView, profileBio;
    private Button editProfileButton;
    private ImageView profilePic;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        profileName = findViewById(R.id.profileName);
        profileUsername = findViewById(R.id.profileUsername);
        roleTextView = findViewById(R.id.roleTextView);
        emailTextView = findViewById(R.id.emailTextView);
        profileBio = findViewById(R.id.profileBio);
        editProfileButton = findViewById(R.id.editProfileButton);
        profilePic = findViewById(R.id.profilePic);

        // Load user data
        loadUserData();

        // Set up edit profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, activity_editProfile.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data when returning to this activity
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            
            db.collection("Users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get user data
                                String name = document.getString("name");
                                String email = document.getString("email");
                                String role = document.getString("role");
                                String bio = document.getString("bio");
                                String profileImageUrl = document.getString("profileImageUrl");
                                
                                // Extract username from email (part before @)
                                String username = email.split("@")[0];
                                
                                // Set default role if not specified
                                if (role == null || role.isEmpty()) {
                                    role = "user";
                                }
                                
                                // Update UI with user data
                                profileName.setText(name);
                                profileUsername.setText(username);
                                roleTextView.setText("📌 Role: " + role);
                                emailTextView.setText("📧 Email: " + email);
                                
                                // Set bio text, or show default message if empty
                                if (bio != null && !bio.isEmpty()) {
                                    profileBio.setText(bio);
                                } else {
                                    profileBio.setText("No bio available");
                                }

                                // Load profile image if exists
                                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                    Picasso.get().load(profileImageUrl).into(profilePic);
                                }
                            }
                        } else {
                            Toast.makeText(Profile.this, "Error loading profile data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
}