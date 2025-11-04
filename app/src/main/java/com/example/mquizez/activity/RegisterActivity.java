package com.example.mquizez.activity;



import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mquizez.R;
import com.example.mquizez.model.User;
import com.example.mquizez.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword;
    private Button btnRegister;
    private UserRepository userRepository;
    private TextView btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRepository = new UserRepository(this);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin= findViewById(R.id.tvHaveAccount);
        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString();
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                if (userRepository.emailExists(email)) {
                    runOnUiThread(() -> Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show());
                } else {
                    userRepository.registerUser(new User(username, email, password));
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Register successful", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại màn hình login
                    });
                }
            }).start();
        });
        btnBackToLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }
}

