package me.app.coinwallet.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.data.livedata.WalletLiveData;
import me.app.coinwallet.data.marketcap.MarketCapEntity;
import me.app.coinwallet.data.marketcap.MarketCapRepository;
import me.app.coinwallet.data.transaction.MonthlyReport;
import me.app.coinwallet.data.transaction.TransactionWrapper;
import org.bitcoinj.core.TransactionConfidence;

import java.util.List;

public class HomePageViewModel extends AndroidViewModel {
    private final WalletLiveData walletLiveData;
    private final LiveData<List<MarketCapEntity>> trendLiveData;

    public LiveData<String> getBalance(){ return walletLiveData.getAvailableBalance(); }

    public LiveData<List<MonthlyReport>> getMonthlyReports() {
        return walletLiveData.getMonthlyReports();
    }

    public LiveData<String> getAddress(){return walletLiveData.getCurrentReceivingAddress();}

    public LiveData<List<MarketCapEntity>> getTrendLiveData() {
        return trendLiveData;
    }

    public LiveData<TransactionWrapper> getLatestTx(){
        return walletLiveData.getLatestTx();
    }

    public void filter(Filter filter){
        switch (filter){
            case PENDING:
                walletLiveData.filterTx(TransactionConfidence.ConfidenceType.PENDING);
                break;
            case SUCCESS:
                walletLiveData.filterTx(TransactionConfidence.ConfidenceType.BUILDING);
                break;
            case FAIL:
                walletLiveData.filterTx(TransactionConfidence.ConfidenceType.DEAD);
                break;
            case ALL:
                walletLiveData.filterTx(null);
                break;
        }
    }

    public HomePageViewModel(Application application){
        super(application);
        MarketCapRepository repository = MarketCapRepository.get(application);
        repository.queryTrends();
        trendLiveData = repository.getTrends();
        walletLiveData = WalletLiveData.get();
    }

    public enum Filter{
        SUCCESS,FAIL,PENDING,ALL
    }
}
