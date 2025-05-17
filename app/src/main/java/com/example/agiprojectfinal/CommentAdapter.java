package com.example.agiprojectfinal;

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

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentViewHolder> {

    public CommentAdapter(@NonNull FirestoreRecyclerOptions<Comment> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment comment) {
        holder.usernameView.setText(comment.getUsername());
        holder.contentView.setText(comment.getContent());
        
        // Format timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault());
        String formattedDate = sdf.format(new Date(comment.getTimestamp()));
        holder.timestampView.setText(formattedDate);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameView;
        TextView contentView;
        TextView timestampView;

        CommentViewHolder(View itemView) {
            super(itemView);
            usernameView = itemView.findViewById(R.id.commentUsername);
            contentView = itemView.findViewById(R.id.commentContent);
            timestampView = itemView.findViewById(R.id.commentTimestamp);
        }
    }
} 