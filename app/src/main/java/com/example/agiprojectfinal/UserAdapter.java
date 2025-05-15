package com.example.agiprojectfinal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.Objects;

public class UserAdapter extends FirestoreRecyclerAdapter<UserModel, UserAdapter.UserModelViewHolder>{

    Context context;
    public UserAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_user_direct_display, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        String name = model.getName();
        String uid = getSnapshots().getSnapshot(position).getId();


        holder.user.setText(model.getName());

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("Messages")
                .whereIn("from", Arrays.asList(currentUserId, uid))
                .whereIn("to", Arrays.asList(currentUserId, uid))
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("text");
                        holder.lastText.setText(lastMessage);
                    } else {
                        holder.lastText.setText("No messages yet");
                    }
                });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DirectMessaging.class);
            intent.putExtra("name", model.getName());
            intent.putExtra("Id", model.getUserID());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    class UserModelViewHolder extends RecyclerView.ViewHolder{

        TextView user, lastText;
        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.userDisplay);
            lastText = itemView.findViewById(R.id.userLastText);
        }
    }
}
