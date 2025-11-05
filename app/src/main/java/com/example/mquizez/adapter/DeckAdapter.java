package com.example.mquizez.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mquizez.R;
import com.example.mquizez.activity.CardListActivity;
import com.example.mquizez.model.Deck;

import java.util.List;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.DeckViewHolder> {

    private List<Deck> deckList;
    private Context context;

    public DeckAdapter(List<Deck> deckList, Context context) {
        this.deckList = deckList;
        this.context = context;
    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new DeckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeckViewHolder holder, int position) {
        Deck deck = deckList.get(position);
        holder.tvDeckName.setText(deck.name);
        holder.tvDeckDescription.setText(deck.description);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CardListActivity.class);

            intent.putExtra("DECK_ID", deck.id);
            intent.putExtra("DECK_NAME", deck.name);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return deckList.size();
    }

    public static class DeckViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeckName, tvDeckDescription;

        public DeckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeckName = itemView.findViewById(R.id.tvDeckName);
            tvDeckDescription = itemView.findViewById(R.id.tvDeckDescription);
        }
    }
}
