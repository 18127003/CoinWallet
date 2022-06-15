package me.app.coinwallet.data.marketcap;

import android.util.Log;
import com.squareup.moshi.*;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MarketCapHost {
    private final static String HOST_PREFIX = "https://cryptowallet-backend.herokuapp.com/api/";
    private final static HttpUrl MARKET_CAP_URL = HttpUrl.parse(HOST_PREFIX + "marketCap/all");
    private final static HttpUrl TREND_ULR=HttpUrl.parse(HOST_PREFIX+"marketCap/trend");
    private final static HttpUrl EXCHANGE_RATE_URL = HttpUrl.parse(HOST_PREFIX + "exchange_rates");

    private static final MediaType MEDIA_TYPE = MediaType.get("application/json");
    private static final String SOURCE = "CoinGecko.com";

    private static final Logger log = LoggerFactory.getLogger(MarketCapHost.class);

    private final Moshi moshi;

    public MarketCapHost(final Moshi moshi){
        this.moshi = moshi;
    }

    public MediaType mediaType() {
        return MEDIA_TYPE;
    }

    public HttpUrl url() {
        HttpUrl.Builder builder = MARKET_CAP_URL.newBuilder();
        return builder.build();
    }

    public HttpUrl trendingUrl() {
        HttpUrl.Builder builder = TREND_ULR.newBuilder();
        return builder.build();
    }


    public List<MarketCapEntry> parse(final BufferedSource jsonSource) throws IOException, JsonDataException {
        Type responseType = Types.newParameterizedType(List.class, MarketCapJson.class);
        final JsonAdapter<List<MarketCapJson>> jsonAdapter = moshi.adapter(responseType);
        final List<MarketCapJson> jsonResponse = jsonAdapter.fromJson(jsonSource);
        final List<MarketCapEntry> result = new ArrayList<>(jsonResponse.size());
        for (MarketCapJson json : jsonResponse) {
            result.add(new MarketCapEntry(SOURCE, json));
        }
        return result;
    }
}
