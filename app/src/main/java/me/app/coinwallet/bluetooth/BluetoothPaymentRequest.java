package me.app.coinwallet.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import me.app.coinwallet.R;
import me.app.coinwallet.utils.BluetoothUtil;
import org.bitcoin.protocols.payments.Protos;
import org.bitcoinj.protocols.payments.PaymentProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;

public class BluetoothPaymentRequest extends DirectPaymentRequest{
    private final BluetoothDevice bluetoothDevice;
    private final Executor executor;

    public BluetoothPaymentRequest(final Executor executor, final ResultCallback resultCallback,
                                   final BluetoothDevice bluetoothDevice) {
        super(resultCallback);
        this.executor = executor;
        this.bluetoothDevice = bluetoothDevice;
    }

    @Override
    public void send(final Protos.Payment payment) {
        executor.execute(() -> {
            Log.i("HD","trying to send tx via bluetooth "+ bluetoothDevice.getName());

            if (payment.getTransactionsCount() != 1)
                throw new IllegalArgumentException("wrong transactions count");

            try (final BluetoothSocket socket =
                         bluetoothDevice.createInsecureRfcommSocketToServiceRecord(BluetoothUtil.CLASSIC_PAYMENT_PROTOCOL_UUID)) {
                socket.connect();
                Log.i("HD","connected to payment protocol "+ bluetoothDevice.getAddress());
                final DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                final DataInputStream is = new DataInputStream(socket.getInputStream());

                payment.writeDelimitedTo(os);
                os.flush();
                Log.i("HD","tx sent via bluetooth");

                final Protos.PaymentACK paymentAck = Protos.PaymentACK.parseDelimitedFrom(is);
                final boolean ack = "ack".equals(PaymentProtocol.parsePaymentAck(paymentAck).getMemo());
                Log.i("HD","received "+ (ack ? "ack" : "nack") +" via bluetooth" );

                onResult(ack);
            } catch (final IOException x) {
                Log.e("HD","problem sending");

                onFail(R.string.fail, x.getMessage());
            }
        });
    }
}
