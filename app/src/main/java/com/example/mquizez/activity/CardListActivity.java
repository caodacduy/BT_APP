package com.example.mquizez.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.AppDatabase;
import com.example.mquizez.R;
import com.example.mquizez.adapter.CardAdapter;
import com.example.mquizez.model.Card;
import com.example.mquizez.model.api.Meaning;
import com.example.mquizez.model.api.MyMemoryApiResponse;
import com.example.mquizez.model.api.Phonetic;
import com.example.mquizez.model.api.WordApiResponse;
import com.example.mquizez.remote.ApiClient;
import com.example.mquizez.remote.DictionaryApiService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardListActivity extends AppCompatActivity {

    private int currentDeckId = -1;
    private String currentDeckName = "";

    // Views
    private TextView tvDeckTitle;
    private Button btnStartLearning;
    private RecyclerView recyclerViewCards;
    private FloatingActionButton fabAddCard;
    private EditText edtSearchWord;
    private ImageButton btnSearchWord;

    // Data & Services
    private AppDatabase db;
    private List<Card> cardList = new ArrayList<>();
    private CardAdapter cardAdapter;
    private DictionaryApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        db = AppDatabase.getDatabase(this);
        apiService = ApiClient.getClient().create(DictionaryApiService.class);

        currentDeckId = getIntent().getIntExtra("DECK_ID", -1);
        currentDeckName = getIntent().getStringExtra("DECK_NAME");

        if (currentDeckId == -1) {
            Toast.makeText(this, "Lỗi, không tìm thấy bộ thẻ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCardsFromDb();
    }

    private void initViews() {
        tvDeckTitle = findViewById(R.id.tvDeckTitle);
        btnStartLearning = findViewById(R.id.btnStartLearning);
        recyclerViewCards = findViewById(R.id.recyclerViewCards);
        fabAddCard = findViewById(R.id.fabAddCard);
        edtSearchWord = findViewById(R.id.edtSearchWord);
        btnSearchWord = findViewById(R.id.btnSearchWord);
        tvDeckTitle.setText(currentDeckName);
    }

    private void setupRecyclerView() {
        cardAdapter = new CardAdapter(cardList);
        recyclerViewCards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCards.setAdapter(cardAdapter);
    }

    private void setupClickListeners() {
        btnStartLearning.setOnClickListener(v -> {
            if (cardList.isEmpty()) {
                Toast.makeText(this, "Bạn cần thêm thẻ trước khi học!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CardListActivity.this, FlashcardActivity.class);
                intent.putExtra("DECK_ID", currentDeckId);
                startActivity(intent);
            }
        });

        fabAddCard.setOnClickListener(v -> showAddCardDialog(null, null, null, null));
        btnSearchWord.setOnClickListener(v -> handleSearch());
        edtSearchWord.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearch();
                return true;
            }
            return false;
        });
    }

    private void handleSearch() {
        String wordToSearch = edtSearchWord.getText().toString().trim();
        if (wordToSearch.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập từ để tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtSearchWord.getWindowToken(), 0);
        }
        searchWordOnline(wordToSearch);
    }

    private void searchWordOnline(String word) {
        setSearchInProgress(true); // Hiển thị trạng thái đang tìm kiếm

        apiService.getWordDefinition(word).enqueue(new Callback<List<WordApiResponse>>() {
            @Override
            public void onResponse(Call<List<WordApiResponse>> call, Response<List<WordApiResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    setSearchInProgress(false);
                    Toast.makeText(CardListActivity.this, "Không tìm thấy từ: '" + word + "'", Toast.LENGTH_LONG).show();
                    return;
                }

                WordApiResponse result = response.body().get(0);
                String englishDefinition = getBestDefinition(result);
                String pronunciation = getBestPronunciation(result);
                String audioUrl = getBestAudioUrl(result);

                if (englishDefinition.equals("Không tìm thấy định nghĩa.")) {
                    setSearchInProgress(false);
                    Toast.makeText(CardListActivity.this, "Không tìm thấy định nghĩa cho từ này.", Toast.LENGTH_LONG).show();
                } else {
                    translateToVietnamese(result.word, englishDefinition, pronunciation, audioUrl);
                }
            }

            @Override
            public void onFailure(Call<List<WordApiResponse>> call, Throwable t) {
                setSearchInProgress(false);
                Toast.makeText(CardListActivity.this, "Lỗi mạng (Từ điển)", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void translateToVietnamese(String term, String englishDefinition, String pronunciation, String audioUrl) {
        apiService.translateText(englishDefinition, "en|vi").enqueue(new Callback<MyMemoryApiResponse>() {
            @Override
            public void onResponse(Call<MyMemoryApiResponse> call, Response<MyMemoryApiResponse> response) {
                setSearchInProgress(false); // Kết thúc tìm kiếm
                String vietnameseDefinition = englishDefinition;
                if (response.isSuccessful() && response.body() != null && response.body().responseData != null) {
                    vietnameseDefinition = response.body().responseData.translatedText;
                }
                showAddCardDialog(term, vietnameseDefinition, pronunciation, audioUrl);
            }

            @Override
            public void onFailure(Call<MyMemoryApiResponse> call, Throwable t) {
                setSearchInProgress(false); // Kết thúc tìm kiếm
                Toast.makeText(CardListActivity.this, "Lỗi mạng (Dịch)", Toast.LENGTH_LONG).show();
                showAddCardDialog(term, englishDefinition, pronunciation, audioUrl);
            }
        });
    }

    private void showAddCardDialog(String term, String definition, String pronunciation, String audioUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_card, null);
        builder.setView(dialogView);

        final EditText edtTerm = dialogView.findViewById(R.id.edtTerm);
        final EditText edtDefinition = dialogView.findViewById(R.id.edtDefinition);

        if (term != null) edtTerm.setText(term);
        if (definition != null) edtDefinition.setText(definition);

        builder.setTitle(term != null ? "Xác nhận thẻ mới" : "Thêm Thẻ Thủ công")
                .setPositiveButton("Lưu", (dialog, id) -> {
                    String finalTerm = edtTerm.getText().toString().trim();
                    String finalDefinition = edtDefinition.getText().toString().trim();
                    if (!finalTerm.isEmpty() && !finalDefinition.isEmpty()) {
                        addNewCard(finalTerm, finalDefinition,
                                pronunciation != null ? pronunciation : "",
                                audioUrl != null ? audioUrl : "");
                    } else {
                        Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    private void addNewCard(String term, String definition, String pronunciation, String audioPath) {
        new Thread(() -> {
            boolean isDuplicate = db.cardDao().getCardsForDeck(currentDeckId)
                    .stream().anyMatch(c -> c.term.equalsIgnoreCase(term));

            if (isDuplicate) {
                runOnUiThread(() -> Toast.makeText(CardListActivity.this, "Thẻ '" + term + "' đã tồn tại.", Toast.LENGTH_LONG).show());
            } else {
                Card newCard = new Card(currentDeckId, term, definition, pronunciation, audioPath);
                db.cardDao().insert(newCard);
                runOnUiThread(this::loadCardsFromDb);
            }
        }).start();
    }

    private void loadCardsFromDb() {
        new Thread(() -> {
            List<Card> cardsFromDb = db.cardDao().getCardsForDeck(currentDeckId);
            runOnUiThread(() -> {
                cardList.clear();
                cardList.addAll(cardsFromDb);
                cardAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private String getBestPronunciation(WordApiResponse apiResult) {
        if (apiResult.phonetics != null) {
            for (Phonetic phonetic : apiResult.phonetics) {
                if (phonetic.text != null && !phonetic.text.isEmpty()) return phonetic.text;
            }
        }
        return apiResult.phonetic != null ? apiResult.phonetic : "";
    }

    private String getBestAudioUrl(WordApiResponse apiResult) {
        if (apiResult.phonetics != null) {
            for (Phonetic phonetic : apiResult.phonetics) {
                if (phonetic.audio != null && !phonetic.audio.isEmpty()) return phonetic.audio;
            }
        }
        return "";
    }

    private String getBestDefinition(WordApiResponse apiResult) {
        if (apiResult.meanings != null && !apiResult.meanings.isEmpty()) {
            for (Meaning meaning : apiResult.meanings) {
                if (meaning.partOfSpeech.matches("(?i)noun|verb") && meaning.definitions != null && !meaning.definitions.isEmpty()) {
                    return meaning.definitions.get(0).definition;
                }
            }
            if (apiResult.meanings.get(0).definitions != null && !apiResult.meanings.get(0).definitions.isEmpty()) {
                return apiResult.meanings.get(0).definitions.get(0).definition;
            }
        }
        return "Không tìm thấy định nghĩa.";
    }

    private void setSearchInProgress(boolean inProgress) {
        btnSearchWord.setEnabled(!inProgress);
    }
}
