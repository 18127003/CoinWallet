package me.app.coinwallet.data.marketcap;

import android.text.format.DateUtils;
import android.util.Log;
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
    private final MarketChartRepository chartRepository;
    private final MarketCapDao dao;
    private final AtomicLong lastUpdated = new AtomicLong(0);

    public synchronized static MarketCapRepository get(final WalletApplication application) {
        if (INSTANCE == null)
            INSTANCE = new MarketCapRepository(application);
        return INSTANCE;
    }

    public MarketCapRepository(final WalletApplication application) {

//        this.config = application.getConfiguration();
//        this.userAgent = WalletApplication.httpUserAgent(application.packageInfo().versionName);

        this.db = MarketCapDatabase.getDatabase(application);
        chartRepository = MarketChartRepository.get(application);
        this.dao = db.marketCapDao();
    }

    public MarketCapDao marketCapDao() {
        maybeRequestMarketCaps();
        return dao;
    }

    public InvalidationTracker marketCapInvalidationTracker() {
        return db.getInvalidationTracker();
    }

    private void maybeRequestMarketCaps() {
//        if (!application.getConfiguration().isEnableExchangeRates())
//            return;

        final Stopwatch watch = Stopwatch.createStarted();
        final long now = System.currentTimeMillis();

        final long lastUpdated = this.lastUpdated.get();
        if (lastUpdated != 0 && now - lastUpdated <= UPDATE_FREQ_MS)
            return;

        Log.e("HD","Market cap request");
        final MarketCapHost marketCapHost = new MarketCapHost(new Moshi.Builder().build());
        final Request.Builder request = new Request.Builder();
        request.url(marketCapHost.url());
        final Headers.Builder headers = new Headers.Builder();
//        headers.add("User-Agent", userAgent);
        headers.add("Accept", marketCapHost.mediaType().toString());
        request.headers(headers.build());

        Log.e("HD",request.build().toString());

        final OkHttpClient.Builder httpClientBuilder = Constants.HTTP_CLIENT.newBuilder();
        httpClientBuilder.connectionSpecs(Collections.singletonList(ConnectionSpec.RESTRICTED_TLS));
        final Call call = httpClientBuilder.build().newCall(request.build());
        call.enqueue(new Callback() {
            @Override
            public void onResponse(final Call call, final Response response) {
                Log.e("HD","Market cap request success");
                try {
                    if (response.isSuccessful()) {
                        List<MarketCapEntry> data = marketCapHost.parse(response.body().source());

                        for (final MarketCapEntry marketCapEntry : data)
                        {
                            dao.insertOrUpdate(marketCapEntry);
                        }

                        MarketCapRepository.this.lastUpdated.set(now);
                        watch.stop();
                        log.info("fetched exchange rates from {}, took {}", marketCapHost.url(), watch);
                    } else {
                        log.warn("http status {} {} when fetching exchange rates from {}", response.code(),
                                response.message(), marketCapHost.url());
                    }
                } catch (final IOException x) {
                    Log.e("HD",x.getMessage());
                    log.warn("problem fetching exchange rates from " + marketCapHost.url(), x);
                } catch (final JsonDataException j) {
                    Log.e("HD",j.getMessage());
                }
            }

            @Override
            public void onFailure(final Call call, final IOException x) {
//                log.warn("problem fetching exchange rates from " + marketCapHost.url(), x);
                Log.e("HD","Market cap request failed");
            }
        });
    }

//    public void maybeRequestTrend() {
////        if (!application.getConfiguration().isEnableExchangeRates())
////            return;
//
//        final Stopwatch watch = Stopwatch.createStarted();
//        final long now = System.currentTimeMillis();
//
//        final long lastUpdated = this.lastUpdated.get();
//        if (lastUpdated != 0 && now - lastUpdated <= UPDATE_FREQ_MS)
//            return;
//
//        Log.e("HD","Market cap request");
//        final MarketCapHost marketCapHost = new MarketCapHost(new Moshi.Builder().add(new TrendConverter()).build());
//        final Request.Builder request = new Request.Builder();
//        request.url(marketCapHost.trendingUrl());
//        final Headers.Builder headers = new Headers.Builder();
////        headers.add("User-Agent", userAgent);
//        headers.add("Accept", marketCapHost.mediaType().toString());
//        request.headers(headers.build());
//
//        Log.e("HD",request.build().toString());
//
//        final OkHttpClient.Builder httpClientBuilder = Constants.HTTP_CLIENT.newBuilder();
//        httpClientBuilder.connectionSpecs(Collections.singletonList(ConnectionSpec.RESTRICTED_TLS));
//        final Call call = httpClientBuilder.build().newCall(request.build());
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(final Call call, final Response response) {
//                Log.e("HD","Market cap request success");
//                try {
//                    if (response.isSuccessful()) {
//                        Log.e("HD","body "+response.body().string());
//                        List<String> data = marketCapHost.parseTrend(response.body().source());
//                        //TEST
//                        Log.e("HD",""+data.size());
//                        data.forEach(id->{
//                            Log.e("HD",id);
//                            chartRepository.maybeRequestChart(id);
//                        });
//
//                        MarketCapRepository.this.lastUpdated.set(now);
//                        watch.stop();
//                    } else {
//                        log.warn("http status {} {} when fetching exchange rates from {}", response.code(),
//                                response.message(), marketCapHost.trendingUrl());
//                    }
//                } catch (final IOException x) {
//                    Log.e("HD",x.getMessage());
//                    log.warn("problem fetching exchange rates from " + marketCapHost.trendingUrl(), x);
//                } catch (final JsonDataException j) {
//                    Log.e("HD",j.getMessage());
//                }
//            }
//
//            @Override
//            public void onFailure(final Call call, final IOException x) {
////                log.warn("problem fetching exchange rates from " + marketCapHost.url(), x);
//                Log.e("HD","Market cap request failed");
//            }
//        });
//    }
}
