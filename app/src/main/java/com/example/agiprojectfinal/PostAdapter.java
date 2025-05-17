package com.example.agiprojectfinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> posts;
    private FirebaseFirestore db;
    private String userId;

    public PostAdapter(Context context, List<Post> posts, String userId) {
        super(context, R.layout.post_item, posts);
        this.context = context;
        this.posts = posts;
        this.userId = userId;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        }

        Post post = posts.get(position);

        TextView usernameText = convertView.findViewById(R.id.usernameText);
        TextView postDate = convertView.findViewById(R.id.postDate);
        TextView postContent = convertView.findViewById(R.id.postContent);
        TextView likesCount = convertView.findViewById(R.id.likesCount);
        TextView commentsCount = convertView.findViewById(R.id.commentsCount);
        ImageButton likeButton = convertView.findViewById(R.id.likeButton);
        ImageButton commentButton = convertView.findViewById(R.id.commentButton);

        usernameText.setText(post.getUsername());
        postContent.setText(post.getContent());
        likesCount.setText(String.valueOf(post.getLikes()));
        commentsCount.setText(String.valueOf(post.getComments()));

        // Format the timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(post.getTimestamp()));
        postDate.setText(dateStr);

        // Set up like button
        likeButton.setOnClickListener(v -> {
            DocumentReference postRef = db.collection("Posts").document(post.getPostId());
            postRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int currentLikes = document.getLong("likes").intValue();
                        postRef.update("likes", currentLikes + 1)
                            .addOnCompleteListener(updateTask -> {
                                if (updateTask.isSuccessful()) {
                                    post.setLikes(currentLikes + 1);
                                    likesCount.setText(String.valueOf(post.getLikes()));
                                }
                            });
                    }
                }
            });
        });

        // Set up comment button
        View finalConvertView = convertView;
        commentButton.setOnClickListener(v -> {
            // Toggle visibility of comment input section
            View commentInputLayout = finalConvertView.findViewById(R.id.commentInputLayout);
            EditText commentInput = finalConvertView.findViewById(R.id.commentInput);
            Button submitCommentButton = finalConvertView.findViewById(R.id.submitCommentButton);

            if (commentInputLayout.getVisibility() == View.VISIBLE) {
                commentInputLayout.setVisibility(View.GONE);
            } else {
                commentInputLayout.setVisibility(View.VISIBLE);
                commentInput.requestFocus();
            }

            // Set up submit button click listener
            submitCommentButton.setOnClickListener(submitV -> {
                String commentText = commentInput.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    createComment(post.getPostId(), commentText);
                    // Clear input and hide comment section
                    commentInput.setText("");
                    commentInputLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Add click listener to the entire post
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailsActivity.class);
            intent.putExtra("postId", post.getPostId());
            context.startActivity(intent);
        });

        return convertView;
    }

    private void createComment(String postId, String content) {
        // Get current user info
        String userId = UserSession.getInstance().getUserId();
        String username = UserSession.getInstance().getUsername();

        // Create comment data
        Map<String, Object> comment = new HashMap<>();
        comment.put("postId", postId);
        comment.put("userId", userId);
        comment.put("username", username);
        comment.put("content", content);
        comment.put("timestamp", System.currentTimeMillis());

        // Add to Firestore
        db.collection("Comments")
            .add(comment)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update post comment count
                    db.collection("Posts").document(postId)
                        .update("comments", FieldValue.increment(1))
                        .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(context, "Comment posted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error updating comment count", Toast.LENGTH_SHORT).show();
                            }
                        });
                } else {
                    Toast.makeText(context, "Error posting comment", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private Map<String, Object> createCommentData(String postId, String userId, String username, String content) {
        Map<String, Object> comment = new HashMap<>();
        comment.put("postId", postId);
        comment.put("userId", userId);
        comment.put("username", username);
        comment.put("content", content);
        comment.put("timestamp", System.currentTimeMillis());
        return comment;
    }
} 