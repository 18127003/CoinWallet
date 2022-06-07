package me.app.coinwallet.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import me.app.coinwallet.R;
import me.app.coinwallet.bluetooth.AcceptBluetoothThread;
import me.app.coinwallet.bluetooth.SimpleBluetoothThread;
import me.app.coinwallet.viewmodels.BluetoothPaymentViewModel;
import org.bitcoinj.core.Transaction;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class BluetoothPaymentFragment extends Fragment {

    private BluetoothPaymentViewModel viewModel;

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()== Activity.RESULT_OK){
                    Log.e("HD","Bluetooth enabled");
                    viewModel.startBtThread();
                } else {
                    requireActivity().finish();
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionAndroid12 = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(!result){
                    requireActivity().finish();
                }
            }
    );

    public BluetoothPaymentFragment() {
        // Required empty public constructor
    }

    public static BluetoothPaymentFragment newInstance() {

        return new BluetoothPaymentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) requireActivity().getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
        if (bluetoothAdapter == null){
            return;
        }
        viewModel = new ViewModelProvider(requireActivity()).get(BluetoothPaymentViewModel.class);
        try {
            viewModel.initBtThread(bluetoothAdapter);
        } catch (IOException ioException) {
            requireActivity().finish();
        }

        if (!bluetoothAdapter.isEnabled()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                requestPermissionAndroid12.launch(Manifest.permission.BLUETOOTH_CONNECT);
            }
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            launcher.launch(enableBtIntent);
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