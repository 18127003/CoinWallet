package me.app.coinwallet.ui.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.MutableLiveData;
import com.google.android.material.card.MaterialCardView;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.transfer.SendMethod;

public class SelectSendMethodFragment extends Fragment {
    MaterialCardView defaultCard;
    MaterialCardView offlineCard;
    MaterialCardView bluetoothCard;
    ImageView defaultIc;
    ImageView offlineIc;
    ImageView bluetoothIc;
    Button selectConfirm;
    private BluetoothHandler bluetoothHandler;
    private final MutableLiveData<SendMethod> sendMethod = new MutableLiveData<>();

    public SelectSendMethodFragment() {
        // Required empty public constructor
    }

    public static SelectSendMethodFragment newInstance() {
        return new SelectSendMethodFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bluetoothHandler = new BluetoothHandler(this, new BluetoothHandler.BluetoothEnableCallback() {
            @Override
            public void onSucceed() {
                bluetoothHandler.getPairingDialog(device->{
                    sendMethod.postValue(SendMethod.getBluetooth(device));
                }).show(requireActivity().getSupportFragmentManager(),"pair_devices");
            }

            @Override
            public void onRejected() {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_send_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        defaultCard = view.findViewById(R.id.send_default_card);
        offlineCard = view.findViewById(R.id.send_offline_card);
        bluetoothCard = view.findViewById(R.id.send_bluetooth_card);
        selectConfirm = view.findViewById(R.id.select_confirm);
        defaultIc = view.findViewById(R.id.default_selected_icon);
        offlineIc = view.findViewById(R.id.offline_selected_icon);
        bluetoothIc = view.findViewById(R.id.bluetooth_selected_icon);
        Intent intent = requireActivity().getIntent();
        sendMethod.setValue(intent.getParcelableExtra(Constants.SEND_METHOD_EXTRA_NAME));
        sendMethod.observe(this, s->{
            switch (s.method){
                case DEFAULT:
                    defaultIc.setVisibility(View.VISIBLE);
                    offlineIc.setVisibility(View.GONE);
                    bluetoothIc.setVisibility(View.GONE);
                    break;
                case OFFLINE:
                    defaultIc.setVisibility(View.GONE);
                    offlineIc.setVisibility(View.VISIBLE);
                    bluetoothIc.setVisibility(View.GONE);
                    break;
                case BLUETOOTH:
                    defaultIc.setVisibility(View.GONE);
                    offlineIc.setVisibility(View.GONE);
                    bluetoothIc.setVisibility(View.VISIBLE);
                    break;
            }
        });
        defaultCard.setOnClickListener(v->sendMethod.setValue(SendMethod.getDefault()));
        offlineCard.setOnClickListener(v->sendMethod.setValue(new SendMethod(SendMethod.Method.OFFLINE)));
        bluetoothCard.setOnClickListener(v->bluetoothHandler.enableBluetooth());
        selectConfirm.setOnClickListener(v->{
            Intent result = new Intent();
            result.putExtra(Constants.SEND_METHOD_EXTRA_NAME, sendMethod.getValue());
            requireActivity().setResult(Activity.RESULT_OK, result);
            requireActivity().finish();
        });
    }
}