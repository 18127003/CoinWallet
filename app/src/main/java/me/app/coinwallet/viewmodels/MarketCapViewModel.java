package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.marketcap.MarketCapDao;
import me.app.coinwallet.marketcap.MarketCapEntry;
import me.app.coinwallet.marketcap.MarketCapRepository;

import java.util.List;
import java.util.Locale;

public class MarketCapViewModel extends AndroidViewModel {
    private final WalletApplication application;
    private final MarketCapDao marketCapDao;
    private final MediatorLiveData<List<MarketCapEntry>> marketCapLiveData = new MediatorLiveData<>();
    private LiveData<List<MarketCapEntry>> underlyingMarketCapLiveData;
    private boolean isFiltered = false;

    public MarketCapViewModel(final Application application){
        super(application);
        this.application = (WalletApplication) application;
        marketCapDao = MarketCapRepository.get(this.application).marketCapDao();
        fetch(null);
    }

    public LiveData<List<MarketCapEntry>> marketCapData() {
        return marketCapLiveData;
    }

    public void fetch(final String filter) {
        if (underlyingMarketCapLiveData != null)
            marketCapLiveData.removeSource(underlyingMarketCapLiveData);
        if (filter != null) {
            underlyingMarketCapLiveData = marketCapDao.findByConstraint(filter.toLowerCase(Locale.US));
            isFiltered = true;
        } else {
            Log.e("HD","Market cap find all");
            underlyingMarketCapLiveData = marketCapDao.findAll();
            isFiltered = false;
        }
        marketCapLiveData.addSource(underlyingMarketCapLiveData, marketCapLiveData::setValue);
    }

    public boolean isFiltered() {
        return isFiltered;
    }
}
