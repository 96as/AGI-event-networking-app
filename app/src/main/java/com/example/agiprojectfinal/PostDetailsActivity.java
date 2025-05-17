package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PostDetailsActivity";
    
    private FirebaseFirestore db;
    private CommentAdapter commentAdapter;
    private String postId;
    private EditText commentInput;
    private ImageButton sendCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get post ID from intent
        postId = getIntent().getStringExtra("postId");
        if (postId == null) {
            Toast.makeText(this, "Error: Post not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        commentInput = findViewById(R.id.commentInput);
        sendCommentButton = findViewById(R.id.sendCommentButton);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView profileButton = findViewById(R.id.profile);

        // Set up navigation
        backButton.setOnClickListener(v -> finish());
        profileButton.setOnClickListener(v -> 
            startActivity(new Intent(PostDetailsActivity.this, Profile.class)));

        // Load post details
        loadPostDetails();

        // Set up comments RecyclerView
        setupCommentsRecyclerView();

        // Set up comment sending
        sendCommentButton.setOnClickListener(v -> sendComment());
    }

    private void loadPostDetails() {
        db.collection("Posts").document(postId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String username = documentSnapshot.getString("username");
                    String content = documentSnapshot.getString("content");
                    Long timestamp = documentSnapshot.getLong("timestamp");

                    TextView postUsername = findViewById(R.id.postUsername);
                    TextView postContent = findViewById(R.id.postContent);
                    TextView postTimestamp = findViewById(R.id.postTimestamp);

                    postUsername.setText(username);
                    postContent.setText(content);

                    if (timestamp != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault());
                        String formattedDate = sdf.format(new Date(timestamp));
                        postTimestamp.setText(formattedDate);
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading post details", e);
                Toast.makeText(this, "Error loading post details", Toast.LENGTH_SHORT).show();
            });
    }

    private void setupCommentsRecyclerView() {
        Query query = db.collection("Comments")
                .whereEqualTo("postId", postId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        commentAdapter = new CommentAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);
    }

    private void sendComment() {
        String content = commentInput.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = UserSession.getInstance().getUserId();
        String username = UserSession.getInstance().getUsername();

        Map<String, Object> comment = new HashMap<>();
        comment.put("postId", postId);
        comment.put("userId", userId);
        comment.put("username", username);
        comment.put("content", content);
        comment.put("timestamp", System.currentTimeMillis());

        db.collection("Comments")
            .add(comment)
            .addOnSuccessListener(documentReference -> {
                commentInput.setText("");
                Toast.makeText(PostDetailsActivity.this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                
                // Update comment count in the post
                db.collection("Posts").document(postId)
                    .update("comments", com.google.firebase.firestore.FieldValue.increment(1));
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error adding comment", e);
                Toast.makeText(PostDetailsActivity.this, "Error adding comment", Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (commentAdapter != null) {
            commentAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (commentAdapter != null) {
            commentAdapter.stopListening();
        }
    }
} 