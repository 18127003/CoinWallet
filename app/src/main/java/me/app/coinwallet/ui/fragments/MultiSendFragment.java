package me.app.coinwallet.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.transfer.PaymentRequest;
import me.app.coinwallet.transfer.SendMethod;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.activities.SingleFragmentActivity;
import me.app.coinwallet.utils.Utils;
import me.app.coinwallet.viewmodels.TransferPageViewModel;
import org.bitcoinj.core.Coin;
import org.bitcoinj.signers.CustomTransactionSigner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiSendFragment extends Fragment {
    Button addRecipientBtn;
    Button sendBtn;
    int editing;
    List<RecipientView> recipientViews = new ArrayList<>();
    TransferPageViewModel viewModel;
    private AuthenticateHandler authenticateHandler;

    public MultiSendFragment() {
        // Required empty public constructor
    }

    public static MultiSendFragment newInstance() {
        return new MultiSendFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        authenticateHandler = new AuthenticateHandler(this, new AuthenticateHandler.AuthenticateResultCallback() {
            @Override
            public void onPasswordVerified(String password) {
                List<TransferPageViewModel.Recipient> recipients = recipientViews.stream()
                        .filter(recipientView -> !Utils.emptyTextField(recipientView.address.getText())
                                && !Utils.emptyTextField(recipientView.amount.getText()))
                        .map(recipientView -> new TransferPageViewModel.Recipient(
                                recipientView.address.getText().toString(), recipientView.amount.getText().toString()))
                        .collect(Collectors.toList());
                viewModel.send(recipients, password);
            }

            @Override
            public void onPasswordDenied() {
                Configuration.get().toastUtil.postToast("Wrong password", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_multi_send, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TransferPageViewModel.class);
        addRecipientBtn = view.findViewById(R.id.more_recipient_btn);
        sendBtn = view.findViewById(R.id.send_button);
        LinearLayout recipientsLayout = view.findViewById(R.id.recipients);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View defaultRecipient = recipientsLayout.getChildAt(0);
        handleRecipient(defaultRecipient, 0);
        addRecipientBtn.setOnClickListener((v)->{
            View recipient = getLayoutInflater().inflate(R.layout.recipient_info, null);
            handleRecipient(recipient, recipientsLayout.getChildCount());
            recipientsLayout.addView(recipient, layoutParams);
        });
        sendBtn.setOnClickListener(v->{
            authenticateHandler.accessPasswordDialog();
        });
    }

    void handleRecipient(View recipient, int index){
        TextInputLayout addressLayout = recipient.findViewById(R.id.address_layout);
        TextInputEditText amount = recipient.findViewById(R.id.amount);
        TextInputEditText address = recipient.findViewById(R.id.address);
        addressLayout.setEndIconOnClickListener(e->{
            editing = index;
            scanRecipientLauncher.launch(null);
        });
        recipientViews.add(new RecipientView(address, amount, index));
    }

    private static class RecipientView{
        TextInputEditText address;
        TextInputEditText amount;
        int index;
        public RecipientView(TextInputEditText address, TextInputEditText amount, int index){
            this.address = address;
            this.amount = amount;
            this.index = index;
        }
        public void update(PaymentRequest paymentRequest){
            address.setText(paymentRequest.getAddress().toString());
            if(paymentRequest.hasAmount()){
                amount.setText(paymentRequest.getAmount().toBtc().toPlainString());
            }
        }
    }

    private final ActivityResultLauncher<PaymentRequest> scanRecipientLauncher = registerForActivityResult(
            new ActivityResultContract<PaymentRequest, PaymentRequest>() {
                @NonNull
                @Override
                public Intent createIntent(@NonNull Context context, PaymentRequest input) {
                    Intent intent = SingleFragmentActivity.newActivity(context, ScanQrFragment.class, R.string.scan_qr_page_label);
                    intent.putExtra("is_return", true);
                    return intent;
                }

                @Override
                public PaymentRequest parseResult(int resultCode, @Nullable Intent intent) {
                    if(resultCode == Activity.RESULT_OK && intent!=null){
                        return intent.getParcelableExtra(Constants.QR_CONTENT);
                    }
                    return null;
                }
            },
            new ActivityResultCallback<PaymentRequest>() {
                @Override
                public void onActivityResult(PaymentRequest result) {
                    if(result!=null){
                        RecipientView recipientView = recipientViews.get(editing);
                        recipientView.update(result);
                    }
                }
            }
    );
}