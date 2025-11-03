package com.example.mquizez.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mquizez.R;

public class QuizDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvQuestionCount;
    private Button btnStartQuiz, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_detail);

        tvTitle = findViewById(R.id.tvQuizDetailTitle);
        tvDescription = findViewById(R.id.tvQuizDetailDescription);
        tvQuestionCount = findViewById(R.id.tvQuizDetailQuestionCount);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        btnBack = findViewById(R.id.btnBackToList);

        // Nhận dữ liệu từ intent

        Intent intent = getIntent();
        int quizId = intent.getIntExtra("quizId", -1);
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        int questionCount = intent.getIntExtra("questionCount", 0);

        // Hiển thị thông tin đề
        tvTitle.setText(title);
        tvDescription.setText(description != null ? description : "Không có mô tả");
        tvQuestionCount.setText("Số câu hỏi: " + questionCount);

        // Nút bắt đầu làm bài
        btnStartQuiz.setOnClickListener(v -> {
            Intent startIntent = new Intent(this, QuestionActivity.class);
            startIntent.putExtra("quizId", intent.getIntExtra("quizId", -1));
            startIntent.putExtra("title", title);
            startActivity(startIntent);
        });

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());
    }
}
