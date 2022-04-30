package me.app.coinwallet.activities;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.ConfirmDialog;
import me.app.coinwallet.R;
import me.app.coinwallet.adapters.AddressBookAdapter;
import me.app.coinwallet.addressbook.AddressBookEntry;
import me.app.coinwallet.viewmodels.TransferPageViewModel;

public class TransferActivity extends AppCompatActivity implements AddressBookAdapter.OnItemClickListener {

    EditText sendAddress;
    EditText sendAmount;
    Button sendBtn;
    Button addAddressBtn;
    TextView balance;
    RecyclerView addressBook;
    TransferPageViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        sendAddress = findViewById(R.id.send_address_text);
        sendAmount = findViewById(R.id.send_text);
        sendBtn = findViewById(R.id.send_button);
        balance = findViewById(R.id.balance);
        addressBook = findViewById(R.id.address_book);
        addAddressBtn = findViewById(R.id.add_address_book_button);
        viewModel = new ViewModelProvider(this).get(TransferPageViewModel.class);
        viewModel.getBalance().observe(this,s -> balance.setText(s));
        sendBtn.setOnClickListener(v->viewModel.send(sendAddress.getText().toString(), sendAmount.getText().toString()));
        addAddressBtn.setOnClickListener(v -> showAddContactDialog());
        AddressBookAdapter adapter = new AddressBookAdapter(this);
        addressBook.setAdapter(adapter);
        addressBook.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        viewModel.getAddressBook().observe(this, adapter::updateAddressBook);
    }

    public void showAddContactDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.add_address_dialog, null);
        EditText labelText = dialogView.findViewById(R.id.address_label_text);
        ConfirmDialog dialog = new ConfirmDialog(dialogView,
                () -> viewModel.saveToAddressBook(labelText.getText().toString(), sendAddress.getText().toString()));
        dialog.show(getSupportFragmentManager(), "add_address");
    }

    @Override
    public void onClick(AddressBookEntry addressBookEntry) {
        sendAddress.setText(addressBookEntry.getAddress());
    }
}