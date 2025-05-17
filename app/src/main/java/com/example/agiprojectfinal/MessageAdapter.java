package com.example.agiprojectfinal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageViewHolder> {
    private String currentUserId;

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, String currentUserId) {
        super(options);
        this.currentUserId = currentUserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message message) {
        Log.d("MessageAdapter", "Binding message: " + message.getContent() + ", sender: " + message.getSenderId());
        if (message.getSenderId().equals(currentUserId)) {
            // Message is from current user
            holder.rightMessageLayout.setVisibility(View.VISIBLE);
            holder.leftMessageLayout.setVisibility(View.GONE);
            holder.rightMessageTV.setText(message.getContent());
        } else {
            // Message is from other user
            holder.leftMessageLayout.setVisibility(View.VISIBLE);
            holder.rightMessageLayout.setVisibility(View.GONE);
            holder.leftMessageTV.setText(message.getContent());
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        View leftMessageLayout, rightMessageLayout;
        TextView leftMessageTV, rightMessageTV;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftMessageLayout = itemView.findViewById(R.id.leftMessageLayout);
            rightMessageLayout = itemView.findViewById(R.id.rightMessageLayout);
            leftMessageTV = itemView.findViewById(R.id.leftMessageTV);
            rightMessageTV = itemView.findViewById(R.id.rightMessageTV);
        }
    }
} 