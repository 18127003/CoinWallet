package me.app.coinwallet.blockchain;

import android.app.Notification;
import android.content.*;
import android.os.*;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleService;
import com.google.common.base.Stopwatch;
import me.app.coinwallet.*;
import me.app.coinwallet.data.livedata.BlockchainLiveData;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.Threading;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkState;

public class BlockchainSyncService extends LifecycleService implements LocalWallet.EventListener {
    private PowerManager pm;

    private Configuration config;
    private LocalWallet wallet;
    private final BlockchainLiveData blockchainLiveData = new BlockchainLiveData();

    private Stopwatch serviceUpTime;
    private boolean resetBlockchainOnShutdown = false;
    private final AtomicBoolean isBound = new AtomicBoolean(false);

    private static final String ACTION_START_SYNC = BlockchainSyncService.class.getPackage().getName()
            + ".sync_blockchain";
    private static final String ACTION_RESET_BLOCKCHAIN = BlockchainSyncService.class.getPackage().getName()
            + ".reset_blockchain";

    public static void start(final Context context){
        Intent intent = new Intent(ACTION_START_SYNC, null, context, BlockchainSyncService.class);
        ContextCompat.startForegroundService(context, intent);
    }

    public static void resetBlockchain(final Context context) {
        // implicitly stops blockchain service
        ContextCompat.startForegroundService(context,
                new Intent(ACTION_RESET_BLOCKCHAIN, null, context, BlockchainSyncService.class));
    }

    @Override
    public void update(WalletNotificationType type, LocalWallet.EventMessage<?> content) {
        switch (type){
            case TX_RECEIVED:
                Transaction tx = (Transaction) content.getContent();
                Notification notification = NotificationHandler.buildNotification(getApplicationContext(),
                        "Transaction received", tx.getTxId().toString());
                config.notificationHandler.sendNotification(Constants.NOTIFICATION_TX_RECEIVE_ID, notification);
                break;
            case SYNC_STARTED:
                Log.e("HD","Sync started");
                updateForegroundNotification("Started downloading blockchain");
                blockchainLiveData.setSyncStatus(false);
                break;
            case SYNC_PROGRESS:
                Log.e("HD","Sync progress");
                double progress = (double) content.getContent();
                updateForegroundNotification(String.valueOf(progress));
                blockchainLiveData.updateProgress(progress);
                break;
            case SYNC_COMPLETED:
                Log.e("HD","Sync complete");
                updateForegroundNotification("Blockchain up to date");
                blockchainLiveData.setSyncStatus(true);
                break;
        }
    }

    private void updateForegroundNotification(String body){
        Notification notification = NotificationHandler.buildServiceNotification(getApplicationContext(),
                getResources().getString(R.string.service_notification_title), body);
        config.notificationHandler.sendNotification(Constants.NOTIFICATION_SYNC_ID, notification);
    }

    private final BroadcastReceiver deviceIdleModeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.i("HD","device "+ (pm.isDeviceIdleMode() ? "entering" : "exiting")+" idle mode");
        }
    };

    public class LocalBinder extends Binder {
        public BlockchainSyncService getService() {
            return BlockchainSyncService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(@NonNull final Intent intent) {
        Log.e("HD", "onBind: {}");
        super.onBind(intent);
        isBound.set(true);
        return mBinder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        Log.e("HD", "onUnbind: "+ intent);
        isBound.set(false);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        serviceUpTime = Stopwatch.createStarted();
        super.onCreate();

        config = ((WalletApplication) getApplication()).getConfiguration();
        wallet = LocalWallet.getInstance();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        registerReceiver(deviceIdleModeReceiver, new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED));
        startForeground(Constants.NOTIFICATION_SYNC_ID,
                NotificationHandler.buildServiceNotification(getApplication(),
                        getResources().getString(R.string.service_notification_title),"Coin wallet is syncing"));
    }


    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            final String action = intent.getAction();
            Log.i("HD","service start command: "+ action);

            if (ACTION_START_SYNC.equals(action)) {
                BriefLogFormatter.init();
//                Handler handler = new Handler(Looper.getMainLooper());
                Threading.USER_THREAD = config.executorService;
                wallet.subscribe(this);
                wallet.initWallet();
            } else if (ACTION_RESET_BLOCKCHAIN.equals(action)) {
                Log.d("HD","will remove blockchain on service shutdown");
                resetBlockchainOnShutdown = true;
                stopSelf();
                if (isBound.get())
                    Log.i("HD","stop is deferred because service still bound");
            }
        } else {
            Log.e("HD","service restart, although it was started as non-sticky");
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        wallet.stopWallet();
        if(resetBlockchainOnShutdown){
            // TODO: delete blockchain file
        }
        unregisterReceiver(deviceIdleModeReceiver);
        StartBlockchainSyncService.schedule(getApplication());
        super.onDestroy();
        Log.i("HD","service was up for "+ serviceUpTime.stop());
    }

    @Override
    public void onTrimMemory(final int level) {
        Log.i("HD","on trim memory level "+ level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            Log.e("HD","low memory detected, trying to stop");
            stopSelf();
            if (isBound.get())
                Log.e("HD","stop is deferred because service still bound");
        }
    }

}
