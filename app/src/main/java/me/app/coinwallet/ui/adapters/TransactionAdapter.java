package me.app.coinwallet.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionConfidence;

public class TransactionAdapter extends BaseAdapter<TransactionWrapper, TransactionAdapter.ViewHolder> {

    public TransactionAdapter() {
        super(null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_card,parent,false);
        return new TransactionAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionWrapper tx = data.get(position);
        holder.receiver.setText(tx.getReceiver().toString());
        holder.time.setText(tx.getTime().toString());
        holder.confirmNum.setText(String.valueOf(tx.getConfirmNum()));
        holder.amount.setText(tx.getAmountString());
        TransactionConfidence.ConfidenceType type = tx.getStatus();
        switch (type){
            case DEAD:
                holder.status.setBackgroundResource(R.drawable.ic_baseline_cancel_24);
                break;
            case PENDING:
                holder.status.setBackgroundResource(R.drawable.ic_pending);
                break;
            case BUILDING:
                holder.status.setBackgroundResource(R.drawable.ic_done);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView receiver;
        private final TextView time;
        private final TextView amount;
        private final TextView confirmNum;
        private final ImageView status;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            receiver = view.findViewById(R.id.tx_receiver);
            time = view.findViewById(R.id.tx_time);
            amount = view.findViewById(R.id.tx_amount);
            confirmNum = view.findViewById(R.id.tx_confirmation_number);
            status = view.findViewById(R.id.tx_status);
        }
    }
}

