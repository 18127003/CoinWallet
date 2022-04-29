package me.app.coinwallet.workers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.ForegroundInfo;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import me.app.coinwallet.LocalWallet;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.Threading;

import java.io.File;
import java.util.Date;

public class BitcoinDownloadWorker extends Worker {

    public BitcoinDownloadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters parameters) {
        super(context, parameters);
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
        LocalWallet wallet = LocalWallet.getInstance();
        wallet.initWallet();
    }
}
