package me.app.coinwallet.ui.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import me.app.coinwallet.utils.Utils;
import org.bitcoinj.core.TransactionConfidence;

public class TransactionAdapter extends BaseAdapter<TransactionWrapper, TransactionAdapter.ViewHolder> {
    private Resources res;
    public TransactionAdapter(OnItemClickListener<TransactionWrapper> listener) {
        super(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item_card,parent,false);
        res=parent.getResources();
        return new TransactionAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionWrapper tx = data.get(position);
        holder.receiver.setText(tx.getReceiver().toString());
        holder.time.setText(Utils.formatDate(tx.getTime()));
        holder.confirmNum.setText(res.getText(R.string.confirmation)+": "+tx.getConfirmNum().toString());
        holder.amount.setText(tx.getAmount().toFriendlyString());
        TransactionConfidence.ConfidenceType type = tx.getStatus();
        switch (type){
            case DEAD:
                holder.status.setBackgroundResource(R.drawable.ic_baseline_cancel_24);
                holder.amount.setTextColor(res.getColor(R.color.red));
                break;
            case PENDING:
                holder.status.setBackgroundResource(R.drawable.ic_pending);
                holder.amount.setTextColor(res.getColor(R.color.grey));
                break;
            case BUILDING:
                holder.status.setBackgroundResource(R.drawable.ic_done);
                holder.amount.setTextColor(res.getColor(R.color.light_green));
        }
        holder.txCard.setOnClickListener(v->listener.onClick(tx));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView receiver;
        private final TextView time;
        private final TextView amount;
        private final TextView confirmNum;
        private final ImageView status;
        private final MaterialCardView txCard;

        public ViewHolder(View view) {
            super(view);
            receiver = view.findViewById(R.id.tx_receiver);
            time = view.findViewById(R.id.tx_time);
            amount = view.findViewById(R.id.tx_amount);
            confirmNum = view.findViewById(R.id.tx_confirmation_number);
            status = view.findViewById(R.id.tx_status);
            txCard = view.findViewById(R.id.tx_item_card);
        }
    }
}

