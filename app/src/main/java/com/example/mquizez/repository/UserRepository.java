package com.example.mquizez.repository;

import android.content.Context;

import com.example.mquizez.AppDatabase;
import com.example.mquizez.DAO.UserDao;
import com.example.mquizez.model.User;

public class UserRepository {
    private final UserDao userDao;

    public UserRepository(Context context) {
        userDao = AppDatabase.getDatabase(context).userDao();
    }

    public void registerUser(User user) {
        new Thread(() -> userDao.insertUser(user)).start();
    }
    public User getUserById(int id) {
        return userDao.findById(id);
    }

    public User login(String email, String password) {
        return userDao.login(email, password);
    }

    public boolean emailExists(String email) {
        return userDao.findByEmail(email) != null;
    }
    public User getUserByEmail(String email) {
        return userDao.findByEmail(email);
    }
    public void updateUser(User user) {
        new Thread(() -> userDao.updateUser(user)).start();
    }

}
