package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DirectMessaging extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_messaging);

        UserModel userModel = new UserModel();
        Intent intent = new Intent();
        userModel.setName(intent.getStringExtra("name"));
        userModel.setUserID(intent.getStringExtra("Id"));
    }
}