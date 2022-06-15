package me.app.coinwallet.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import org.bitcoinj.core.Transaction;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AcceptBluetoothThread extends Thread{
    protected final BluetoothServerSocket listeningSocket;
    protected final AtomicBoolean running = new AtomicBoolean(true);

    protected AcceptBluetoothThread(final BluetoothServerSocket listeningSocket) {
        this.listeningSocket = listeningSocket;
    }

    public void stopAccepting() {
        running.set(false);

        try {
            listeningSocket.close();
        } catch (final IOException x) {
            // swallow
        }
    }

    protected abstract boolean handleTx(Transaction tx);
}
