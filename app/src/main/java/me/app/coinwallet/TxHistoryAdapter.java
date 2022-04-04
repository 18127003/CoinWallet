package me.app.coinwallet;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.bitcoinj.core.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TxHistoryAdapter extends RecyclerView.Adapter<TxHistoryAdapter.ViewHolder> {

    private List<Transaction> history;

    public TxHistoryAdapter(){
        history = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tx_history_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction tx = history.get(position);
        holder.getTextView().setText(tx.getTxId().toString());
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    public void updateHistory(final List<Transaction> newHistory){
        Log.e("HD", "Update history "+newHistory.size());
        history.clear();
        history = newHistory;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = view.findViewById(R.id.tx_id);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}
