package me.app.coinwallet.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import me.app.coinwallet.Constants;
import me.app.coinwallet.utils.BluetoothUtil;
import org.bitcoinj.core.ProtocolException;
import org.bitcoinj.core.Transaction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/***
 * Bluetooth server thread
 */
public abstract class SimpleBluetoothThread extends AcceptBluetoothThread{
    public SimpleBluetoothThread(final BluetoothAdapter adapter) throws IOException {
        super(adapter.listenUsingInsecureRfcommWithServiceRecord(BluetoothUtil.CLASSIC_PAYMENT_PROTOCOL_NAME,
                BluetoothUtil.CLASSIC_PAYMENT_PROTOCOL_UUID));
    }

    @Override
    public void run() {
        org.bitcoinj.core.Context.propagate(Constants.BITCOIN_CONTEXT);

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
                        final Transaction tx = new Transaction(Constants.NETWORK_PARAMETERS, msg);

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
}
