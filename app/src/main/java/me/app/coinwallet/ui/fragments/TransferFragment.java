package me.app.coinwallet.ui.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.transfer.SendMethod;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.AddressBookAdapter;
import me.app.coinwallet.transfer.PaymentRequest;
import me.app.coinwallet.viewmodels.TransferPageViewModel;

public class TransferFragment extends Fragment {

    private TextInputEditText addressText;
    private TextInputEditText amountText;
    private SwitchMaterial saveContactSwitch;
    private MaterialCardView sendMethodCard;
    private View saveContactView;
    private FrameLayout saveContactLayout;
    private RecyclerView addressBook;
    private Button sendBtn;
    private TransferPageViewModel viewModel;
    private AuthenticateHandler authenticateHandler;
    private TextView selectedSendMethodHint;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        authenticateHandler = new AuthenticateHandler(this, new AuthenticateHandler.AuthenticateResultCallback() {
            @Override
            public void onPasswordVerified(String password) {
                if(viewModel.paymentRequest != null){
                    viewModel.send(password);
                } else {
                    final String sendAddressText = addressText.getText().toString();
                    final String sendAmountText =  amountText.getText().toString();
                    viewModel.send(sendAddressText, sendAmountText, password);
                    saveContact(sendAddressText);
                }
            }

            @Override
            public void onPasswordDenied() {
                // Toast
            }
        });

//        bluetoothHandler.registerReceiver(() -> bluetoothSwitch.setChecked(false));
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
        sendMethodCard = view.findViewById(R.id.send_method_card);
        selectedSendMethodHint = view.findViewById(R.id.selected_method_hint);
        sendBtn.setOnClickListener(v -> authenticateHandler.accessPasswordDialog());
        saveContactView = LayoutInflater.from(getContext()).inflate(R.layout.save_contact_view, saveContactLayout, false);
        AddressBookAdapter adapter = new AddressBookAdapter(item -> addressText.setText(item.getAddress()));
        addressBook.setAdapter(adapter);
        addressBook.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Intent intent = requireActivity().getIntent();
        PaymentRequest qrContent = intent.getParcelableExtra(Constants.QR_CONTENT);
        handleQrIntent(qrContent);
        viewModel.getAddressBook().observe(requireActivity(), adapter::update);
        saveContactSwitch.setOnCheckedChangeListener((v, isChecked)->{
            if(isChecked) {
                saveContactLayout.addView(saveContactView);
            } else {
                saveContactLayout.removeView(saveContactView);
            }
        });
        sendMethodCard.setOnClickListener(v->{
            selectSendMethodLauncher.launch(viewModel.sendMethod.getValue());
        });
        viewModel.sendMethod.observe(this, s->selectedSendMethodHint.setText(s.toString()));
    }

    private void saveContact(String address){
        if(saveContactSwitch.isChecked()){
            TextInputEditText addressLabel = saveContactView.findViewById(R.id.address_label_text_field);
            viewModel.saveToAddressBook(addressLabel.getText().toString(), address);
        }
    }

    private void handleQrIntent(PaymentRequest paymentRequest){
        if (paymentRequest == null)
            return;
        viewModel.paymentRequest = paymentRequest;
        if(paymentRequest.useBluetooth()){
            viewModel.sendMethod.setValue(new SendMethod(SendMethod.Method.BLUETOOTH));
        }
        if(paymentRequest.hasAddress()){
            addressText.setText(paymentRequest.getAddress().toString());
            addressText.setEnabled(false);
        }
        if(paymentRequest.hasAmount()){
            amountText.setText(String.valueOf(paymentRequest.getAmount().toBtc().doubleValue()));
            amountText.setEnabled(false);
        }
    }

    @Override
    public void onDetach() {
//        bluetoothHandler.unregisterReceiver();
        super.onDetach();
    }

    private final ActivityResultLauncher<SendMethod> selectSendMethodLauncher = registerForActivityResult(
            new ActivityResultContract<SendMethod, SendMethod>() {
                @NonNull
                @Override
                public Intent createIntent(@NonNull Context context, SendMethod input) {
                    Intent intent = new Intent(context, SingleFragmentActivity.class);
                    intent.putExtra(Constants.INIT_FRAGMENT_EXTRA_NAME, SelectSendMethodFragment.class);
                    intent.putExtra(Constants.APP_BAR_TITLE_EXTRA_NAME, "Select Send Method");
                    intent.putExtra(Constants.SEND_METHOD_EXTRA_NAME, input);
                    return intent;
                }

                @Override
                public SendMethod parseResult(int resultCode, @Nullable Intent intent) {
                    if(resultCode == Activity.RESULT_OK && intent!=null){
                        return intent.getParcelableExtra(Constants.SEND_METHOD_EXTRA_NAME);
                    }
                    return SendMethod.getDefault();
                }
            },
            new ActivityResultCallback<SendMethod>() {
                @Override
                public void onActivityResult(SendMethod result) {
                    viewModel.sendMethod.postValue(result);
                }
            }
    );
}