package com.example.mquizez.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.R;
import com.example.mquizez.model.UserQuizAttempt;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AttemptHistoryAdapter extends RecyclerView.Adapter<AttemptHistoryAdapter.AttemptViewHolder> {

    private final List<UserQuizAttempt> attempts;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public AttemptHistoryAdapter(List<UserQuizAttempt> attempts) {
        this.attempts = attempts;
    }

    @NonNull
    @Override
    public AttemptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_attempt_history, parent, false);
        return new AttemptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttemptViewHolder holder, int position) {
        UserQuizAttempt attempt = attempts.get(position);
        holder.tvQuizId.setText("Đề thi ID: " + attempt.getQuizId());
        holder.tvScore.setText("Điểm: " + attempt.getScore());
        holder.tvTime.setText("Kết thúc: " + sdf.format(attempt.getFinishedAt()));
    }

    @Override
    public int getItemCount() {
        return attempts.size();
    }

    static class AttemptViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizId, tvScore, tvTime;

        public AttemptViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizId = itemView.findViewById(R.id.tvQuizId);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
