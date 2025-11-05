package com.example.mquizez.activity;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast; // Thêm Toast để hiển thị thông báo

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mquizez.R;
import com.google.android.material.navigation.NavigationView;

// ... (các imports khác)
public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private CardView btnMath, btnEnglish, btnHistory, btnHistoryScore, btnFlashcard;
    private ImageButton btnMenu;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnMath = findViewById(R.id.btnMath);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnHistory = findViewById(R.id.btnHistory);
        btnFlashcard = findViewById(R.id.btnFlashcard);

//        btnHistoryScore = findViewById(R.id.btnHistoryScore);
        btnMenu = findViewById(R.id.btnMenu);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Lấy thông tin user
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPref.getString("username", "User");
        tvWelcome.setText("Xin chào, " + username + "!");

        // Xử lý nút chọn môn học
        btnMath.setOnClickListener(v -> startQuizActivity("Toán học"));
        btnEnglish.setOnClickListener(v -> startQuizActivity("Tiếng Anh"));
        btnHistory.setOnClickListener(v -> startQuizActivity("Lịch sử"));
        btnFlashcard.setOnClickListener(v -> {
            Toast.makeText(this, "FlashCard", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, DeckListActivity.class);
            startActivity(intent);
        });
//        btnHistoryScore.setOnClickListener(v -> openHistoryActivity());

        // Mở drawer
        btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Xử lý navigation drawer
        navigationView.setNavigationItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);

            } else if (itemId == R.id.nav_history) {
                openHistoryActivity();
            } else if (itemId == R.id.nav_logout) {
                logout();
            }
            else if (itemId== R.id.nav_add_question){
                Intent intent = new Intent(HomeActivity.this, AddQuizActivity.class);
                startActivity(intent);
            }
            else if (itemId == R.id.nav_manage_decks) {
                Intent intent = new Intent(HomeActivity.this, DeckListActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        });
    }

    private void startQuizActivity(String subject) {
        Toast.makeText(this, "Bắt đầu bài kiểm tra: " + subject, Toast.LENGTH_SHORT).show();
        // Intent đến QuizActivity
        Intent intent = new Intent(HomeActivity.this, QuizListActivity.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }

    private void openHistoryActivity() {
        Toast.makeText(this, "Chuyển đến Lịch sử làm bài", Toast.LENGTH_SHORT).show();
        // Intent đến HistoryActivity
        Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    private void logout() {
        SharedPreferences sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
    }
}
