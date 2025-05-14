package com.example.agiprojectfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends ArrayAdapter<Post> {
    private Context context;
    private List<Post> posts;
    private FirebaseFirestore db;
    private String currentUserId;

    public PostAdapter(Context context, List<Post> posts, String currentUserId) {
        super(context, R.layout.post_item, posts);
        this.context = context;
        this.posts = posts;
        this.currentUserId = currentUserId;
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
        commentButton.setOnClickListener(v -> {
            // We'll implement this in the next step

        });

        return convertView;
    }
} 