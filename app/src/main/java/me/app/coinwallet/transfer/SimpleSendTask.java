package me.app.coinwallet.transfer;

import android.util.Log;
import android.widget.Toast;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.bitcoinj.LocalWallet;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

public abstract class SimpleSendTask extends SendTask{
    private final LocalWallet localWallet = LocalWallet.getInstance();

    public SimpleSendTask(Configuration configuration){
        super(configuration);
    }

    @Override
    void onSend(SendRequest sendRequest, String password) {
        try{
            Log.e("HD",sendRequest.tx.getOutput(0).getValue().toFriendlyString());
            Transaction tx = localWallet.send(sendRequest, password);
            callbackHandler.post(()->onSuccess(tx));
        } catch (InsufficientMoneyException e){
            final Coin missing = e.missing;
            String m = missing==null?"coins": missing.toFriendlyString();
            configuration.toastUtil.postToast("Insufficient money, missing "+m, Toast.LENGTH_SHORT);
        } catch (Wallet.DustySendRequested | Wallet.ExceededMaxTransactionSize d){
            configuration.toastUtil.postToast("Send failed due to invalid request", Toast.LENGTH_SHORT);
        } catch (Wallet.CouldNotAdjustDownwards n) {
            configuration.toastUtil.postToast("Attempt to send on empty wallet", Toast.LENGTH_SHORT);
        } catch (Wallet.BadWalletEncryptionKeyException ke){
            configuration.toastUtil.postToast("Wrong password", Toast.LENGTH_SHORT);
        }
    }
}
