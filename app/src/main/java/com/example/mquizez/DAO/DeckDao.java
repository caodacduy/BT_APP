package com.example.mquizez.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.mquizez.model.Deck;
import java.util.List;

@Dao
public interface DeckDao {
    @Insert
    long insert(Deck deck);

    @Query("SELECT * FROM decks")
    List<Deck> getAllDecks();
}
    