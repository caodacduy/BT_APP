package com.example.mquizez.adapter;



import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.R;
import com.example.mquizez.activity.QuizDetailActivity;
import com.example.mquizez.model.Quiz;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.QuizViewHolder> {

    private final List<Quiz> quizList;

    public QuizListAdapter(List<Quiz> quizList) {
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.tvQuizName.setText(quiz.getTitle());
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(),
                    "Đang vào đề: " + quiz.getTitle(),
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(v.getContext(), QuizDetailActivity.class);
            intent.putExtra("quizId", quiz.getId());
            intent.putExtra("title", quiz.getTitle());
            intent.putExtra("description", quiz.getDescription());
            intent.putExtra("questionCount", quiz.getQuestionCount());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizName;
        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizName = itemView.findViewById(R.id.tvQuizName);
        }
    }
}

