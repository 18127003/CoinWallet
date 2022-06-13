package me.app.coinwallet.ui.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.utils.BluetoothUtil;
import me.app.coinwallet.utils.QRUtil;
import me.app.coinwallet.viewmodels.PaymentQrReceiveViewModel;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.uri.BitcoinURIParseException;

import java.io.IOException;

public class PaymentQrFragment extends Fragment {

    private ImageView qrCode;
    private TextView bluetoothNotify;
    private BluetoothHandler bluetoothHandler;
    private PaymentQrReceiveViewModel viewModel;

    public PaymentQrFragment() {
        // Required empty public constructor
    }

    public static PaymentQrFragment newInstance() {
        return new PaymentQrFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
        bluetoothHandler = new BluetoothHandler(this, new BluetoothHandler.BluetoothEnableCallback() {
            @Override
            public void onSucceed() {
                try {
                    viewModel.initBtThread(bluetoothAdapter);
                } catch (IOException ioException) {
                    requireActivity().finish();
                }
                viewModel.startBtThread();
            }

            @Override
            public void onRejected() {
                requireActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qrCode = view.findViewById(R.id.qr_code_img);
        bluetoothNotify = view.findViewById(R.id.bluetooth_notify);
        bluetoothHandler.enableBluetooth();
        viewModel = new ViewModelProvider(requireActivity()).get(PaymentQrReceiveViewModel.class);
        Intent intent = requireActivity().getIntent();
        String uriString = intent.getStringExtra("uri");
        try {
            BitcoinURI uri = new BitcoinURI(uriString);
            Bitmap bm = QRUtil.createQRCodeBitmap(uriString);
            if (bm != null) {
                qrCode.setImageBitmap(Bitmap.createScaledBitmap(bm,
                        Constants.QR_BITMAP_SCALE_WIDTH, Constants.QR_BITMAP_SCALE_HEIGHT, false));
            }
            boolean useBluetooth = Boolean.parseBoolean((String) uri.getParameterByName(BluetoothUtil.MAC_URI_PARAM));
            if(useBluetooth){
                bluetoothHandler.enableBluetooth();
                bluetoothNotify.setText("Please stay on this screen until you received the payment");
            }
        } catch (BitcoinURIParseException e) {
            requireActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        if (viewModel!=null){
            viewModel.stopBtThread();
        }
        super.onDestroy();
    }
}