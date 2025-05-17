package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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

public class ViewAgenda extends AppCompatActivity {

    private ListView agendaListView;
    private FirebaseFirestore db;
    private List<AgendaItem> agendaItems;
    private AgendaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_agenda);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        agendaListView = findViewById(R.id.agendaListView);
        agendaItems = new ArrayList<>();
        adapter = new AgendaAdapter(this, agendaItems);
        agendaListView.setAdapter(adapter);

        // Set up header navigation
        ImageView profile = findViewById(R.id.profile);
        ImageView addPostButton = findViewById(R.id.addPostButton);
        ImageView viewAgendaButton = findViewById(R.id.viewAgendaButton);

        profile.setOnClickListener(v -> startActivity(new Intent(ViewAgenda.this, Profile.class)));
        addPostButton.setOnClickListener(v -> startActivity(new Intent(ViewAgenda.this, AddPostPage.class)));
        viewAgendaButton.setOnClickListener(v -> startActivity(new Intent(ViewAgenda.this, ViewAgenda.class)));

        // Load agenda items
        loadAgendaItems();
    }

    private void loadAgendaItems() {
        db.collection("Agenda")
            .orderBy("createdAt")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        agendaItems.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            AgendaItem item = new AgendaItem(
                                document.getString("title"),
                                document.getString("description"),
                                document.getString("date") + " - " + document.getString("time")
                            );
                            agendaItems.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ViewAgenda.this, "Error loading agenda items", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    // Inner class to represent an agenda item
    private static class AgendaItem {
        private String title;
        private String description;
        private String dateTime;

        public AgendaItem(String title, String description, String dateTime) {
            this.title = title;
            this.description = description;
            this.dateTime = dateTime;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getDateTime() { return dateTime; }
    }

    // Custom adapter for agenda items
    private class AgendaAdapter extends ArrayAdapter<AgendaItem> {
        public AgendaAdapter(ViewAgenda context, List<AgendaItem> items) {
            super(context, R.layout.activity_tv_for_lv, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_tv_for_lv, parent, false);
            }

            AgendaItem item = getItem(position);

            TextView titleView = convertView.findViewById(R.id.agendaTitleTV);
            TextView descriptionView = convertView.findViewById(R.id.agendaDescriptionTV);
            TextView dateTimeView = convertView.findViewById(R.id.agendaDateTimeTV);

            titleView.setText(item.getTitle());
            descriptionView.setText(item.getDescription());
            dateTimeView.setText(item.getDateTime());

            return convertView;
        }
    }
}