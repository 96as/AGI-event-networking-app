package com.example.agiprojectfinal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationAdapter extends FirestoreRecyclerAdapter<Notification, NotificationAdapter.NotificationViewHolder> {
    private static final String TAG = "NotificationAdapter";

    public NotificationAdapter(@NonNull FirestoreRecyclerOptions<Notification> options) {
        super(options);
        Log.d(TAG, "Adapter created with " + options.getSnapshots().size() + " items");
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull Notification model) {
        Log.d(TAG, "Binding notification at position " + position + ": " + model.getTitle());
        holder.titleText.setText(model.getTitle());
        holder.messageText.setText(model.getMessage());
        
        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(model.getTimestamp()));
        holder.timeText.setText(formattedDate);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating new ViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        Log.d(TAG, "Data changed. New size: " + getItemCount());
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView messageText;
        TextView timeText;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.notificationTitle);
            messageText = itemView.findViewById(R.id.notificationMessage);
            timeText = itemView.findViewById(R.id.notificationTime);
        }
    }
} 