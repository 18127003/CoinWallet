package me.app.coinwallet.viewmodels;

import android.bluetooth.BluetoothAdapter;
import androidx.lifecycle.ViewModel;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.bluetooth.SimpleBluetoothThread;
import org.bitcoinj.core.Transaction;

import java.io.IOException;

public class PaymentQrReceiveViewModel extends ViewModel {
    private SimpleBluetoothThread bluetoothThread;
    private final LocalWallet localWallet = LocalWallet.getInstance();

    public void initBtThread(BluetoothAdapter bluetoothAdapter) throws IOException {
        bluetoothThread = new SimpleBluetoothThread(bluetoothAdapter) {
            @Override
            protected boolean handleTx(Transaction tx) {
                return localWallet.handleBluetoothReceivedTx(tx);
            }
        };
    }

    public void startBtThread(){
        if(bluetoothThread!=null)
            bluetoothThread.start();
    }

    public void stopBtThread(){
        if(bluetoothThread!=null)
            bluetoothThread.stopAccepting();
    }
}
