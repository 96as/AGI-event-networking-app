package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class NotificationPage extends AppCompatActivity {
    private static final String TAG = "NotificationPage";
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        notificationRecyclerView = findViewById(R.id.postList);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup RecyclerView
        setupRecyclerView();

        // Header
        ImageView profile = findViewById(R.id.profile);
        ImageView addPostButton = findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = findViewById(R.id.viewAgendaButton);

        addPostButton.setOnClickListener(v -> startActivity(new Intent(NotificationPage.this, AddPostPage.class)));
        profile.setOnClickListener(v -> startActivity(new Intent(NotificationPage.this, Profile.class)));
        viewAgendaButton.setOnClickListener(v -> startActivity(new Intent(NotificationPage.this, ViewAgenda.class)));

        // Footer
        ImageView mainPageBtn = findViewById(R.id.logo);
        ImageView notificationBtn = findViewById(R.id.notification);
        ImageView directBtn = findViewById(R.id.message);

        mainPageBtn.setOnClickListener(v -> startActivity(new Intent(NotificationPage.this, GlobalChatPage.class)));
        notificationBtn.setOnClickListener(v -> startActivity(new Intent(NotificationPage.this, NotificationPage.class)));
        directBtn.setOnClickListener(v -> startActivity(new Intent(NotificationPage.this, AllMessagesDisplay.class)));
    }

    private void setupRecyclerView() {
        String userId = UserSession.getInstance().getUserId();
        Log.d(TAG, "Setting up RecyclerView for user: " + userId);
        
        if (userId == null) {
            Log.e(TAG, "User ID is null");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // First, let's do a direct query to check if we can read notifications
        db.collection("Notifications")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                Log.d(TAG, "Direct query found " + queryDocumentSnapshots.size() + " notifications");
                if (queryDocumentSnapshots.isEmpty()) {
                    Log.d(TAG, "No notifications found in direct query");
                    Toast.makeText(this, "No notifications found", Toast.LENGTH_SHORT).show();
                } else {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.d(TAG, "Found notification: " + document.getData());
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error in direct query: " + e.getMessage());
                Toast.makeText(this, "Error loading notifications", Toast.LENGTH_SHORT).show();
            });

        // Then set up the RecyclerView
        Query query = db.collection("Notifications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                .setQuery(query, Notification.class)
                .build();

        adapter = new NotificationAdapter(options);
        notificationRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Starting adapter listening");
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Stopping adapter listening");
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}