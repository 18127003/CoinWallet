package me.app.coinwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.data.livedata.WalletLiveData;
import me.app.coinwallet.data.marketcap.MarketCapEntry;
import me.app.coinwallet.data.marketcap.MarketCapRepository;
import me.app.coinwallet.data.transaction.MonthlyReport;
import me.app.coinwallet.data.transaction.TransactionWrapper;

import java.util.List;

public class HomePageViewModel extends AndroidViewModel {
    private final WalletLiveData walletLiveData;
    private final LiveData<List<MarketCapEntry>> trendLiveData;

    public LiveData<String> getBalance(){ return walletLiveData.getAvailableBalance(); }

    public MutableLiveData<List<MonthlyReport>> getMonthlyReports() {
        return walletLiveData.getMonthlyReports();
    }

    public LiveData<String> getAddress(){return walletLiveData.getCurrentReceivingAddress();}

    public LiveData<List<MarketCapEntry>> getTrendLiveData() {
        return trendLiveData;
    }

    public TransactionWrapper getLatestTx(){
        return walletLiveData.getLatestTx();
    }

    public HomePageViewModel(Application application){
        super(application);
        MarketCapRepository repository = MarketCapRepository.get(application);
        repository.queryTrends();
        trendLiveData = repository.getTrends();
        walletLiveData = WalletLiveData.get();
    }

}
