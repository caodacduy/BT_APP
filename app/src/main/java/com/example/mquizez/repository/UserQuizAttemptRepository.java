package com.example.mquizez.repository;

import android.content.Context;

import com.example.mquizez.DAO.UserQuizAttemptDao;
import com.example.mquizez.AppDatabase;
import com.example.mquizez.model.UserQuizAttempt;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserQuizAttemptRepository {
    private UserQuizAttemptDao attemptDao;
    private ExecutorService executorService;

    public UserQuizAttemptRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        attemptDao = db.userQuizAttemptDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertAttempt(UserQuizAttempt attempt) {
        executorService.execute(() -> attemptDao.insertAttempt(attempt));
    }

    public List<UserQuizAttempt> getAttemptsByUser(int userId) {
        return attemptDao.getAttemptsByUser(userId);
    }

    public void clearAll() {
        executorService.execute(() -> attemptDao.clearAll());
    }
}
