package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;


public class AddPostPage extends AppCompatActivity {

    private EditText postText;
    private Button addPostButton;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post_page);

        // Set Add Agenda button visibility based on admin status
        Button addAgendaButton = (Button) findViewById(R.id.addAgenda);
        boolean isAdmin = UserSession.getInstance().isAdmin();
        addAgendaButton.setVisibility(isAdmin ? View.VISIBLE : View.GONE);


        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        EditText postText = (EditText) findViewById(R.id.postText);
        Button submitPostButton = (Button) findViewById(R.id.submitPostButton);

        // Set up post button
        submitPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = postText.getText().toString().trim();
                if (content.isEmpty()) {
                    Toast.makeText(AddPostPage.this, "Please enter some text", Toast.LENGTH_SHORT).show();
                    return;
                }

                 //Get current user info
                String userId = UserSession.getInstance().getUserId();
                String username = UserSession.getInstance().getUsername();

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
                        .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentReference>() {
                            @Override
                            public void onComplete(Task<com.google.firebase.firestore.DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddPostPage.this, "Post added successfully", Toast.LENGTH_SHORT).show();
                                    // Clear the input and return to global chat
                                    postText.setText("");
                                    startActivity(new Intent(AddPostPage.this, GlobalChatPage.class));
                                    finish();
                                } else {
                                    Toast.makeText(AddPostPage.this, "Error adding post", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Footer
        ImageView mainPageBtn = (ImageView) findViewById(R.id.logo);
        ImageView notificationBtn = (ImageView) findViewById(R.id.notification);
        ImageView directBtn = (ImageView) findViewById(R.id.message);

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
}