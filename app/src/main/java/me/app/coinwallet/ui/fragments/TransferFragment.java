package me.app.coinwallet.ui.fragments;

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
import me.app.coinwallet.R;
import me.app.coinwallet.data.addressbook.AddressBookEntry;
import me.app.coinwallet.ui.adapters.AddressBookAdapter;
import me.app.coinwallet.ui.adapters.BaseAdapter;
import me.app.coinwallet.ui.dialogs.ConfirmDialog;
import me.app.coinwallet.ui.dialogs.CustomDialog;
import me.app.coinwallet.viewmodels.TransferPageViewModel;

public class TransferFragment extends Fragment implements BaseAdapter.OnItemClickListener<AddressBookEntry>{

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
        TransferFragment fragment = new TransferFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        sendBtn.setOnClickListener(v -> sendPasswordDialog());
        saveContactView = LayoutInflater.from(getContext()).inflate(R.layout.save_contact_view, saveContactLayout, false);
        AddressBookAdapter adapter = new AddressBookAdapter(this);
        addressBook.setAdapter(adapter);
        addressBook.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        viewModel.getSendToAddress().observe(requireActivity(), s->addressText.setText(s));
        viewModel.getAddressBook().observe(requireActivity(), adapter::update);
        saveContactSwitch.setOnCheckedChangeListener((v, isChecked)->{
            if(isChecked) {
                saveContactLayout.addView(saveContactView);
            } else {
                saveContactLayout.removeView(saveContactView);
            }
        });
    }

   private void sendPasswordDialog(){
        final String sendAddressText = addressText.getText().toString();
        final String sendAmountText =  amountText.getText().toString();
        saveContact(sendAddressText);
        if(viewModel.isWalletEncrypted()){
            ConfirmDialog dialog = CustomDialog.passwordDialog(getLayoutInflater(),
                    (password) -> viewModel.send(sendAddressText, sendAmountText, password));
            dialog.show(requireActivity().getSupportFragmentManager(), "password_send");
        } else {
            viewModel.send(sendAddressText, sendAmountText, null);
        }
   }

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