package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.WalletNotificationType;
import me.app.coinwallet.data.livedata.WalletLiveData;
import me.app.coinwallet.data.marketcap.*;
import me.app.coinwallet.data.transaction.MonthlyReport;
import org.bitcoinj.core.Transaction;
import java.util.List;

public class HomePageViewModel extends AndroidViewModel implements LocalWallet.EventListener {
    private final LocalWallet localWallet = LocalWallet.getInstance();
    private final WalletApplication application;
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

    public HomePageViewModel(Application application){
        super(application);
        this.application = (WalletApplication) application;
        localWallet.subscribe(this);
        MarketCapRepository repository = MarketCapRepository.get(this.application);
        repository.queryTrends();
        trendLiveData = repository.getTrends();
        walletLiveData = new WalletLiveData(localWallet);
        walletLiveData.refreshAll();
    }

    @Override
    public void update(WalletNotificationType type, LocalWallet.EventMessage<?> content) {
        switch (type){
            case TX_ACCEPTED:
                walletLiveData.refreshAvailableBalance();
                walletLiveData.refreshTxHistory((Transaction) content.getContent());
                walletLiveData.refreshAvailableBalance();
                break;
            case TX_RECEIVED:
                walletLiveData.refreshCurrentReceivingAddress();
                walletLiveData.refreshExpectedBalance();
                walletLiveData.refreshTxHistory((Transaction) content.getContent());
                break;
        }
    }
}
