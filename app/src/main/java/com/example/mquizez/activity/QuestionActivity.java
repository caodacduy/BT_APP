package com.example.mquizez.activity;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mquizez.R;
import com.example.mquizez.model.Question;
import com.example.mquizez.model.UserQuizAttempt;
import com.example.mquizez.repository.QuestionRepository;
import com.example.mquizez.repository.UserQuizAttemptRepository;

import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private TextView tvQuestion, tvTitle;
    private RadioGroup rgOptions;
    private RadioButton rbA, rbB, rbC, rbD;
    private Button btnNext, btnFinish;
    private List<Question> questionList;
    private int currentIndex = 0;
    private int score = 0;
    private int quizId;
    private String quizTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        tvTitle = findViewById(R.id.tvQuizTitle);
        tvQuestion = findViewById(R.id.tvQuestion);
        rgOptions = findViewById(R.id.rgOptions);
        rbA = findViewById(R.id.rbOptionA);
        rbB = findViewById(R.id.rbOptionB);
        rbC = findViewById(R.id.rbOptionC);
        rbD = findViewById(R.id.rbOptionD);
        btnNext = findViewById(R.id.btnNext);
        btnFinish = findViewById(R.id.btnFinish);

        quizId = getIntent().getIntExtra("quizId", -1);
        quizTitle = getIntent().getStringExtra("title");

        tvTitle.setText("Đề thi: " + quizTitle);

        QuestionRepository repository = new QuestionRepository(this);

        // Tải câu hỏi
        new Thread(() -> {
            questionList = repository.getQuestionsByQuizId(quizId);
            runOnUiThread(() -> {
                if (questionList == null || questionList.isEmpty()) {
                    Toast.makeText(this, "Chưa có câu hỏi cho đề này!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    showQuestion();
                }
            });
        }).start();

        btnNext.setOnClickListener(v -> nextQuestion());
        btnFinish.setOnClickListener(v -> finishQuiz());
    }

    private void showQuestion() {
        Question q = questionList.get(currentIndex);
        tvQuestion.setText((currentIndex + 1) + ". " + q.getQuestionText());
        rbA.setText("A. " + q.getOptionA());
        rbB.setText("B. " + q.getOptionB());
        rbC.setText("C. " + q.getOptionC());
        rbD.setText("D. " + q.getOptionD());
        rgOptions.clearCheck();
    }

    private void nextQuestion() {
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Hãy chọn một đáp án!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selected = findViewById(selectedId);
        String answer = selected.getText().toString().substring(0, 1);

        Question q = questionList.get(currentIndex);
        if (answer.equalsIgnoreCase(q.getCorrectAnswer())) {
            score++;
        }

        currentIndex++;
        if (currentIndex < questionList.size()) {
            showQuestion();
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        // Lưu kết quả
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPref.getInt("user_id", -1);
        int quizId = getIntent().getIntExtra("quizId", -1);
        String quizTitle = getIntent().getStringExtra("title");
        UserQuizAttempt attempt = new UserQuizAttempt();
        attempt.setUserId(userId); // nếu có user đăng nhập thì dùng ID thật
        attempt.setQuizId(quizId);
        attempt.setScore(score);
        attempt.setStartedAt(System.currentTimeMillis());
        attempt.setFinishedAt(System.currentTimeMillis());

        UserQuizAttemptRepository repository = new UserQuizAttemptRepository(this);
        repository.insertAttempt(attempt);

        // Mở màn hình kết quả
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", questionList.size());
        startActivity(intent);
        finish();
    }
}
