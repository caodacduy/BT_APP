package com.example.mquizez.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.R;
import com.example.mquizez.adapter.MyQuizAdapter;
import com.example.mquizez.model.Quiz;
import com.example.mquizez.repository.QuizRepository;

import java.util.List;

public class MyQuizzesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyQuizAdapter adapter;
    private QuizRepository quizRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_quizzes);

        recyclerView = findViewById(R.id.recyclerViewMyQuizzes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizRepository = new QuizRepository(this);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Lấy id user
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPref.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load quiz do user tạo
        new Thread(() -> {
            List<Quiz> quizList = quizRepository.getQuizzesByUserSync(userId);
            runOnUiThread(() -> {
                if (quizList == null || quizList.isEmpty()) {
                    Toast.makeText(this, "Bạn chưa tạo quiz nào", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = new MyQuizAdapter(quizList, quizRepository, this);
                    recyclerView.setAdapter(adapter);
                }
            });
        }).start();
    }
}
