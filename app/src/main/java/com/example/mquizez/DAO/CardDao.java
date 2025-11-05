package com.example.mquizez.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.mquizez.model.Card;
import java.util.List;

@Dao
public interface CardDao {
    @Insert
    void insert(Card card);

    @Query("SELECT * FROM cards WHERE deckId = :deckId")
    List<Card> getCardsForDeck(int deckId);
}
    