package com.example.mquizez.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mquizez.R;

public class ResultActivity extends AppCompatActivity {

    private TextView txtScore;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtScore = findViewById(R.id.txtScore);
        btnHome = findViewById(R.id.btnHome);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 0);

        txtScore.setText("Bạn được " + score + "/" + total + " điểm!");

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
