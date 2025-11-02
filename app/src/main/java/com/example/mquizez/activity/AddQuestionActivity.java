package com.example.mquizez.activity;



import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mquizez.R;
import com.example.mquizez.model.Question;
import com.example.mquizez.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionActivity extends AppCompatActivity {

    private TextView tvProgress;
    private EditText edtQuestion, edtA, edtB, edtC, edtD;
    private RadioGroup rgCorrect;
    private Button btnNext;
    private int quizId, totalQuestions, currentIndex = 1;
    private List<Question> questionList = new ArrayList<>();
    private QuestionRepository questionRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        tvProgress = findViewById(R.id.tvProgress);
        edtQuestion = findViewById(R.id.edtQuestion);
        edtA = findViewById(R.id.edtA);
        edtB = findViewById(R.id.edtB);
        edtC = findViewById(R.id.edtC);
        edtD = findViewById(R.id.edtD);
        rgCorrect = findViewById(R.id.rgCorrect);
        btnNext = findViewById(R.id.btnNext);

        quizId = getIntent().getIntExtra("quizId", -1);
        totalQuestions = getIntent().getIntExtra("questionCount", 1);
        questionRepository = new QuestionRepository(this);

        updateProgress();

        btnNext.setOnClickListener(v -> saveQuestion());
    }

    private void updateProgress() {
        tvProgress.setText("Câu " + currentIndex + "/" + totalQuestions);
    }

    private void saveQuestion() {
        String questionText = edtQuestion.getText().toString().trim();
        String a = edtA.getText().toString().trim();
        String b = edtB.getText().toString().trim();
        String c = edtC.getText().toString().trim();
        String d = edtD.getText().toString().trim();

        int selectedId = rgCorrect.getCheckedRadioButtonId();
        if (selectedId == -1 || questionText.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton rb = findViewById(selectedId);
        String correct = rb.getText().toString().substring(0, 1); // "A", "B", "C", "D"

        Question q = new Question();
        q.setQuizId(quizId);
        q.setQuestionText(questionText);
        q.setOptionA(a);
        q.setOptionB(b);
        q.setOptionC(c);
        q.setOptionD(d);
        q.setCorrectAnswer(correct);
        questionList.add(q);

        if (currentIndex < totalQuestions) {
            currentIndex++;
            clearForm();
            updateProgress();
        } else {
            questionRepository.insertQuestions(questionList);
            Toast.makeText(this, "Đã thêm toàn bộ câu hỏi!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void clearForm() {
        edtQuestion.setText("");
        edtA.setText("");
        edtB.setText("");
        edtC.setText("");
        edtD.setText("");
        rgCorrect.clearCheck();
    }
}
