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
import android.widget.Toast;
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
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.ui.adapters.AddressBookAdapter;
import me.app.coinwallet.transfer.PaymentRequest;
import me.app.coinwallet.viewmodels.TransferPageViewModel;

import java.util.Objects;

public class TransferFragment extends Fragment {

    private TextInputEditText addressText;
    private TextInputEditText amountText;
    private SwitchMaterial saveContactSwitch;
    private View saveContactView;
    private View sendMethodView;
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
                final String sendAddressText = Objects.requireNonNull(addressText.getText()).toString();
                final String sendAmountText =  Objects.requireNonNull(amountText.getText()).toString();
                if(viewModel.paymentRequest != null){
                    if(!viewModel.paymentRequest.hasAmount()){
                        viewModel.paymentRequest = viewModel.paymentRequest.mergeWithEditedValues(sendAmountText);
                    }
                    viewModel.send(password);
                } else {
                    viewModel.send(sendAddressText, sendAmountText, password);
                }
                saveContact(sendAddressText);
                addressText.getText().clear();
                amountText.getText().clear();
            }

            @Override
            public void onPasswordDenied() {
                ((BaseActivity) requireActivity()).configuration.toastUtil.postToast("Wrong password", Toast.LENGTH_SHORT);
            }
        });
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
        sendMethodView= view.findViewById(R.id.send_method_view);
        sendMethodView.setOnClickListener(v->{
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
            amountText.setText(paymentRequest.getAmount().toBtc().toPlainString());
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
                    Intent intent = SingleFragmentActivity.newActivity(context, SelectSendMethodFragment.class, R.string.select_send_method_page_label);
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