package com.example.mquizez.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mquizez.model.Quiz;
import com.example.mquizez.model.User;

import java.util.List;

@Dao
public interface QuizDao {
    @Insert
    void insertQuiz(Quiz quiz);
        @Query("SELECT * FROM quizzes WHERE category_id= :categoryId")
        List<Quiz> getQuizzesByCategory(int categoryId);

}