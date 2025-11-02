package com.example.mquizez.repository;

import android.content.Context;

import com.example.mquizez.AppDatabase;
import com.example.mquizez.DAO.QuizDao;
import com.example.mquizez.DAO.UserDao;
import com.example.mquizez.model.User;

public class QuizRepository {
    private final QuizDao quizDao;

    public QuizRepository(Context context) {
        quizDao= AppDatabase.getDatabase(context).quizDao();
    }

    public void insertQuiz(com.example.mquizez.model.Quiz quiz) {
        new Thread(() -> quizDao.insertQuiz(quiz)).start();
    }
    public void getQuizzesByCategory(int categoryId) {
        new Thread(() -> quizDao.getQuizzesByCategory(categoryId)).start();
    }
}
