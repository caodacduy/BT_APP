package com.example.mquizez.repository;

import android.content.Context;

import com.example.mquizez.AppDatabase;
import com.example.mquizez.DAO.QuizDao;
import com.example.mquizez.DAO.UserDao;
import com.example.mquizez.model.Quiz;
import com.example.mquizez.model.User;

import java.util.List;

public class QuizRepository {
    private final QuizDao quizDao;

    public QuizRepository(Context context) {
        quizDao= AppDatabase.getDatabase(context).quizDao();
    }

    public void insertQuiz(Quiz quiz, OnQuizInsertListener listener) {
        new Thread(() -> {
            long id = quizDao.insertQuiz(quiz);
            if (listener != null) {
                listener.onQuizInserted(id);
            }
        }).start();
    }
    public interface OnQuizInsertListener {
        void onQuizInserted(long quizId);
    }
    public void getQuizzesByCategory(int categoryId) {
        new Thread(() -> quizDao.getQuizzesByCategory(categoryId)).start();
    }

    public List<Quiz> getQuizzesByCategorySync(int categoryId) {
        return quizDao.getQuizzesByCategory(categoryId);
    }
    public List<Quiz> getQuizzesByUserSync(int userId) {
        return quizDao.getQuizzesByUser(userId);
    }

    public void deleteQuizById(int quizId) {
        new Thread(() -> quizDao.deleteQuizById(quizId)).start();
    }

}
