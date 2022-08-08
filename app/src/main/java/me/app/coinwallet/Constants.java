package me.app.coinwallet;

import android.text.format.DateUtils;
import com.google.common.io.BaseEncoding;
import me.app.coinwallet.bitcoinj.Bip44KeyChainGroupStructure;
import me.app.coinwallet.utils.SmallestCoinSelector;
import okhttp3.OkHttpClient;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.KeyChainGroupStructure;

import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Constants {
    public static final String NOTIFICATION_CHANNEL_ID_RECEIVE = "NOTIFICATION_CHANNEL_ID_RECEIVE";

    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "NOTIFICATION_CHANNEL_ID_SERVICE";

    public static final int NOTIFICATION_SYNC_ID = 1;

    public static final int NOTIFICATION_TX_RECEIVE_ID = 2;

    public static final String QR_CONTENT = "qr_content";

    public static final String INIT_FRAGMENT_EXTRA_NAME = "init_fragment";

    public static final String APP_BAR_TITLE_EXTRA_NAME = "app_bar_title";

    public static final String SEND_METHOD_EXTRA_NAME = "selected_send_method";

    public static final String WALLET_LABEL_EXTRA_NAME = "wallet_label";

    public static final String MNEMONIC_EXTRA_NAME = "mnemonic";

    public static final int QR_BITMAP_SCALE_HEIGHT = 500;

    public static final int QR_BITMAP_SCALE_WIDTH = 500;

    public static final CoinSelector DEFAULT_COIN_SELECTOR = SmallestCoinSelector.get();

    public static final Bip44KeyChainGroupStructure WALLET_STRUCTURE = Bip44KeyChainGroupStructure.get();

    public static final Script.ScriptType DEFAULT_OUTPUT_SCRIPT_TYPE = Script.ScriptType.P2WPKH;

    public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();

    public static final UUID BLUETOOTH_PAYMENT_PROTOCOL_UUID = UUID.fromString("3357A7BB-762D-464A-8D9A-DCA592D57D5B");

    public static final String BLUETOOTH_PAYMENT_PROTOCOL_NAME = "Bitcoin classic payment protocol BIP21";

    public static final String BT_ENABLED_PARAM = "bt";

    public static final Map<String, String> SUPPORTED_BLOCKCHAIN = Stream.of(
            new AbstractMap.SimpleEntry<>("Bitcoin TestNet","org.bitcoin.test"),
            new AbstractMap.SimpleEntry<>("Bitcoin MainNet","org.bitcoin.production")
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    /** Number of confirmations until a transaction is fully confirmed. */
    public static final int MAX_NUM_CONFIRMATIONS = 7;

    public static final OkHttpClient HTTP_CLIENT;
    static {

        final OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.followRedirects(false);
        httpClientBuilder.followSslRedirects(true);
        httpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(15, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(15, TimeUnit.SECONDS);
//        httpClientBuilder.addInterceptor(loggingInterceptor);
        HTTP_CLIENT = httpClientBuilder.build();
    }
}
