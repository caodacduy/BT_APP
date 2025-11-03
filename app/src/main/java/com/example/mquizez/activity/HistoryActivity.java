package com.example.mquizez.activity;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.R;
import com.example.mquizez.adapter.AttemptHistoryAdapter;
import com.example.mquizez.model.UserQuizAttempt;
import com.example.mquizez.repository.UserQuizAttemptRepository;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnBackHome;
    private UserQuizAttemptRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerViewHistory);
        btnBackHome = findViewById(R.id.btnBackHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repository = new UserQuizAttemptRepository(this);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPref.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy danh sách lịch sử làm bài (chạy trên luồng nền)
        new Thread(() -> {
            List<UserQuizAttempt> attemptList = repository.getAttemptsByUser(userId);
            runOnUiThread(() -> {
                if (attemptList == null || attemptList.isEmpty()) {
                    Toast.makeText(this, "Chưa có lịch sử làm bài!", Toast.LENGTH_SHORT).show();
                } else {
                    recyclerView.setAdapter(new AttemptHistoryAdapter(attemptList));
                }
            });
        }).start();

        // Nút quay lại
        btnBackHome.setOnClickListener(v -> finish());
    }
}
