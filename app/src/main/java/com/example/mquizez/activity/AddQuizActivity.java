package com.example.mquizez.activity;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//import com.example.mquizez.R;
import com.example.mquizez.R;
import com.example.mquizez.model.Quiz;
import com.example.mquizez.repository.QuizRepository;

public class AddQuizActivity extends AppCompatActivity {

    private EditText edtTitle, edtDescription;
    private Spinner spinnerCategory;
    private Button btnSave;
    private int selectedCategory = 1; // default Toán

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);

        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);

        // Spinner danh mục
        String[] categories = {"Toán", "Anh", "Lịch sử"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCategory = position + 1; // 1=Toán, 2=Anh, 3=Lịch sử
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnSave.setOnClickListener(v -> saveQuiz());
    }

    private void saveQuiz() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        int createdBy = sharedPref.getInt("user_id", -1); // lưu id người dùng, nên sửa lại khi đăng nhập
        if (createdBy == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setCategoryId(selectedCategory);
        quiz.setCreatedBy(createdBy);

        QuizRepository quizRepository = new QuizRepository(this);
        quizRepository.insertQuiz(quiz);

        Toast.makeText(this, "Thêm đề thi thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
