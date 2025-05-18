package com.example.agiprojectfinal;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class activity_editProfile extends AppCompatActivity {

    private EditText profileName, profileUsername, profileBio;
    private TextView profileRole, emailTextView;
    private Button editProfileButton;
    private ImageView profilePic;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri selectedImageUri;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;



    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                if (data != null && data.getData() != null) {
                    selectedImageUri = data.getData();
                    profilePic.setImageURI(selectedImageUri);
                }
            }
        }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Header
        ImageView profile = (ImageView) findViewById(R.id.profile);
        ImageView addPostButton = (ImageView) findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = (ImageView) findViewById(R.id.viewAgendaButton);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_editProfile.this, AddPostPage.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_editProfile.this, Profile.class));
            }
        });

        viewAgendaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_editProfile.this, ViewAgenda.class));
            }
        });

        // Footer
        ImageView mainPageBtn = (ImageView) findViewById(R.id.logo);
        ImageView notificationBtn = (ImageView) findViewById(R.id.notification);
        ImageView directBtn = (ImageView) findViewById(R.id.message);

        mainPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_editProfile.this, GlobalChatPage.class));
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_editProfile.this, NotificationPage.class));
            }
        });

        directBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_editProfile.this, AllMessagesDisplay.class));
            }
        });


        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize views
        profileName = findViewById(R.id.profileName);
        profileUsername = findViewById(R.id.profileUsername);
        profileBio = findViewById(R.id.profileBio);
        profileRole = findViewById(R.id.profileRole);
        emailTextView = findViewById(R.id.emailTextView);
        editProfileButton = findViewById(R.id.editProfileButton);
        profilePic = findViewById(R.id.profilePic);

        // Load current user data
        loadUserData();

        // Set up profile picture click listener
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });

        // Set up save button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above, use READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        } else {
            // For Android 12 and below, use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                openImagePicker();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImage.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied to access images. Please grant permission in Settings.", Toast.LENGTH_LONG).show();
            }
        }
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
                                profileBio.setText(bio != null ? bio : "");
                                profileRole.setText(role);
                                emailTextView.setText("📧 Email: " + email);

                                // Load profile image if exists
                                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                    Picasso.get().load(profileImageUrl).into(profilePic);
                                }
                            }
                        } else {
                            Toast.makeText(activity_editProfile.this, "Error loading profile data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }

    private void saveUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            
            // Get updated values
            String newName = profileName.getText().toString().trim();
            String newUsername = profileUsername.getText().toString().trim();
            String newBio = profileBio.getText().toString().trim();
            
            // Validate input
            if (newName.isEmpty() || newUsername.isEmpty()) {
                Toast.makeText(this, "Name and username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // If an image was selected, upload it first
            if (selectedImageUri != null) {
                uploadImageAndSaveData(userId, newName, newBio);
            } else {
                // If no new image, just save the other data
                saveUserDataToFirestore(userId, newName, newBio, null);
            }
        }
    }

    private void uploadImageAndSaveData(String userId, String name, String bio) {
        StorageReference storageRef = storage.getReference();
        String imageId = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child("profile_images/" + userId + "/" + imageId);

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            // Save user data with the image URL
                            saveUserDataToFirestore(userId, name, bio, downloadUri.toString());
                        }
                    });
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity_editProfile.this, 
                        "Failed to upload image: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void saveUserDataToFirestore(String userId, String name, String bio, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("bio", bio);
        if (imageUrl != null) {
            updates.put("profileImageUrl", imageUrl);
        }
        
        db.collection("Users").document(userId)
            .update(updates)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(activity_editProfile.this, 
                            "Profile updated successfully", 
                            Toast.LENGTH_SHORT).show();
                        finish(); // Return to profile page
                    } else {
                        Toast.makeText(activity_editProfile.this, 
                            "Error updating profile: " + task.getException().getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}