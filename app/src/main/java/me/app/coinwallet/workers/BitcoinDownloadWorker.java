package me.app.coinwallet.workers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import me.app.coinwallet.LocalWallet;
import me.app.coinwallet.NotificationHandler;
import me.app.coinwallet.WalletNotificationType;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.Threading;

public class BitcoinDownloadWorker extends Worker implements LocalWallet.EventListener {

    private final LocalWallet localWallet;
    public BitcoinDownloadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters parameters) {
        super(context, parameters);
        localWallet = LocalWallet.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        BriefLogFormatter.init();
        Handler handler = new Handler(Looper.getMainLooper());
        Threading.USER_THREAD = handler::post;
        download();
        return Result.success();
    }

    private void download() {
        localWallet.subscribe(this);
        localWallet.initWallet();
    }

    @Override
    public void update(WalletNotificationType type, LocalWallet.EventMessage<?> content) {
        switch (type){
            case TX_RECEIVED:
//                NotificationHandler.sendNotification(getApplicationContext(), "Transaction received","Body");
                break;
        }
    }
}
