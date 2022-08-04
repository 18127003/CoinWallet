package me.app.coinwallet.data.exchangerates;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import me.app.coinwallet.data.marketcap.MarketCapHost;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okio.BufferedSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ExchangeRatesHost {
    private final static String HOST_PREFIX = "https://cryptowallet-backend.herokuapp.com/api/";
    private final static HttpUrl NAME_LIST_URL = HttpUrl.parse(HOST_PREFIX + "exchangeRates/name");
    private final static HttpUrl EXCHANGE_RATES_ULR=HttpUrl.parse(HOST_PREFIX+"exchangeRates");

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    private final Moshi moshi;

    public ExchangeRatesHost(final Moshi moshi){
        this.moshi = moshi;
    }

    public MediaType mediaType() {
        return MEDIA_TYPE;
    }

    public HttpUrl getNameListUrl() {
        HttpUrl.Builder builder = NAME_LIST_URL.newBuilder();
        return builder.build();
    }

    public HttpUrl getExchangeRatesUlr(String name) {
        HttpUrl.Builder builder = EXCHANGE_RATES_ULR.newBuilder();
        builder.addQueryParameter("name",name);
        return builder.build();
    }

    public List<String> parseNameList(final BufferedSource jsonSource) throws IOException, JsonDataException {
        Type responseType = Types.newParameterizedType(List.class, String.class);
        final JsonAdapter<List<String>> jsonAdapter = moshi.adapter(responseType);
        return jsonAdapter.fromJson(jsonSource);
    }

    public ExchangeRatesJson parseExchange(final BufferedSource jsonSource) throws IOException, JsonDataException {
        final JsonAdapter<ExchangeRatesJson> jsonAdapter = moshi.adapter(ExchangeRatesJson.class);
        return jsonAdapter.fromJson(jsonSource);
    }
}
