package me.app.coinwallet.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.addressbook.AddressBookEntry;

public class AddressBookAdapter extends BaseAdapter<AddressBookEntry, AddressBookAdapter.ViewHolder> {

    public AddressBookAdapter(OnItemClickListener<AddressBookEntry> listener){
        super(listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_book_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressBookEntry entry = data.get(position);
        holder.getLabel().setText(entry.getLabel());
        holder.getLabel().setOnClickListener(v -> listener.onClick(entry));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView label;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            label = view.findViewById(R.id.address_label);
        }

        public TextView getLabel() {
            return label;
        }
    }
}
