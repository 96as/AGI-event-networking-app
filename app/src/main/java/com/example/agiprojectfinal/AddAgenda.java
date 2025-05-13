package com.example.agiprojectfinal;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AddAgenda extends AppCompatActivity {

    private EditText titleInput, descriptionInput, dateInput, timeInput;
    private Button saveButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_agenda);

        // Check if user is admin
        if (!UserSession.getInstance().isAdmin()) {
            Toast.makeText(this, "Only admins can add agenda items", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        titleInput = findViewById(R.id.agendaTitle);
        descriptionInput = findViewById(R.id.agendaDescription);
        dateInput = findViewById(R.id.agendaDate);
        timeInput = findViewById(R.id.agendaTime);
        saveButton = findViewById(R.id.saveAgenda);

        // Set up header navigation
        ImageView profile = findViewById(R.id.profile);
        ImageView addPostButton = findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = findViewById(R.id.viewAgendaButton);

        profile.setOnClickListener(v -> startActivity(new Intent(AddAgenda.this, Profile.class)));
        addPostButton.setOnClickListener(v -> startActivity(new Intent(AddAgenda.this, AddPostPage.class)));
        viewAgendaButton.setOnClickListener(v -> startActivity(new Intent(AddAgenda.this, ViewAgenda.class)));

        // Set up save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAgenda();
            }
        });
    }

    private void saveAgenda() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create agenda item
        Map<String, Object> agendaItem = new HashMap<>();
        agendaItem.put("title", title);
        agendaItem.put("description", description);
        agendaItem.put("date", date);
        agendaItem.put("time", time);
        agendaItem.put("createdBy", UserSession.getInstance().getUserId());
        agendaItem.put("createdAt", System.currentTimeMillis());

        // Save to Firestore
        db.collection("Agenda")
            .add(agendaItem)
            .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.DocumentReference>() {
                @Override
                public void onComplete(Task<com.google.firebase.firestore.DocumentReference> task) {
                    if (task.isSuccessful()) {
                        // Create notification for all users
                        createNotification(title);
                        Toast.makeText(AddAgenda.this, "Agenda item added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddAgenda.this, "Error adding agenda item", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void createNotification(String agendaTitle) {
        // Get all users
        db.collection("Users")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        // Create notification for each user
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getId();
                            
                            Map<String, Object> notification = new HashMap<>();
                            notification.put("title", "New Agenda Item");
                            notification.put("message", "A new agenda item has been added: " + agendaTitle);
                            notification.put("userId", userId);
                            notification.put("timestamp", System.currentTimeMillis());
                            notification.put("read", false);

                            db.collection("Notifications")
                                .add(notification);
                        }
                    }
                }
            });
    }
}