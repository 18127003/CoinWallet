package me.app.coinwallet.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.data.livedata.WalletLiveData;
import me.app.coinwallet.transfer.PaymentRequest;
import org.bitcoinj.uri.BitcoinURIParseException;

public class ScanQrPageViewModel extends AndroidViewModel {
    private final WalletLiveData walletLiveData;
    private final LocalWallet localWallet = LocalWallet.getInstance();

    public ScanQrPageViewModel(@NonNull Application application) {
        super(application);
        walletLiveData = WalletLiveData.get();
    }

    public LiveData<String> getAddress() {
        return walletLiveData.getCurrentReceivingAddress();
    }

    public LiveData<String> getBalance(){ return walletLiveData.getAvailableBalance();}

    public PaymentRequest paymentRequestFromQr(String qr) throws BitcoinURIParseException {
        return PaymentRequest.from(qr, localWallet.parameters());
    }
}
