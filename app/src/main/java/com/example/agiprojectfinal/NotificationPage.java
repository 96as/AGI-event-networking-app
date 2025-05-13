package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class NotificationPage extends AppCompatActivity {

    private ListView notificationListView;
    private FirebaseFirestore db;
    private List<NotificationItem> notifications;
    private ArrayAdapter<NotificationItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        notificationListView = findViewById(R.id.postList);
        notifications = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications);
        notificationListView.setAdapter(adapter);

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
        directBtn.setOnClickListener(v -> startActivity(new Intent(NotificationPage.this, DirectMessaging.class)));

        // Load notifications
        loadNotifications();
    }

    private void loadNotifications() {
        String userId = UserSession.getInstance().getUserId();
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Notifications")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        notifications.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            NotificationItem item = new NotificationItem(
                                document.getString("title"),
                                document.getString("message"),
                                document.getLong("timestamp")
                            );
                            notifications.add(item);

                            // Mark notification as read
                            document.getReference().update("read", true);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(NotificationPage.this, "Error loading notifications", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    // Inner class to represent a notification
    private static class NotificationItem {
        private String title;
        private String message;
        private long timestamp;

        public NotificationItem(String title, String message, long timestamp) {
            this.title = title;
            this.message = message;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return title + "\n" + message;
        }
    }
}