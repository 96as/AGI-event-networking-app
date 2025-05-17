package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ListView commentsListView;
    private EditText commentInput;
    private List<Comment> comments;
    private CommentAdapter adapter;
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
        commentsListView = findViewById(R.id.commentsList);
        commentInput = findViewById(R.id.commentInput);
        comments = new ArrayList<>();
        adapter = new CommentAdapter(this, comments);
        commentsListView.setAdapter(adapter);

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

        // Load comments
        loadComments();

        // Set up header navigation
        ImageView profile = findViewById(R.id.profile);
        ImageView addPostButton = findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = findViewById(R.id.viewAgendaButton);

        profile.setOnClickListener(v -> startActivity(new Intent(CommentsActivity.this, Profile.class)));
        addPostButton.setOnClickListener(v -> startActivity(new Intent(CommentsActivity.this, AddPostPage.class)));
        viewAgendaButton.setOnClickListener(v -> startActivity(new Intent(CommentsActivity.this, ViewAgenda.class)));
    }

    private void loadComments() {
        db.collection("Comments")
            .whereEqualTo("postId", currentPost.getPostId())
            .orderBy("timestamp")
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    comments.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Comment comment = new Comment(
                            document.getId(),
                            document.getString("postId"),
                            document.getString("userId"),
                            document.getString("username"),
                            document.getString("content"),
                            document.getLong("timestamp")
                        );
                        comments.add(comment);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CommentsActivity.this, "Error loading comments", Toast.LENGTH_SHORT).show();
                }
            });
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
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update post comment count
                    db.collection("Posts").document(currentPost.getPostId())
                        .update("comments", currentPost.getComments() + 1);
                    
                    // Clear input and reload comments
                    commentInput.setText("");
                    loadComments();
                } else {
                    Toast.makeText(CommentsActivity.this, "Error posting comment", Toast.LENGTH_SHORT).show();
                }
            });
    }
} 