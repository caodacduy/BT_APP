package com.example.mquizez.activity;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.AppDatabase;
import com.example.mquizez.R;
import com.example.mquizez.adapter.DeckAdapter;
import com.example.mquizez.model.Deck;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DeckListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewDecks;
    private FloatingActionButton fabAddDeck;
    private DeckAdapter deckAdapter;
    private List<Deck> deckList = new ArrayList<>();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deck_list);

        db = AppDatabase.getDatabase(this);

        recyclerViewDecks = findViewById(R.id.recyclerViewDecks);
        fabAddDeck = findViewById(R.id.fabAddDeck);

        setupRecyclerView();
        loadDecksFromDb();

        fabAddDeck.setOnClickListener(v -> showAddDeckDialog());
    }

    // Tải lại dữ liệu khi quay lại màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        loadDecksFromDb();
    }

    private void setupRecyclerView() {
        deckAdapter = new DeckAdapter(deckList, this);
        recyclerViewDecks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDecks.setAdapter(deckAdapter);
    }

    private void loadDecksFromDb() {
        new Thread(() -> {
            List<Deck> decksFromDb = db.deckDao().getAllDecks();
            runOnUiThread(() -> {
                deckList.clear();
                deckList.addAll(decksFromDb);
                deckAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void showAddDeckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tạo Bộ Thẻ Mới");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Nhập tên bộ thẻ");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String deckName = input.getText().toString().trim();
            if (!deckName.isEmpty()) {
                addNewDeck(deckName);
            } else {
                Toast.makeText(this, "Tên bộ thẻ không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addNewDeck(String name) {
        new Thread(() -> {
            db.deckDao().insert(new Deck(name, "")); // Mô tả có thể thêm sau
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã tạo bộ thẻ: " + name, Toast.LENGTH_SHORT).show();
                loadDecksFromDb(); // Tải lại danh sách sau khi thêm
            });
        }).start();
    }
}
