package com.example.mquizez.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.mquizez.R;
import com.example.mquizez.model.User;
import com.example.mquizez.repository.UserRepository;

public class ProfileActivity extends AppCompatActivity {

    private EditText edtUsername, edtEmail, edtPassword;
    private Button btnSave, btnBack;
    private UserRepository userRepository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        userRepository = new UserRepository(this);

        int userId = getSharedPreferences("UserSession", MODE_PRIVATE)
                .getInt("user_id", -1);

        if (userId != -1) {
            loadUserInfo(userId);
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
        }


        btnSave.setOnClickListener(v -> saveChanges());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserInfo(int userId) {
        new Thread(() -> {
            User user = userRepository.getUserById(userId);
            runOnUiThread(() -> {
                if (user != null) {
                    currentUser = user;
                    edtUsername.setText(user.getUsername());
                    edtEmail.setText(user.getEmail());
                    edtPassword.setText(user.getPassword());
                } else {
                    Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void saveChanges() {
        if (currentUser == null) {
            Toast.makeText(this, "Lỗi: chưa tải được thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        String newUsername = edtUsername.getText().toString().trim();
        String newPassword = edtPassword.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Tên người dùng không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setUsername(newUsername);
        if (!newPassword.isEmpty()) currentUser.setPassword(newPassword);

        new Thread(() -> {
            userRepository.updateUser(currentUser);
            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                edtPassword.setText("");
            });
        }).start();
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
