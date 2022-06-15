package me.app.coinwallet.ui.dialogs;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import me.app.coinwallet.R;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BluetoothPairingDialog extends DialogFragment {
    private final List<BluetoothDevice> pairedDevices;
    private final PairedDeviceSelectedCallback callback;

    public BluetoothPairingDialog(List<BluetoothDevice> pairedDevices, PairedDeviceSelectedCallback callback){
        this.pairedDevices = pairedDevices;
        this.callback = callback;
    }

    public interface PairedDeviceSelectedCallback{
        void onSelected(BluetoothDevice device);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        String[] pairedArray = pairedDevices.stream().map(BluetoothDevice::getName).toArray(String[]::new);
        builder.setTitle(R.string.select_paired_device)
                .setItems(pairedArray, (dialog, pos)-> callback.onSelected(pairedDevices.get(pos)));
        return builder.create();
    }
}
