package me.app.coinwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.data.livedata.WalletLiveData;
import me.app.coinwallet.data.transaction.MonthlyReport;
import java.util.List;

public class HomePageViewModel extends AndroidViewModel {
    private final WalletLiveData walletLiveData;

    public LiveData<String> getBalance(){ return walletLiveData.getAvailableBalance(); }

    public MutableLiveData<List<MonthlyReport>> getMonthlyReports() {
        return walletLiveData.getMonthlyReports();
    }

    public LiveData<String> getAddress(){return walletLiveData.getCurrentReceivingAddress();}

    public HomePageViewModel(Application application){
        super(application);
        walletLiveData = WalletLiveData.get();
    }

}
