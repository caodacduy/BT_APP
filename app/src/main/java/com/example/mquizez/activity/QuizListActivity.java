package com.example.mquizez.activity;



import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.R;
import com.example.mquizez.adapter.QuizListAdapter;
import com.example.mquizez.model.Quiz;
import com.example.mquizez.repository.QuizRepository;

import java.util.List;

public class QuizListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTitle;
    private QuizListAdapter adapter;
    private QuizRepository quizRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        btnBackHome.setOnClickListener(v -> {
            finish(); // Trở về HomeActivity
        });
        recyclerView = findViewById(R.id.recyclerViewQuizzes);
        tvTitle = findViewById(R.id.tvQuizTitle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String subject = getIntent().getStringExtra("subject");
        tvTitle.setText("Các đề: " + subject);

        quizRepository = new QuizRepository(this);

        // Giả sử categoryId tương ứng (tuỳ bạn map lại)
        int categoryId = getCategoryId(subject);

        new Thread(() -> {
            List<Quiz> quizList = quizRepository.getQuizzesByCategorySync(categoryId);
            runOnUiThread(() -> {
                if (quizList == null || quizList.isEmpty()) {
                    Toast.makeText(this, "Chưa có đề cho " + subject, Toast.LENGTH_SHORT).show();
                } else {
                    adapter = new QuizListAdapter(quizList);
                    recyclerView.setAdapter(adapter);
                }
            });
        }).start();
    }

    private int getCategoryId(String subject) {
        switch (subject) {
            case "Toán học":
                return 1;
            case "Tiếng Anh":
                return 2;
            case "Lịch sử":
                return 3;
            default:
                return 0;
        }
    }
}
