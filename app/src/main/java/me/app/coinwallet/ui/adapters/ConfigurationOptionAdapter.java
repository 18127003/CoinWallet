package me.app.coinwallet.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import me.app.coinwallet.R;
import me.app.coinwallet.data.configuration.ConfigurationOption;

public class ConfigurationOptionAdapter <T> extends BaseAdapter<ConfigurationOption<T>, ConfigurationOptionAdapter.ViewHolder> {
    public ConfigurationOptionAdapter(OnItemClickListener<ConfigurationOption<T>> listener, T selected) {
        super(listener);
        this.selected = selected;
    }
    private final T selected;
    private int selectedColor;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.configuration_option_item,parent,false);
        selectedColor = MaterialColors.getColor(parent, R.attr.colorOnSecondary);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConfigurationOption<T> option = data.get(position);
        holder.label.setText(option.label);
        if(option.code.equals(selected)){
            holder.label.setTextColor(selectedColor);
        }
        holder.card.setOnClickListener(
                v -> {
                    listener.onClick(option);
                    holder.label.setTextColor(selectedColor);
                }
        );
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView label;
        final MaterialCardView card;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            label = view.findViewById(R.id.option_label);
            card = view.findViewById(R.id.option_card);
        }
    }
}
