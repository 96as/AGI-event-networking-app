package com.example.agiprojectfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends ArrayAdapter<Comment> {
    private Context context;
    private List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        super(context, R.layout.comment_item, comments);
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        }

        Comment comment = comments.get(position);

        TextView usernameText = convertView.findViewById(R.id.commentUsername);
        TextView commentDate = convertView.findViewById(R.id.commentDate);
        TextView commentContent = convertView.findViewById(R.id.commentContent);

        usernameText.setText(comment.getUsername());
        commentContent.setText(comment.getContent());

        // Format the timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(comment.getTimestamp()));
        commentDate.setText(dateStr);

        return convertView;
    }
} 