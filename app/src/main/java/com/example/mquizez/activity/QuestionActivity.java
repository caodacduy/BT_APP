package com.example.mquizez.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
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

    private TextView tvQuestion, tvTitle, tvTimer;
    private TextView rbA, rbB, rbC, rbD;
    private Button btnNext, btnFinish;
    private List<Question> questionList;
    private int currentIndex = 0;
    private int score = 0;
    private int quizId;
    private String quizTitle;
    private String selectedAnswer = null;

    private CountDownTimer countDownTimer;
    private static final int QUESTION_TIME = 10000; // 10 giây mỗi câu
    private boolean isAnswered = false; // Đánh dấu đã trả lời hay chưa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        tvTitle = findViewById(R.id.tvQuizTitle);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvTimer = findViewById(R.id.tvTimer);
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

        btnNext.setOnClickListener(v -> {
            if (!isAnswered) {
                Toast.makeText(this, "Hãy chọn đáp án trước!", Toast.LENGTH_SHORT).show();
            } else {
                nextQuestion();
            }
        });

        btnFinish.setOnClickListener(v -> finishQuiz());

        // Listener cho 4 ô đáp án
        View.OnClickListener optionClickListener = view -> {
            if (isAnswered) return; // Không cho chọn lại

            isAnswered = true;
            countDownTimer.cancel();

            TextView clicked = (TextView) view;
            selectedAnswer = getAnswerLetter(clicked);
            Question q = questionList.get(currentIndex);

            // Kiểm tra đúng sai
            if (selectedAnswer.equalsIgnoreCase(q.getCorrectAnswer())) {
                score++;
                clicked.setBackgroundResource(R.drawable.bg_answer_correct); // Màu xanh
            } else {
                clicked.setBackgroundResource(R.drawable.bg_answer_wrong); // Màu đỏ
                highlightCorrectAnswer(q.getCorrectAnswer()); // Hiển thị đáp án đúng
            }

            // Tự động sang câu sau sau 1 giây
            new Handler().postDelayed(this::nextQuestion, 1000);
        };

        rbA.setOnClickListener(optionClickListener);
        rbB.setOnClickListener(optionClickListener);
        rbC.setOnClickListener(optionClickListener);
        rbD.setOnClickListener(optionClickListener);
    }

    private void showQuestion() {
        resetOptionColors();
        selectedAnswer = null;
        isAnswered = false;

        if (countDownTimer != null) countDownTimer.cancel();
        startTimer();

        Question q = questionList.get(currentIndex);
        tvQuestion.setText((currentIndex + 1) + ". " + q.getQuestionText());
        rbA.setText("A. " + q.getOptionA());
        rbB.setText("B. " + q.getOptionB());
        rbC.setText("C. " + q.getOptionC());
        rbD.setText("D. " + q.getOptionD());
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(QUESTION_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvTimer.setText(seconds + "s");

                if (seconds <= 3) {
                    tvTimer.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    tvTimer.setTextColor(getResources().getColor(android.R.color.black));
                }
            }

            @Override
            public void onFinish() {
                if (!isAnswered) {
                    Toast.makeText(QuestionActivity.this, "Hết thời gian!", Toast.LENGTH_SHORT).show();
                    isAnswered = true;
                    new Handler().postDelayed(QuestionActivity.this::nextQuestion, 1000);
                }
            }
        };
        countDownTimer.start();
    }

    private void resetOptionColors() {
        rbA.setBackgroundResource(R.drawable.bg_answer_option_square);
        rbB.setBackgroundResource(R.drawable.bg_answer_option_square);
        rbC.setBackgroundResource(R.drawable.bg_answer_option_square);
        rbD.setBackgroundResource(R.drawable.bg_answer_option_square);
    }

    private String getAnswerLetter(TextView view) {
        if (view == rbA) return "A";
        if (view == rbB) return "B";
        if (view == rbC) return "C";
        if (view == rbD) return "D";
        return "";
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        switch (correctAnswer.toUpperCase()) {
            case "A":
                rbA.setBackgroundResource(R.drawable.bg_answer_correct);
                break;
            case "B":
                rbB.setBackgroundResource(R.drawable.bg_answer_correct);
                break;
            case "C":
                rbC.setBackgroundResource(R.drawable.bg_answer_correct);
                break;
            case "D":
                rbD.setBackgroundResource(R.drawable.bg_answer_correct);
                break;
        }
    }

    private void nextQuestion() {
        if (countDownTimer != null) countDownTimer.cancel();

        currentIndex++;
        if (currentIndex < questionList.size()) {
            showQuestion();
        } else {
            finishQuiz();
        }
    }

    private void finishQuiz() {
        if (countDownTimer != null) countDownTimer.cancel();

        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        int userId = sharedPref.getInt("user_id", -1);

        UserQuizAttempt attempt = new UserQuizAttempt();
        attempt.setUserId(userId);
        attempt.setQuizId(quizId);
        attempt.setScore(score);
        attempt.setStartedAt(System.currentTimeMillis());
        attempt.setFinishedAt(System.currentTimeMillis());

        UserQuizAttemptRepository repository = new UserQuizAttemptRepository(this);
        repository.insertAttempt(attempt);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("score", score);
        intent.putExtra("total", questionList.size());
        startActivity(intent);
        finish();
    }
}
