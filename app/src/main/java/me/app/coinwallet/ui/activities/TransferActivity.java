package me.app.coinwallet.ui.activities;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.R;
import me.app.coinwallet.ui.adapters.AddressBookAdapter;
import me.app.coinwallet.data.addressbook.AddressBookEntry;
import me.app.coinwallet.ui.dialogs.CustomDialog;
import me.app.coinwallet.viewmodels.TransferPageViewModel;

public class TransferActivity extends AppCompatActivity implements BaseAdapter.OnItemClickListener<AddressBookEntry> {

    EditText sendAddress;
    EditText sendAmount;
    Button sendBtn;
    Button addAddressBtn;
    TextView balance;
    TextView expectedBalance;
    RecyclerView addressBook;
    TransferPageViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        sendAddress = findViewById(R.id.send_address_text);
        sendAmount = findViewById(R.id.send_text);
        sendBtn = findViewById(R.id.send_button);
        balance = findViewById(R.id.balance_text);
        expectedBalance = findViewById(R.id.expected_balance);
        addressBook = findViewById(R.id.address_book);
        addAddressBtn = findViewById(R.id.add_address_book_button);
        viewModel = new ViewModelProvider(this).get(TransferPageViewModel.class);
        viewModel.getBalance().observe(this,s -> balance.setText(s));
        viewModel.getExpectedBalance().observe(this, s -> expectedBalance.setText(s));
        sendBtn.setOnClickListener(v -> sendPasswordDialog());
        addAddressBtn.setOnClickListener(v -> showAddContactDialog());
        AddressBookAdapter adapter = new AddressBookAdapter(this);
        addressBook.setAdapter(adapter);
        addressBook.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        viewModel.getAddressBook().observe(this, adapter::update);
    }

    public void showAddContactDialog(){
        ConfirmDialog dialog = CustomDialog.labelDialog(getLayoutInflater(),
                (label)-> viewModel.saveToAddressBook(label, sendAddress.getText().toString()));
        dialog.show(getSupportFragmentManager(), "add_address");
    }

    void sendPasswordDialog(){
        final String sendAddressText = sendAddress.getText().toString();
        final String sendAmountText =  sendAmount.getText().toString();
        if(viewModel.isWalletEncrypted()){
            ConfirmDialog dialog = CustomDialog.passwordDialog(getLayoutInflater(),
                    (password) -> viewModel.send(sendAddressText, sendAmountText, password));
            dialog.show(getSupportFragmentManager(), "password_send");
        } else {
            viewModel.send(sendAddressText, sendAddressText, null);
        }
    }

    @Override
    public void onClick(AddressBookEntry addressBookEntry) {
        sendAddress.setText(addressBookEntry.getAddress());
    }
}