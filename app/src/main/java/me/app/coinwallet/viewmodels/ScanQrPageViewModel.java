package me.app.coinwallet.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import me.app.coinwallet.data.livedata.WalletLiveData;

public class ScanQrPageViewModel extends AndroidViewModel {
    private final WalletLiveData walletLiveData;

    public ScanQrPageViewModel(@NonNull Application application) {
        super(application);
        walletLiveData = WalletLiveData.get();
    }

    public LiveData<String> getAddress() {
        return walletLiveData.getCurrentReceivingAddress();
    }

    public LiveData<String> getBalance(){ return walletLiveData.getAvailableBalance();}
}
