package me.app.coinwallet.transfer;

import android.os.Handler;
import android.os.Looper;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.Constants;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.util.concurrent.Executor;

public abstract class SendTask {
    protected final Configuration configuration;
    protected final Handler callbackHandler;

    protected SendTask(final Configuration configuration){
        this.configuration = configuration;
        callbackHandler = new Handler(Looper.getMainLooper());
    }

    public void send(SendRequest sendRequest, String password){
        configuration.executorService.execute(()-> {
            org.bitcoinj.core.Context.propagate(Constants.BITCOIN_CONTEXT);
            onSend(sendRequest, password);
        });
    }

    abstract void onSend(SendRequest sendRequest, String password);

    protected void onSuccess(Transaction transaction){}
//
//    void onInsufficientMoney(Coin missing);
//
//    void onInvalidEncryptionKey();
//
//    default void onEmptyWalletFailed() {
//        onFailure(new Wallet.CouldNotAdjustDownwards());
//    }
//
//    void onFailure(Exception exception);
}
