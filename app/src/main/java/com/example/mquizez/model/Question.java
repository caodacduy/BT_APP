package com.example.mquizez.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "question")
public class Question {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "quiz_id")
    private Integer quizId;

    @ColumnInfo(name = "question_text")
    private String questionText;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getQuizId() { return quizId; }
    public void setQuizId(Integer quizId) { this.quizId = quizId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }
}
