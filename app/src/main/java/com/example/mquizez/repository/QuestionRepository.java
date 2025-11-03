package com.example.mquizez.repository;

import android.content.Context;

import com.example.mquizez.AppDatabase;
import com.example.mquizez.DAO.QuestionDao;
import com.example.mquizez.model.Question;

import java.util.List;

public class QuestionRepository {
    private final QuestionDao questionDao;

    public QuestionRepository(Context context) {
        questionDao = AppDatabase.getDatabase(context).questionDao();
    }
    public List<Question> getQuestionsByQuizId(int quizId) {
        return questionDao.getQuestionsByQuizId(quizId);
    }
    public void insertQuestion(Question question) {
        new Thread(() -> questionDao.insertQuestion(question)).start();
    }

    public void insertQuestions(List<Question> questions) {
        new Thread(() -> {
            for (Question q : questions) {
                questionDao.insertQuestion(q);
            }
        }).start();
    }
}
