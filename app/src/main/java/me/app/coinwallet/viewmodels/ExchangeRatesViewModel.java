package me.app.coinwallet.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import me.app.coinwallet.R;
import me.app.coinwallet.data.exchangerates.ExchangeRatesJson;
import me.app.coinwallet.data.exchangerates.ExchangeRatesRepository;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExchangeRatesViewModel extends AndroidViewModel {
    public ExchangeRatesViewModel(@NonNull Application application) {
        super(application);
        exchangeRatesRepository=ExchangeRatesRepository.get(application);
        queryNameList();
    }
    private ExchangeRatesRepository exchangeRatesRepository;
    private LiveData<ExchangeRatesJson> exchangeRatesJsonLiveData;
    private MutableLiveData<List<String>> nameList= new MutableLiveData<>(Collections.EMPTY_LIST);
    public LiveData<ExchangeRatesJson> getExchangeRate(){
        return exchangeRatesRepository.getCurrent();
    }
    public void queryExchangeRates(String name){
        exchangeRatesRepository.requestExchangeRate(name);
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


}
