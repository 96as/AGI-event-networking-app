package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import java.util.Map;

public class GlobalChatPage extends AppCompatActivity {

    private ListView postListView;
    private FirebaseFirestore db;
    private List<Post> posts;
    private PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_chat_page);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        postListView = findViewById(R.id.postList);
        posts = new ArrayList<>();
        String currentUserId = UserSession.getInstance().getUserId();
        adapter = new PostAdapter(this, posts, currentUserId);
        postListView.setAdapter(adapter);

        // Load posts
        loadPosts();

        // Header
        ImageView profile = (ImageView) findViewById(R.id.profile);
        ImageView addPostButton = (ImageView) findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = (ImageView) findViewById(R.id.viewAgendaButton);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GlobalChatPage.this, AddPostPage.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GlobalChatPage.this, Profile.class));
            }
        });

        viewAgendaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GlobalChatPage.this, ViewAgenda.class));
            }
        });

        // Footer
        ImageView mainPageBtn = (ImageView) findViewById(R.id.logo);
        ImageView notificationBtn = (ImageView) findViewById(R.id.notification);
        ImageView directBtn = (ImageView) findViewById(R.id.message);

        mainPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GlobalChatPage.this, GlobalChatPage.class));
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GlobalChatPage.this, NotificationPage.class));
            }
        });

        directBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GlobalChatPage.this, AllMessagesDisplay.class));
            }
        });
    }

    private void loadPosts() {
        db.collection("Posts")
            .orderBy("timestamp")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        posts.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Post post = new Post(
                                document.getId(),
                                document.getString("userId"),
                                document.getString("username"),
                                document.getString("content"),
                                document.getLong("timestamp")
                            );
                            post.setLikes(document.getLong("likes").intValue());
                            post.setComments(document.getLong("comments").intValue());
                            
                            // Get userLikes map
                            Map<String, Boolean> userLikes = (Map<String, Boolean>) document.get("userLikes");
                            if (userLikes != null) {
                                post.setUserLikes(userLikes);
                            }
                            
                            posts.add(post);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(GlobalChatPage.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPosts(); // Reload posts when returning to this activity
    }
}