package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.Configuration;
import me.app.coinwallet.R;
import me.app.coinwallet.WalletApplication;
import me.app.coinwallet.data.exchangerates.ExchangeRatesJson;
import me.app.coinwallet.data.exchangerates.ExchangeRatesRepository;
import me.app.coinwallet.ui.activities.BaseActivity;
import me.app.coinwallet.ui.listeners.RepositoryQueryListener;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExchangeRatesViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isQuerying = new MutableLiveData<>(Boolean.FALSE);
    private final Configuration configuration;
    private final RepositoryQueryListener queryListener = new RepositoryQueryListener() {
        @Override
        public void onSucceed() {
            isQuerying.postValue(Boolean.FALSE);
        }

        @Override
        public void onFailed() {
            isQuerying.postValue(Boolean.FALSE);
            configuration.toastUtil.postToast("Get trend failed. Connect and try again.", Toast.LENGTH_SHORT);
        }
    };

    public ExchangeRatesViewModel(@NonNull Application application) {
        super(application);
        configuration = ((WalletApplication) application).getConfiguration();
        exchangeRatesRepository=ExchangeRatesRepository.get(application);
        queryNameList();
    }
    private final ExchangeRatesRepository exchangeRatesRepository;
    private final MutableLiveData<List<String>> nameList= new MutableLiveData<>(Collections.emptyList());
    public LiveData<ExchangeRatesJson> getExchangeRate(){
        return exchangeRatesRepository.getCurrent();
    }
    public void queryExchangeRates(String name){
        isQuerying.postValue(Boolean.TRUE);
        exchangeRatesRepository.requestExchangeRate(name, queryListener);
    }

    public LiveData<List<String>> getNameList(){
        return nameList;
    }
    public  void queryNameList(){
        SharedPreferences preferences = getApplication().getSharedPreferences(
                getApplication().getString(R.string.name_coin_list), Context.MODE_PRIVATE);
        nameList.postValue(new ArrayList<>(preferences.getStringSet("name", Collections.emptySet())));
    }
    public BigDecimal calculate(Float num, Float currency){
        return BigDecimal.valueOf(num).multiply(BigDecimal.valueOf(currency)).setScale(4,BigDecimal.ROUND_CEILING);
    }

    public LiveData<Boolean> isQuerying(){return isQuerying;}
}
