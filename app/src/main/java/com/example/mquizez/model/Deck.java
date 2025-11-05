package com.example.mquizez.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "decks")
public class Deck {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String description;
    public Deck() {}

    public Deck(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
    