package me.app.coinwallet.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.R;
import me.app.coinwallet.data.addressbook.AddressBookEntry;

import java.util.ArrayList;
import java.util.List;

public class AddressBookAdapter extends RecyclerView.Adapter<AddressBookAdapter.ViewHolder> {

    private List<AddressBookEntry> addressBookEntries;
    private final OnItemClickListener onClickListener;

    public AddressBookAdapter(OnItemClickListener onClickListener){
        addressBookEntries = new ArrayList<>();
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_book_card,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressBookEntry entry = addressBookEntries.get(position);
        holder.getLabel().setText(entry.getLabel());
        holder.getLabel().setOnClickListener(v -> onClickListener.onClick(entry));
    }

    @Override
    public int getItemCount() {
        return addressBookEntries.size();
    }

    public void updateAddressBook(final List<AddressBookEntry> newAddressBooks){
        Log.e("HD", "Update address book " + newAddressBooks.size());
        addressBookEntries.clear();
        addressBookEntries = newAddressBooks;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onClick(AddressBookEntry addressBookEntry);
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
