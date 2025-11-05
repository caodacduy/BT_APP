package com.example.mquizez.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog; // << THÊM IMPORT NÀY
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mquizez.AppDatabase;
import com.example.mquizez.R;
import com.example.mquizez.model.Card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity {

    // ... (các biến của bạn giữ nguyên)
    private List<Card> cardList = new ArrayList<>();
    private int currentCardIndex = 0;
    private boolean isFront = true;
    private TextView tvTerm, tvPronunciation, tvDefinition;
    private CardView cardFront, cardBack;
    private ImageButton btnAudio;
    private Button btnPrev, btnNext;
    private AnimatorSet rightOut, rightIn;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        db = AppDatabase.getDatabase(this);

        initViews();
        loadAnimations();
        setupClickListeners();

        int deckId = getIntent().getIntExtra("DECK_ID", -1);
        if (deckId != -1) {
            loadCardsFromDb(deckId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy bộ thẻ!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // ... (các hàm initViews, loadAnimations, loadCardsFromDb, showCurrentCard giữ nguyên)
    private void initViews() {
        tvTerm = findViewById(R.id.tvTerm);
        tvPronunciation = findViewById(R.id.tvPronunciation);
        tvDefinition = findViewById(R.id.tvDefinition);
        cardFront = findViewById(R.id.cardFront);
        cardBack = findViewById(R.id.cardBack);
        btnAudio = findViewById(R.id.btnAudio);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
    }

    private void loadAnimations() {
        rightOut = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_out);
        rightIn = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_in);

        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        cardFront.setCameraDistance(8000 * scale);
        cardBack.setCameraDistance(8000 * scale);
    }

    private void loadCardsFromDb(int deckId) {
        new Thread(() -> {
            List<Card> cardsFromDb = db.cardDao().getCardsForDeck(deckId);
            runOnUiThread(() -> {
                if (cardsFromDb == null || cardsFromDb.isEmpty()) {
                    Toast.makeText(this, "Bộ thẻ này chưa có từ nào!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    cardList.clear();
                    cardList.addAll(cardsFromDb);
                    currentCardIndex = 0;
                    showCurrentCard();
                }
            });
        }).start();
    }

    private void showCurrentCard() {
        if (cardList.isEmpty() || currentCardIndex < 0 || currentCardIndex >= cardList.size()) {
            return;
        }
        Card currentCard = cardList.get(currentCardIndex);
        tvTerm.setText(currentCard.term);
        tvPronunciation.setText(currentCard.pronunciation);
        tvDefinition.setText(currentCard.definition);
        if (!isFront) {
            flipCard();
        }
        cardFront.setAlpha(1f);
        cardBack.setAlpha(0f);
    }

    private void setupClickListeners() {
        View.OnClickListener flipClickListener = v -> flipCard();
        cardFront.setOnClickListener(flipClickListener);
        cardBack.setOnClickListener(flipClickListener);

        btnAudio.setOnClickListener(v -> playAudio());

        btnNext.setOnClickListener(v -> {
            if (currentCardIndex < cardList.size() - 1) {
                // Nếu chưa phải thẻ cuối, chuyển sang thẻ tiếp theo
                currentCardIndex++;
                showCurrentCard();
            } else {
                // Nếu đã là thẻ cuối cùng, hiển thị dialog hoàn thành
                showCompletionDialog();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentCardIndex > 0) {
                currentCardIndex--;
                showCurrentCard();
            } else {
                Toast.makeText(this, "Bạn đang ở thẻ đầu tiên!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hoàn thành!")
                .setMessage("Bạn đã học xong tất cả các thẻ trong bộ này.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Khi người dùng nhấn OK, đóng màn hình học lại
                    finish();
                })
                .setCancelable(false) // Không cho phép đóng dialog bằng nút Back
                .show();
    }


    // ... (hàm flipCard và playAudio
    private void flipCard() {
        if (isFront) {
            rightOut.setTarget(cardFront);
            rightIn.setTarget(cardBack);
        } else {
            rightOut.setTarget(cardBack);
            rightIn.setTarget(cardFront);
        }
        rightOut.start();
        rightIn.start();
        isFront = !isFront;
    }

    private void playAudio() {
        if (cardList.isEmpty()) return;
        Card currentCard = cardList.get(currentCardIndex);
        String audioPath = currentCard.audioFilePath;
        if (audioPath != null && !audioPath.isEmpty()) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audioPath);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(MediaPlayer::start);
                mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi: Không thể phát file âm thanh", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Từ này không có âm thanh", Toast.LENGTH_SHORT).show();
        }
    }
}
