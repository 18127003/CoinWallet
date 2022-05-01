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

    private final WalletApplication application;
//    private final Configuration config;
//    private final String userAgent;
    private final MarketCapDatabase db;
    private final MarketCapDao dao;
    private final AtomicLong lastUpdated = new AtomicLong(0);

    public synchronized static MarketCapRepository get(final WalletApplication application) {
        if (INSTANCE == null)
            INSTANCE = new MarketCapRepository(application);
        return INSTANCE;
    }

    public MarketCapRepository(final WalletApplication application) {
        this.application = application;
//        this.config = application.getConfiguration();
//        this.userAgent = WalletApplication.httpUserAgent(application.packageInfo().versionName);

        this.db = MarketCapDatabase.getDatabase(application);
        this.dao = db.marketCapDao();
    }

    public MarketCapDao marketCapDao() {
        maybeRequestExchangeRates();
        return dao;
    }

    public InvalidationTracker marketCapInvalidationTracker() {
        return db.getInvalidationTracker();
    }

    private void maybeRequestExchangeRates() {
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
        request.url(marketCapHost.url("usd"));
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
                        log.info("fetched exchange rates from {}, took {}", marketCapHost.url("usd"), watch);
                    } else {
                        log.warn("http status {} {} when fetching exchange rates from {}", response.code(),
                                response.message(), marketCapHost.url("usd"));
                    }
                } catch (final IOException x) {
                    Log.e("HD",x.getMessage());
                    log.warn("problem fetching exchange rates from " + marketCapHost.url("usd"), x);
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
}
