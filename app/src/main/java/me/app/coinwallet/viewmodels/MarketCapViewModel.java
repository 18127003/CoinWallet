package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.data.marketcap.MarketCapDao;
import me.app.coinwallet.data.marketcap.MarketCapEntity;
import me.app.coinwallet.data.marketcap.MarketCapRepository;

import java.util.List;
import java.util.Locale;

public class MarketCapViewModel extends AndroidViewModel {
    private final WalletApplication application;
    private final MarketCapDao marketCapDao;
    private final MediatorLiveData<List<MarketCapEntity>> marketCapLiveData = new MediatorLiveData<>();
    private LiveData<List<MarketCapEntity>> underlyingMarketCapLiveData;
    private boolean isFiltered = false;

    public MarketCapViewModel(final Application application){
        super(application);
        this.application = (WalletApplication) application;
        marketCapDao = MarketCapRepository.get(this.application).marketCapDao();
        fetch(null);
    }

    public LiveData<List<MarketCapEntity>> marketCapData() {
        return marketCapLiveData;
    }

    public void fetch(final String filter) {
        if (underlyingMarketCapLiveData != null)
            marketCapLiveData.removeSource(underlyingMarketCapLiveData);
        if (filter != null) {
            underlyingMarketCapLiveData = marketCapDao.findByFilter(filter.toLowerCase(Locale.US));
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
