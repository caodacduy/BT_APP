package com.example.mquizez.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "cards",
        foreignKeys = @ForeignKey(entity = Deck.class,
                parentColumns = "id",
                childColumns = "deckId",
                onDelete = ForeignKey.CASCADE))
public class Card {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int deckId;
    public String term;
    public String definition;
    public String pronunciation;
    public String audioFilePath;

    public Card(int deckId, String term, String definition, String pronunciation, String audioFilePath) {
        this.deckId = deckId;
        this.term = term;
        this.definition = definition;
        this.pronunciation = pronunciation;
        this.audioFilePath = audioFilePath;
    }
}
    