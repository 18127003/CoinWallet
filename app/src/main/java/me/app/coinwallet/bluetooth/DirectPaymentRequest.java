package me.app.coinwallet.bluetooth;

import android.os.Handler;
import android.os.Looper;
import org.bitcoin.protocols.payments.Protos;

import java.util.concurrent.Executor;

public abstract class DirectPaymentRequest {
    private final ResultCallback resultCallback;
    private final Handler callbackHandler;

    protected DirectPaymentRequest(ResultCallback resultCallback) {
        this.callbackHandler = new Handler(Looper.myLooper());
        this.resultCallback = resultCallback;
    }

    public interface ResultCallback {
        void onResult(boolean ack);

        void onFail(int messageResId, Object... messageArgs);
    }

    public abstract void send(Protos.Payment payment);

    protected void onResult(final boolean ack) {
        callbackHandler.post(() -> resultCallback.onResult(ack));
    }

    protected void onFail(final int messageResId, final Object... messageArgs) {
        callbackHandler.post(() -> resultCallback.onFail(messageResId, messageArgs));
    }
}
