package com.example.agiprojectfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        TextView register = findViewById(R.id.register_hyperlink);
        Button login = findViewById(R.id.loginBtn);
        emailInput = findViewById(R.id.editTextTextEmailAddress);
        passwordInput = findViewById(R.id.editTextTextPassword);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Get user role from Firestore
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserSession.getInstance().setUserId(user.getUid());
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                
                                db.collection("Users").document(user.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    // Store user role in a static variable for easy access
                                                    String role = document.getString("role");
                                                    UserSession.getInstance().setUserRole(role);
                                                    
                                                    // Check if user is admin based on role
                                                    boolean isAdmin = "admin".equalsIgnoreCase(role);
                                                    UserSession.getInstance().setIsAdmin(isAdmin);
                                                    
                                                    // Set user ID
                                                    UserSession.getInstance().setUserId(user.getUid());

                                                    // Navigate to main activity
                                                    startActivity(new Intent(Login.this, GlobalChatPage.class));
                                                    finish();
                                                }
                                            } else {
                                                Toast.makeText(Login.this, "Error getting user data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                            }
                            //else
                           // {
                             //   Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_LONG).show();
                            // }
                            else {
                                Toast.makeText(Login.this, "Authentication failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        });
    }
}