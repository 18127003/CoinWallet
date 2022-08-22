package me.app.coinwallet.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
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

            if (payment.getTransactionsCount() != 1)
                throw new IllegalArgumentException("wrong transactions count");

            try (final BluetoothSocket socket =
                         bluetoothDevice.createInsecureRfcommSocketToServiceRecord(Constants.BLUETOOTH_PAYMENT_PROTOCOL_UUID)) {
                socket.connect();
                final DataOutputStream os = new DataOutputStream(socket.getOutputStream());
                final DataInputStream is = new DataInputStream(socket.getInputStream());

                payment.writeDelimitedTo(os);
                os.flush();

                final Protos.PaymentACK paymentAck = Protos.PaymentACK.parseDelimitedFrom(is);
                final boolean ack = "ack".equals(PaymentProtocol.parsePaymentAck(paymentAck).getMemo());

                onResult(ack);
            } catch (final IOException x) {
                onFail(R.string.fail, x.getMessage());
            }
        });
    }
}
