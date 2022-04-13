package me.app.coinwallet.marketcap;

import android.util.Log;
import com.squareup.moshi.*;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okio.BufferedSource;
import org.bitcoinj.utils.Fiat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MarketCapHost {
    private final static String HOST_PREFIX = "https://api.coingecko.com/api/v3/";
    private final static HttpUrl MARKET_CAP_URL = HttpUrl.parse(HOST_PREFIX + "coins/markets");
    private final static HttpUrl EXCHANGE_RATE_URL = HttpUrl.parse(HOST_PREFIX + "exchange_rates");
    private final static String CURRENCY_QUERY_PARAM = "vs_currency";
    private final static String ORDER_QUERY_PARAM = "order";
    private final static String PAGE_CAP_QUERY_PARAM = "per_page";
    private final static String PAGE_NUM_QUERY_PARAM = "page";
    private final static String SPARKLINE_QUERY_PARAM = "sparkline";
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

    public HttpUrl url(final String currency) {
        HttpUrl.Builder builder = MARKET_CAP_URL.newBuilder();
        builder.addQueryParameter(CURRENCY_QUERY_PARAM, currency);
        builder.addQueryParameter(ORDER_QUERY_PARAM, "market_cap_desc");
        builder.addQueryParameter(PAGE_CAP_QUERY_PARAM, "100");
        builder.addQueryParameter(PAGE_NUM_QUERY_PARAM, "1");
        builder.addQueryParameter(SPARKLINE_QUERY_PARAM, "false");
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
