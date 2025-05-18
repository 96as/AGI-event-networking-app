package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.annotations.NonNull;

public class AddPostPage extends AppCompatActivity {

    private static final String TAG = "AddPostPage";
    private EditText postText;
    private Button addPostButton;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_page);

        // Set Add Agenda button visibility based on admin status
        Button addAgendaButton = findViewById(R.id.addAgenda);
        boolean isAdmin = UserSession.getInstance().isAdmin();
        addAgendaButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        // Add click listener for Add Agenda button
        addAgendaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostPage.this, AddAgenda.class));
            }
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        postText = findViewById(R.id.postText);
        Button submitPostButton = findViewById(R.id.submitPostButton);

        // Set up post button
        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = postText.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(AddPostPage.this, "Please enter some text", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get current user info
                String userId = UserSession.getInstance().getUserId();
                String username = UserSession.getInstance().getUsername();

                // Debug log
                Log.d(TAG, "Attempting to create post. UserID: " + userId + ", Username: " + username);

                // Create post data
                Map<String, Object> post = new HashMap<>();
                post.put("userId", userId);
                post.put("username", username);
                post.put("content", content);
                post.put("timestamp", System.currentTimeMillis());
                post.put("likes", 0);
                post.put("comments", 0);
                post.put("userLikes", new HashMap<String, Boolean>()); // Initialize empty userLikes map

                // Add post to Firestore
                db.collection("Posts")
                        .add(post)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Post added successfully with ID: " + documentReference.getId());
                                // Create notifications for all users
                                createNotification(content);
                                Toast.makeText(AddPostPage.this, "Post added successfully", Toast.LENGTH_SHORT).show();
                                postText.setText("");
                                startActivity(new Intent(AddPostPage.this, GlobalChatPage.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error adding post", e);
                                Toast.makeText(AddPostPage.this, "Error adding post: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // Header
        ImageView profile = (ImageView) findViewById(R.id.profile);
        ImageView addPostButton = (ImageView) findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = (ImageView) findViewById(R.id.viewAgendaButton);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostPage.this, AddPostPage.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostPage.this, Profile.class));
            }
        });

        viewAgendaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostPage.this, ViewAgenda.class));
            }
        });


        // Footer
        ImageView mainPageBtn = findViewById(R.id.logo);
        ImageView notificationBtn = findViewById(R.id.notification);
        ImageView directBtn = findViewById(R.id.message);

        mainPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostPage.this, GlobalChatPage.class));
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostPage.this, NotificationPage.class));
            }
        });

        directBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddPostPage.this, AllMessagesDisplay.class));
            }
        });
    }

    private void createNotification(String postContent) {
        Log.d("AddPostPage", "Creating notification for post: " + postContent);

        // First, let's create a test notification for the current user
        String currentUserId = UserSession.getInstance().getUserId();
        Map<String, Object> testNotification = new HashMap<>();
        testNotification.put("title", "Test Notification");
        testNotification.put("message", "This is a test notification: " + postContent);
        testNotification.put("type", "Post");
        testNotification.put("userId", currentUserId);
        testNotification.put("timestamp", System.currentTimeMillis());
        testNotification.put("read", false);

        db.collection("Notifications")
            .add(testNotification)
            .addOnSuccessListener(documentReference -> {
                Log.d("AddPostPage", "Test notification created successfully");
                Toast.makeText(AddPostPage.this, "Test notification created", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> {
                Log.e("AddPostPage", "Error creating test notification", e);
            });

        // Then proceed with creating notifications for all users
        db.collection("Users")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("AddPostPage", "Found " + task.getResult().size() + " users to notify");
                        // Create notification for each user
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getId();
                            Log.d("AddPostPage", "Creating notification for user: " + userId);

                            Map<String, Object> notification = new HashMap<>();
                            notification.put("title", "New Post");
                            notification.put("message", "A new post has been added: " + postContent);
                            notification.put("type", "Post");
                            notification.put("userId", userId);
                            notification.put("timestamp", System.currentTimeMillis());
                            notification.put("read", false);

                            db.collection("Notifications")
                                .add(notification)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("AddPostPage", "Notification created successfully for user: " + userId);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("AddPostPage", "Error creating notification for user: " + userId, e);
                                });
                        }
                    } else {
                        Log.e("AddPostPage", "Error getting users: ", task.getException());
                    }
                }
            });
    }
}