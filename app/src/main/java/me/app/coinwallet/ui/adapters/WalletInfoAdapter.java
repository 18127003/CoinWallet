package me.app.coinwallet.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.wallets.WalletInfoEntry;

public class WalletInfoAdapter extends BaseAdapter<WalletInfoEntry, WalletInfoAdapter.ViewHolder> {

    public WalletInfoAdapter(OnItemClickListener<WalletInfoEntry> listener){
        super(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_info_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WalletInfoEntry entry = data.get(position);
        holder.getLabel().setText(entry.getLabel());
        holder.getLabel().setOnClickListener(v -> listener.onClick(entry));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView label;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            label = view.findViewById(R.id.wallet_info_label);
        }

        public TextView getLabel() {
            return label;
        }
    }
}
