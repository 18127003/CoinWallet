package me.app.coinwallet.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import org.bitcoinj.core.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TxHistoryAdapter extends BaseAdapter<Transaction, TxHistoryAdapter.ViewHolder> {

    public TxHistoryAdapter(OnItemClickListener<Transaction> listener){
        super(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tx_history_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction tx = data.get(position);
        holder.getTextView().setText(tx.getTxId().toString());
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
