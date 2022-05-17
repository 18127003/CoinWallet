package me.app.coinwallet.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.data.addressbook.AddressBookEntry;
import me.app.coinwallet.ui.adapters.AddressBookAdapter;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.SingleTextFieldDialog;
import me.app.coinwallet.viewmodels.TransferPageViewModel;

public class TransferFragment extends AuthenticateFragment implements BaseAdapter.OnItemClickListener<AddressBookEntry>{

    private TextInputEditText addressText;
    private TextInputEditText amountText;
    private SwitchMaterial saveContactSwitch;
    private View saveContactView;
    private FrameLayout saveContactLayout;
    private RecyclerView addressBook;
    private Button sendBtn;
    private TransferPageViewModel viewModel;

    public TransferFragment() {
        // Required empty public constructor
    }

    public static TransferFragment newInstance() {
        return new TransferFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TransferPageViewModel.class);
        addressBook = view.findViewById(R.id.address_book_list);
        addressText = view.findViewById(R.id.address_text_field);
        amountText = view.findViewById(R.id.amount_text_field);
        saveContactSwitch = view.findViewById(R.id.save_contact_switch);
        sendBtn = view.findViewById(R.id.send_button);
        saveContactLayout = view.findViewById(R.id.save_contact_layout);
        sendBtn.setOnClickListener(v -> accessPasswordDialog());
        saveContactView = LayoutInflater.from(getContext()).inflate(R.layout.save_contact_view, saveContactLayout, false);
        AddressBookAdapter adapter = new AddressBookAdapter(this);
        addressBook.setAdapter(adapter);
        addressBook.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Intent intent = requireActivity().getIntent();
        String address = intent.getStringExtra(Constants.SEND_TO_ADDRESS_EXTRA_NAME);
        if(address!=null){
            addressText.setText(address);
        }
        viewModel.getAddressBook().observe(requireActivity(), adapter::update);
        saveContactSwitch.setOnCheckedChangeListener((v, isChecked)->{
            if(isChecked) {
                saveContactLayout.addView(saveContactView);
            } else {
                saveContactLayout.removeView(saveContactView);
            }
        });
    }

    @Override
    protected void onPasswordVerified(String password) {
        final String sendAddressText = addressText.getText().toString();
        final String sendAmountText =  amountText.getText().toString();
        viewModel.send(sendAddressText, sendAmountText, password);
        saveContact(sendAddressText);
    }

    @Override
    protected void onPasswordDenied() {}

   private void saveContact(String address){
        if(saveContactSwitch.isChecked()){
            TextInputEditText addressLabel = saveContactView.findViewById(R.id.address_label_text_field);
            viewModel.saveToAddressBook(addressLabel.getText().toString(), address);
        }
   }

   @Override
   public void onClick(AddressBookEntry addressBookEntry) {
        addressText.setText(addressBookEntry.getAddress());
    }
}