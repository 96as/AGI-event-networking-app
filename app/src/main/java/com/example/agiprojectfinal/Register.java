package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    UserModel userModel;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String name_value, email_value, pass_value, role_value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button reg = (Button) findViewById(R.id.register);
        EditText name = (EditText) findViewById(R.id.register_name);
        EditText email = (EditText) findViewById(R.id.register_email);
        EditText password = (EditText) findViewById(R.id.register_password);


        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString().isEmpty() ||
                        email.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty())
                {
                    Toast.makeText(Register.this,"Empty fields are required!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    name_value = name.getText().toString();
                    email_value = email.getText().toString();
                    pass_value = password.getText().toString();
                    addUser(name_value, email_value, pass_value, role_value);

                }

            }
        });
    }
    public void addUser(String user_name, String user_email, String user_pass, String user_role){
        mAuth.createUserWithEmailAndPassword(user_email,
                user_pass).addOnCompleteListener(Register.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            user = mAuth.getCurrentUser();
                            String uid = user.getUid();

                            // Store extra user info in Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Map<String, Object> userInfo = new HashMap<>();
                            userInfo.put("name", user_name);
                            userInfo.put("email", user_email);
                            userInfo.put("role", user_role);

                            userModel = new UserModel(user_name, user_email, Timestamp.now(), uid);

                            db.collection("Users").document(uid)
                                    .set(userInfo)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(Register.this,
                                                "User added to AGIS Chat: " + user_email,
                                                Toast.LENGTH_LONG).show();

                                        startActivity(new Intent(Register.this, Login.class));
                                        finish();

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(Register.this, "User registered but failed to save role: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    });
                        }
                        else{
                            Toast.makeText(Register.this, "User already exists!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}