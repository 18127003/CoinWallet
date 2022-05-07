package me.app.coinwallet.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;

public class RestoreMnemonicAdapter extends BaseAdapter<String, RestoreMnemonicAdapter.ViewHolder>{

    public RestoreMnemonicAdapter(OnItemClickListener<String> listener){
        super(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.restore_mnemonic_card,parent,false);
        return new RestoreMnemonicAdapter.ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String label = data.get(position);
        holder.getLabel().setText(label);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView label;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            label = view.findViewById(R.id.mnemonic_label);
        }

        public TextView getLabel() {
            return label;
        }
    }
}
