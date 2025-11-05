package com.example.mquizez.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.R;
import com.example.mquizez.model.Quiz;
import com.example.mquizez.repository.QuizRepository;

import java.util.List;

public class MyQuizAdapter extends RecyclerView.Adapter<MyQuizAdapter.ViewHolder> {
    private final List<Quiz> quizList;
    private final QuizRepository quizRepository;
    private final Context context;

    public MyQuizAdapter(List<Quiz> quizList, QuizRepository quizRepository, Context context) {
        this.quizList = quizList;
        this.quizRepository = quizRepository;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_quiz, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.tvTitle.setText(quiz.getTitle());
        holder.tvDescription.setText(quiz.getDescription());

        holder.btnDelete.setOnClickListener(v -> {
            quizRepository.deleteQuizById(quiz.getId());
            quizList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Đã xóa quiz: " + quiz.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        Button btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvQuizTitle);
            tvDescription = itemView.findViewById(R.id.tvQuizDescription);
            btnDelete = itemView.findViewById(R.id.btnDeleteQuiz);
        }
    }
}
