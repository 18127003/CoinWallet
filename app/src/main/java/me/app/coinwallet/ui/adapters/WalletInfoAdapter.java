package me.app.coinwallet.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.wallets.WalletInfoEntry;

import java.util.ArrayList;
import java.util.List;

public class WalletInfoAdapter extends RecyclerView.Adapter<WalletInfoAdapter.ViewHolder> {

    private List<WalletInfoEntry> walletInfos;
    private final OnItemClickListener onClickListener;

    public WalletInfoAdapter(OnItemClickListener onClickListener){
        walletInfos = new ArrayList<>();
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_info_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WalletInfoEntry entry = walletInfos.get(position);
        holder.getLabel().setText(entry.getLabel());
        holder.getLabel().setOnClickListener(v -> onClickListener.onClick(entry));
    }

    @Override
    public int getItemCount() {
        return walletInfos.size();
    }

    public void updateWalletInfos(final List<WalletInfoEntry> newWalletInfos){
        Log.e("HD", "Update wallet info "+newWalletInfos.size());
        walletInfos.clear();
        walletInfos = newWalletInfos;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onClick(WalletInfoEntry walletInfoEntry);
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
