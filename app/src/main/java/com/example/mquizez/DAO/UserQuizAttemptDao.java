package com.example.mquizez.DAO;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.mquizez.model.UserQuizAttempt;

import java.util.List;

@Dao
public interface UserQuizAttemptDao {

    @Insert
    void insertAttempt(UserQuizAttempt attempt);

    @Query("SELECT * FROM user_quiz_attempts WHERE user_id = :userId")
    List<UserQuizAttempt> getAttemptsByUser(int userId);

    @Query("DELETE FROM user_quiz_attempts")
    void clearAll();
}
