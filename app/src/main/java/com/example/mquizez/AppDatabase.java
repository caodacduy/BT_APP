package com.example.mquizez;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.mquizez.DAO.QuestionDao;
import com.example.mquizez.DAO.QuizDao;
import com.example.mquizez.DAO.UserDao;
import com.example.mquizez.model.Category;
import com.example.mquizez.model.Question;
import com.example.mquizez.model.Quiz;
import com.example.mquizez.model.User;
import com.example.mquizez.model.UserQuizAttempt;

@Database(entities = {User.class, Category.class, Quiz.class, Question.class, UserQuizAttempt.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {



    public abstract UserDao userDao();
//    public abstract QuizDao quizDao();
    // thêm các DAO khác
    public abstract QuizDao quizDao();

    public abstract QuestionDao questionDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "quiz_db")
                            .fallbackToDestructiveMigration()  // ⚡ Xóa DB cũ và tạo lại khi version thay đổi
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Log.d("RoomTest", "✅ Database created successfully!");
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    Log.d("RoomTest", "✅ Database opened successfully!");
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
