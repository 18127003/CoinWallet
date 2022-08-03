package me.app.coinwallet.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.Constants;
import me.app.coinwallet.bitcoinj.LocalWallet;

public class PaymentRequestViewModel extends AndroidViewModel {
    MutableLiveData<String> uri = new MutableLiveData<>();
    LocalWallet localWallet = LocalWallet.getInstance();

    public PaymentRequestViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getUri() {
        return uri;
    }

    public void generateUri(double amount, boolean useBluetooth){
        StringBuilder uriBuilder = new StringBuilder(localWallet.generatePaymentRequest(amount, "Payment request", "message"));
        if (useBluetooth){
            uriBuilder.append("&");
            uriBuilder.append(Constants.BT_ENABLED_PARAM);
            uriBuilder.append("=true");
        }
        uri.postValue(uriBuilder.toString());
    }
}
