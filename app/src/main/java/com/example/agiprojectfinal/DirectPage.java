package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectPage extends AppCompatActivity {
    private FirebaseFirestore db;
    private String currentUserId;
    private String otherUserId;
    private String chatRoomId;
    private EditText messageInput;
    private RecyclerView chatRecyclerView;
    private MessageAdapter adapter;
    private TextView userNameText;
    private FrameLayout sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_page);
        UserSession.getInstance().init(this);




        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        currentUserId = UserSession.getInstance().getUserId();
        // Get other user's ID from intent
        Intent intent = getIntent();
        otherUserId = intent.getStringExtra("userId");
        // Initialize views
        messageInput = findViewById(R.id.inputMessage);
        chatRecyclerView = findViewById(R.id.chatRV);
        userNameText = findViewById(R.id.textName);
        // Set chat partner's name from Firestore
        setChatPartnerName();
        // Setup RecyclerView
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Find or create chat room
        findOrCreateChatRoom();
        // Setup send message button
        sendButton = findViewById(R.id.layoutSend);
        sendButton.setEnabled(false); // Disable until chatRoomId is ready
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        // Setup back button
        ImageView backButton = findViewById(R.id.imageBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // Setup header and footer navigation
        setupNavigation();
    }

    private void setChatPartnerName() {
        FirebaseFirestore.getInstance()
            .collection("Users")
            .document(otherUserId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    userNameText.setText(name != null ? name : "User");
                } else {
                    userNameText.setText("User");
                }
            })
            .addOnFailureListener(e -> userNameText.setText("User"));
    }

    private void findOrCreateChatRoom() {
        // Query to find existing chat room
        db.collection("chatRooms")
            .whereArrayContains("participants", currentUserId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        boolean chatRoomFound = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> participants = (List<String>) document.get("participants");
                            if (participants.contains(otherUserId)) {
                                chatRoomId = document.getId();
                                chatRoomFound = true;
                                setupMessageListener();
                                sendButton.setEnabled(true); // Enable send button
                                break;
                            }
                        }
                        
                        if (!chatRoomFound) {
                            createChatRoom();
                        }
                    }
                }
            });
    }

    private void createChatRoom() {
        List<String> participants = Arrays.asList(currentUserId, otherUserId);
        ChatRoom chatRoom = new ChatRoom(null, participants, System.currentTimeMillis());
        db.collection("chatRooms")
            .add(chatRoom)
            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        chatRoomId = task.getResult().getId();
                        // Update chatRoomId field in Firestore
                        db.collection("chatRooms").document(chatRoomId)
                            .update("chatRoomId", chatRoomId);
                        setupMessageListener();
                        sendButton.setEnabled(true); // Enable send button
                    } else {
                        Toast.makeText(DirectPage.this, "Error creating chat room", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void setupMessageListener() {
        if (chatRoomId == null) {
            Toast.makeText(this, "Chat room not ready yet. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }
        Query query = db.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING);
        Log.d("DirectPage", "Setting up adapter for chatRoomId: " + chatRoomId);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message.class)
            .build();
        adapter = new MessageAdapter(options, currentUserId);
        chatRecyclerView.setAdapter(adapter);
        chatRecyclerView.setVisibility(View.VISIBLE); // Ensure chatRV is visible
        adapter.startListening();
    }

    private void sendMessage() {
        if (chatRoomId == null) {
            Toast.makeText(this, "Chat room not ready yet. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }
        String messageContent = messageInput.getText().toString().trim();
        if (messageContent.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        Message message = new Message(
            null,
            currentUserId,
            messageContent,
            System.currentTimeMillis()
        );
        db.collection("chatRooms")
            .document(chatRoomId)
            .collection("messages")
            .add(message)
            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(Task<DocumentReference> task) {
                    if (task.isSuccessful()) {
                        String messageId = task.getResult().getId();
                        // Update messageId field in Firestore
                        db.collection("chatRooms").document(chatRoomId)
                            .collection("messages").document(messageId)
                            .update("messageId", messageId);
                        messageInput.setText("");
                        // Update last message timestamp in chat room
                        db.collection("chatRooms")
                            .document(chatRoomId)
                            .update("lastMessageTimestamp", System.currentTimeMillis());
                    } else {
                        Toast.makeText(DirectPage.this, "Error sending message", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void setupNavigation() {
        // Header
        ImageView profile = (ImageView) findViewById(R.id.profile);
        ImageView addPostButton = (ImageView) findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = (ImageView) findViewById(R.id.viewAgendaButton);

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectPage.this, AddPostPage.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectPage.this, Profile.class));
            }
        });

        viewAgendaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectPage.this, ViewAgenda.class));
            }
        });

        // Footer
        ImageView mainPageBtn = (ImageView) findViewById(R.id.logo);
        ImageView notificationBtn = (ImageView) findViewById(R.id.notification);
        ImageView directBtn = (ImageView) findViewById(R.id.message);

        mainPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectPage.this, GlobalChatPage.class));
            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectPage.this, NotificationPage.class));
            }
        });

        directBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DirectPage.this, AllMessagesDisplay.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}