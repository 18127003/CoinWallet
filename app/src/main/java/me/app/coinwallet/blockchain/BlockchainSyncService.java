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
import me.app.coinwallet.bitcoinj.LocalWallet;
import me.app.coinwallet.bitcoinj.WalletNotificationType;
import me.app.coinwallet.data.livedata.BlockchainLiveData;
import me.app.coinwallet.utils.NotificationHandler;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.utils.Threading;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.base.Preconditions.checkState;

public class BlockchainSyncService extends LifecycleService implements LocalWallet.EventListener {
    private PowerManager pm;

    private Configuration config;
    private static final LocalWallet wallet = LocalWallet.getInstance();

    private Stopwatch serviceUpTime;
    private boolean shutdownOnSynced = false;
    private boolean resetBlockchainOnShutdown = false;
    private final AtomicBoolean isBound = new AtomicBoolean(false);
    private static final AtomicBoolean IS_RUNNING = new AtomicBoolean(false);
    public static final AtomicBoolean SHOULD_RESTART = new AtomicBoolean(true);

    public static final String ACTION_START_SYNC = BlockchainSyncService.class.getPackage().getName()
            + ".sync_blockchain";
    public static final String ACTION_RESET_BLOCKCHAIN = BlockchainSyncService.class.getPackage().getName()
            + ".reset_blockchain";
    public static final String ACTION_RESTORE_MNEMONIC = BlockchainSyncService.class.getPackage().getName()
            + ".restore_mnemonic";

    public static void start(final Context context){
        Intent intent = new Intent(ACTION_START_SYNC, null, context, BlockchainSyncService.class);
        ContextCompat.startForegroundService(context, intent);
    }

    public static void stop(){
        wallet.stopWallet();
    }

    public static void resetBlockchain(final Context context) {
        // implicitly stops blockchain service
        ContextCompat.startForegroundService(context,
                new Intent(ACTION_RESET_BLOCKCHAIN, null, context, BlockchainSyncService.class));
    }

    public static void restoreMnemonic(final Context context) {
        ContextCompat.startForegroundService(context,
                new Intent(ACTION_RESTORE_MNEMONIC, null, context, BlockchainSyncService.class));
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
                IS_RUNNING.set(true);
                updateForegroundNotification("Started downloading blockchain");
                break;
            case SYNC_PROGRESS:
                double progress = (double) content.getContent();
                updateForegroundNotification(String.valueOf(progress));
                break;
            case SYNC_COMPLETED:
                updateForegroundNotification("Blockchain up to date");
                if(shutdownOnSynced){
                    SHOULD_RESTART.set(false);
                    innerStop();
                }
                break;
            case SYNC_STOPPED:
                innerStop();
                break;
        }
    }

    private void innerStop(){
        IS_RUNNING.set(false);
        stopSelf();
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
        super.onBind(intent);
        isBound.set(true);
        return mBinder;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        isBound.set(false);
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        serviceUpTime = Stopwatch.createStarted();
        super.onCreate();
        config = ((WalletApplication) getApplication()).getConfiguration();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        registerReceiver(deviceIdleModeReceiver, new IntentFilter(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED));
        startForeground(Constants.NOTIFICATION_SYNC_ID,
                NotificationHandler.buildServiceNotification(getApplication(),
                        getResources().getString(R.string.service_notification_title),"Coin wallet is syncing"));
    }


    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        if(IS_RUNNING.get()){
            wallet.stopWallet();
            return START_NOT_STICKY;
        }
        if (intent != null) {
            final String action = intent.getAction();

            if (ACTION_START_SYNC.equals(action)) {
                shutdownOnSynced = false;
                BriefLogFormatter.init();
                Threading.USER_THREAD = config.executorService;
                wallet.subscribe(this);
                wallet.configWalletAppKit();
                wallet.initWallet();
            } else if (ACTION_RESET_BLOCKCHAIN.equals(action)) {
                resetBlockchainOnShutdown = true;
                stopSelf();
            } else if (ACTION_RESTORE_MNEMONIC.equals(action)) {
                BriefLogFormatter.init();
                Threading.USER_THREAD = config.executorService;
                wallet.subscribe(this);
                shutdownOnSynced = true;
                wallet.configWalletAppKit();
                wallet.initWallet();
            }
        }
        IS_RUNNING.set(true);
        SHOULD_RESTART.set(true);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        if(wallet.isOnline()){
            wallet.stopWallet();
        }
        if(resetBlockchainOnShutdown){
            File chainFile = new File(config.directory, wallet.getLabel()+".spvchain");
            try {
                chainFile.delete();
            } catch (SecurityException e){
                Log.e("HD", "No access to file "+chainFile.getName());
            }
        }
        unregisterReceiver(deviceIdleModeReceiver);

        super.onDestroy();
        if(SHOULD_RESTART.get()){
            StartBlockchainSyncService.schedule(getApplication(), ACTION_START_SYNC);
        }
    }

    @Override
    public void onTrimMemory(final int level) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            stopSelf();
        }
    }

}
