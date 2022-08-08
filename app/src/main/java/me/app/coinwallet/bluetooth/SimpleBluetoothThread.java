package me.app.coinwallet.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import me.app.coinwallet.Constants;
import me.app.coinwallet.bitcoinj.LocalWallet;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.Transaction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/***
 * Bluetooth server thread
 */
public abstract class SimpleBluetoothThread extends Thread{
    protected final BluetoothServerSocket listeningSocket;
    protected final AtomicBoolean running = new AtomicBoolean(true);

    public SimpleBluetoothThread(final BluetoothAdapter adapter) throws IOException {
        listeningSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(Constants.BLUETOOTH_PAYMENT_PROTOCOL_NAME,
                Constants.BLUETOOTH_PAYMENT_PROTOCOL_UUID);
    }

    @Override
    public void run() {
        final LocalWallet localWallet = LocalWallet.getInstance();
        org.bitcoinj.core.Context.propagate(localWallet.getContext());

        while (running.get()) {
            try ( // start a blocking call, and return only on success or exception
                  final BluetoothSocket socket = listeningSocket.accept();
                  final DataInputStream is = new DataInputStream(socket.getInputStream());
                  final DataOutputStream os = new DataOutputStream(socket.getOutputStream())) {
                Log.i("HD","accepted classic bluetooth connection");

                boolean ack = true;

                final int numMessages = is.readInt();

                for (int i = 0; i < numMessages; i++) {
                    final int msgLength = is.readInt();
                    final byte[] msg = new byte[msgLength];
                    is.readFully(msg);

                    try {
                        final Transaction tx = new Transaction(localWallet.parameters(), msg);

                        if (!handleTx(tx))
                            ack = false;
                    } catch (final ProtocolException x) {
                        Log.e("HD","cannot decode message received via bluetooth");
                        ack = false;
                    }
                }

                os.writeBoolean(ack);
            } catch (final IOException x) {
                Log.e("HD","exception in bluetooth accept loop");
            }
        }
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
