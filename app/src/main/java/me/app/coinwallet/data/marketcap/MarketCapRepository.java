package me.app.coinwallet.data.marketcap;

import android.text.format.DateUtils;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.InvalidationTracker;
import com.google.common.base.Stopwatch;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import me.app.coinwallet.Constants;
import me.app.coinwallet.WalletApplication;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MarketCapRepository {
    private static MarketCapRepository INSTANCE;

    private static final long UPDATE_FREQ_MS = 10 * DateUtils.MINUTE_IN_MILLIS;
    private static final Logger log = LoggerFactory.getLogger(MarketCapRepository.class);
    private final MarketCapDatabase db;

//    private final MarketChartRepository chartRepository;
    private final MarketCapDao dao;
    private final MutableLiveData<List<MarketCapEntry>> trends = new MutableLiveData<>();

    private final AtomicLong lastUpdated = new AtomicLong(0);
    private final AtomicLong lastUpdatedTrend = new AtomicLong(0);
    final MarketCapHost marketCapHost = new MarketCapHost(new Moshi.Builder().build());

    public synchronized static MarketCapRepository get(final WalletApplication application) {
        if (INSTANCE == null)
            INSTANCE = new MarketCapRepository(application);
        return INSTANCE;
    }

    public MarketCapRepository(final WalletApplication application) {

        this.db = MarketCapDatabase.getDatabase(application);
        this.dao = db.marketCapDao();
    }

    public MarketCapDao marketCapDao() {
        maybeRequestMarketCaps(marketCapHost.url());
        return dao;
    }
    public void queryTrends() {
        maybeRequestTrend(marketCapHost.trendingUrl());
    }

    public InvalidationTracker marketCapInvalidationTracker() {
        return db.getInvalidationTracker();
    }

    private void maybeRequestMarketCaps(HttpUrl url) {
        final Stopwatch watch = Stopwatch.createStarted();
        final long now = System.currentTimeMillis();

        if(!needRequest(lastUpdated, now)){
            return;
        }

        Log.e("HD","Market cap request");

        final Call call = httpClient().newCall(httpRequest(url));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, final Response response) {
                Log.e("HD","Market cap request success");
                try {
                    if (response.isSuccessful()) {
                        List<MarketCapEntry> data = marketCapHost.parse(response.body().source());
                        Log.e("HD",data.size()+"llll");
                        dao.insertOrUpdateAll(data);

                        MarketCapRepository.this.lastUpdated.set(now);
                        watch.stop();
                        log.info("fetched market caps from {}, took {}", marketCapHost.url(), watch);
                    } else {
                        log.warn("http status {} {} when fetching market caps from {}", response.code(),
                                response.message(), marketCapHost.url());
                    }
                } catch (final IOException x) {
                    Log.e("HD",x.getMessage());
                    log.warn("problem fetching market caps from " + marketCapHost.url(), x);
                } catch (final JsonDataException j) {
                    Log.e("HD",j.getMessage());
                }
            }

            @Override
            public void onFailure(final Call call, final IOException x) {
                Log.e("HD","Market cap request failed");
            }
        });
    }

    public LiveData<List<MarketCapEntry>> getTrends() {
        return trends;
    }

    private void maybeRequestTrend(HttpUrl url){
        final Stopwatch watch = Stopwatch.createStarted();
        final long now = System.currentTimeMillis();

        if(!needRequest(lastUpdatedTrend, now)){
            return;
        }
        Log.e("HD","Trend request");
        final Call call = httpClient().newCall(httpRequest(url));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, final Response response) {
                Log.e("HD","Trend request success");
                try {
                    if (response.isSuccessful()) {
                        List<MarketCapEntry> data = marketCapHost.parse(response.body().source());
                        trends.postValue(data);
                        MarketCapRepository.this.lastUpdatedTrend.set(now);
                        watch.stop();
                        log.info("fetched trend from {}, took {}", marketCapHost.trendingUrl(), watch);
                    } else {
                        log.warn("http status {} {} when fetching trends from {}", response.code(),
                                response.message(), marketCapHost.trendingUrl());
                    }
                } catch (final IOException x) {
                    Log.e("HD",x.getMessage());
                    log.warn("problem fetching trends from " + marketCapHost.trendingUrl(), x);
                } catch (final JsonDataException j) {
                    Log.e("HD",j.getMessage());
                }
            }

            @Override
            public void onFailure(final Call call, final IOException x) {
                Log.e("HD","Trend request failed");
            }
        });
    }

    private boolean needRequest(AtomicLong lastUpdate, long now){
        final long lastUpdated = lastUpdate.get();
        return lastUpdated == 0 || now - lastUpdated > UPDATE_FREQ_MS;
    }

    private Request httpRequest(HttpUrl url){
        final Request.Builder request = new Request.Builder();
        request.url(url);
        final Headers.Builder headers = new Headers.Builder();
        headers.add("Accept", marketCapHost.mediaType().toString());
        request.headers(headers.build());
        return request.build();
    }

    private OkHttpClient httpClient(){
        final OkHttpClient.Builder httpClientBuilder = Constants.HTTP_CLIENT.newBuilder();
        httpClientBuilder.connectionSpecs(Collections.singletonList(ConnectionSpec.RESTRICTED_TLS));
        return httpClientBuilder.build();
    }
}
