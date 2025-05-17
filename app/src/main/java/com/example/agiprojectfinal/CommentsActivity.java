package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class CommentsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private RecyclerView commentsRecyclerView;
    private EditText commentInput;
    private CommentAdapter commentAdapter;
    private Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get post data from intent
        Intent intent = getIntent();
        currentPost = new Post(
            intent.getStringExtra("postId"),
            intent.getStringExtra("userId"),
            intent.getStringExtra("username"),
            intent.getStringExtra("content"),
            intent.getLongExtra("timestamp", 0)
        );

        // Initialize views
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentInput = findViewById(R.id.commentInput);

        // Display post content
        TextView postUsername = findViewById(R.id.postUsername);
        TextView postContent = findViewById(R.id.postContent);
        TextView postDate = findViewById(R.id.postDate);

        postUsername.setText(currentPost.getUsername());
        postContent.setText(currentPost.getContent());
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(currentPost.getTimestamp()));
        postDate.setText(dateStr);

        // Set up comment submission
        findViewById(R.id.submitCommentButton).setOnClickListener(v -> submitComment());

        // Set up comments RecyclerView
        setupCommentsRecyclerView();

        // Set up header navigation
        ImageView profile = findViewById(R.id.profile);
        ImageView addPostButton = findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = findViewById(R.id.viewAgendaButton);

        profile.setOnClickListener(v -> startActivity(new Intent(CommentsActivity.this, Profile.class)));
        addPostButton.setOnClickListener(v -> startActivity(new Intent(CommentsActivity.this, AddPostPage.class)));
        viewAgendaButton.setOnClickListener(v -> startActivity(new Intent(CommentsActivity.this, ViewAgenda.class)));
    }

    private void setupCommentsRecyclerView() {
        Query query = db.collection("Comments")
                .whereEqualTo("postId", currentPost.getPostId())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        commentAdapter = new CommentAdapter(options);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);
    }

    private void submitComment() {
        String content = commentInput.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = UserSession.getInstance().getUserId();
        String username = UserSession.getInstance().getUsername();

        Map<String, Object> comment = new HashMap<>();
        comment.put("postId", currentPost.getPostId());
        comment.put("userId", userId);
        comment.put("username", username);
        comment.put("content", content);
        comment.put("timestamp", System.currentTimeMillis());

        db.collection("Comments")
            .add(comment)
            .addOnSuccessListener(documentReference -> {
                commentInput.setText("");
                Toast.makeText(this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                
                // Update comment count in the post
                db.collection("Posts").document(currentPost.getPostId())
                    .update("comments", com.google.firebase.firestore.FieldValue.increment(1));
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error posting comment", Toast.LENGTH_SHORT).show();
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