package me.app.coinwallet.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import me.app.coinwallet.ui.dialogs.BluetoothPairingDialog;
import me.app.coinwallet.utils.BluetoothUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class BluetoothHandler {
    final BluetoothAdapter bluetoothAdapter;
    final Fragment fragment;
    final BluetoothEnableCallback callback;
    private BluetoothStateChangeCallback stateChangeCallback;
    private final ActivityResultLauncher<Intent> launcher;
    private final ActivityResultLauncher<String> requestPermission;

    public BluetoothHandler(Fragment fragment, BluetoothEnableCallback callback){
        this.fragment = fragment;
        this.callback = callback;
        bluetoothAdapter = ((BluetoothManager) fragment.requireContext().getSystemService(Context.BLUETOOTH_SERVICE))
                .getAdapter();
        launcher = fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Log.e("HD","Bluetooth enabled");
                        callback.onSucceed();
                    } else {
                        callback.onRejected();
                    }
                }
        );
        requestPermission = fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                result -> {
                    if(!result){
                        callback.onRejected();
                    } else {
                        requestBluetoothEnable();
                    }
                }
        );
    }

    public interface BluetoothEnableCallback{
        default void onSucceed(){}
        default void onRejected(){}
    }

    public interface BluetoothStateChangeCallback{
        void onBluetoothTurnOff();
    }

    public void enableBluetooth(){
        if (bluetoothAdapter == null){
            callback.onRejected();
            return;
        }

        if (!bluetoothAdapter.isEnabled()){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                requestPermission.launch(Manifest.permission.BLUETOOTH_CONNECT);
            } else {
                requestBluetoothEnable();
            }
        } else {
            callback.onSucceed();
        }
    }

    private void requestBluetoothEnable(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        launcher.launch(enableBtIntent);
    }

    final BroadcastReceiver btStateChangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_TURNING_OFF) {
                    stateChangeCallback.onBluetoothTurnOff();
                }
            }
        }
    };

    public void registerReceiver(BluetoothStateChangeCallback stateChangeCallback){
        this.stateChangeCallback = stateChangeCallback;
        fragment.requireActivity().registerReceiver(btStateChangeListener, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    public void unregisterReceiver(){
        fragment.requireActivity().unregisterReceiver(btStateChangeListener);
    }

    public List<BluetoothDevice> getPairedDevices(){
        return new ArrayList<>(bluetoothAdapter.getBondedDevices());
    }

    public BluetoothPairingDialog getPairingDialog(BluetoothPairingDialog.PairedDeviceSelectedCallback callback){
        return new BluetoothPairingDialog(getPairedDevices(), callback);
    }
}
