package me.app.coinwallet.data.exchangerates;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.common.base.Stopwatch;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import me.app.coinwallet.Constants;
import me.app.coinwallet.R;
import me.app.coinwallet.data.marketcap.MarketCapRepository;
import me.app.coinwallet.ui.listeners.RepositoryQueryListener;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ExchangeRatesRepository {
    private static ExchangeRatesRepository INSTANCE;
    private final Application application;
    private static final long UPDATE_NAME_FREQ_MS =  DateUtils.DAY_IN_MILLIS;
    private static final Logger log = LoggerFactory.getLogger(MarketCapRepository.class);
    private final AtomicLong lastUpdatedNameList = new AtomicLong(0);
    final ExchangeRatesHost exchangeRatesHost = new ExchangeRatesHost(new Moshi.Builder().build());
    private final MutableLiveData<ExchangeRatesJson> exchangeRatesJsonMutableLiveData= new MutableLiveData<>();

    public ExchangeRatesRepository(Application application) {
        this.application=application;
    }

    public synchronized static ExchangeRatesRepository get(final Application application) {
        if (INSTANCE == null)
            INSTANCE = new ExchangeRatesRepository(application);
        INSTANCE.queryNameList();
        return INSTANCE;
    }

    private Request httpRequest(HttpUrl url){
        final Request.Builder request = new Request.Builder();
        request.url(url);
        final Headers.Builder headers = new Headers.Builder();
        headers.add("Accept", exchangeRatesHost.mediaType().toString());
        request.headers(headers.build());
        return request.build();
    }

    private OkHttpClient httpClient(){
        final OkHttpClient.Builder httpClientBuilder = Constants.HTTP_CLIENT.newBuilder();
        httpClientBuilder.connectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS));
        return httpClientBuilder.build();
    }

    public void queryNameList(){
        maybeRequestNameList(exchangeRatesHost.getNameListUrl());

    }

    private boolean needRequest(AtomicLong lastUpdate, long now, long type){
        final long lastUpdated = lastUpdate.get();
        return lastUpdated == 0 || now - lastUpdated > type;
    }

    private void maybeRequestNameList(HttpUrl url) {
        final Stopwatch watch = Stopwatch.createStarted();
        final long now = System.currentTimeMillis();

        if(!needRequest(lastUpdatedNameList, now, UPDATE_NAME_FREQ_MS)){
            return;
        }

        Log.e("HD","Name list request");

        final Call call = httpClient().newCall(httpRequest(url));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, final Response response) {
                Log.e("HD","Name list request success");
                try {
                    if (response.isSuccessful()) {
                        List<String> data = exchangeRatesHost.parseNameList(response.body().source());

                        SharedPreferences preferences = application.getSharedPreferences(
                                application.getString(R.string.name_coin_list), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor= preferences.edit();
                        editor.putStringSet("name", new HashSet<>(data));
                        editor.apply();

                        ExchangeRatesRepository.this.lastUpdatedNameList.set(now);
                        watch.stop();
                        log.info("fetched name list from {}, took {}", url, watch);
                    } else {
                        log.warn("http status {} {} when fetching name list from {}", response.code(),
                                response.message(), url);
                    }
                } catch (final IOException x) {
                    Log.e("HD",x.getMessage());
                    log.warn("problem fetching name list from " + url, x);
                } catch (final JsonDataException j) {
                    Log.e("HD",j.getMessage());
                }
            }

            @Override
            public void onFailure(final Call call, final IOException x) {
                Log.e("HD","Name list request failed");
            }
        });
    }

    public void requestExchangeRate(String name, RepositoryQueryListener listener){
        HttpUrl url = exchangeRatesHost.getExchangeRatesUlr(name);
        maybeRequestExchangeRates(url, listener);
    }

    private void maybeRequestExchangeRates(HttpUrl url, RepositoryQueryListener listener) {

        Log.e("HD","Exchange Rates request");

        final Call call = httpClient().newCall(httpRequest(url));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, final Response response) {
                Log.e("HD","Exchange Rates request success");
                try {
                    if (response.isSuccessful()) {

                        ExchangeRatesJson data = exchangeRatesHost.parseExchange(response.body().source());
                        listener.onSucceed();
                        exchangeRatesJsonMutableLiveData.postValue(data);
                    } else {
                        log.warn("http status {} {} when fetching exchange rates from {}", response.code(),
                                response.message(), url);
                    }
                } catch (final IOException x) {
                    Log.e("HD",x.getMessage());
                    log.warn("problem fetching exchange rates from " + url, x);
                    listener.onFailed();
                } catch (final JsonDataException j) {
                    Log.e("HD",j.getMessage());
                    listener.onFailed();
                }
            }

            @Override
            public void onFailure(final Call call, final IOException x) {
                Log.e("HD","Exchange Rates request failed");
                listener.onFailed();
            }
        });
    }
    public LiveData<ExchangeRatesJson> getCurrent(){
        return exchangeRatesJsonMutableLiveData;
    }
}
