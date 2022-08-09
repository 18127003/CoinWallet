package me.app.coinwallet.bitcoinj;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.Service;
import me.app.coinwallet.Constants;
import me.app.coinwallet.utils.WalletUtil;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.crypto.KeyCrypter;
import org.bitcoinj.crypto.KeyCrypterException;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.uri.BitcoinURI;
import org.bitcoinj.wallet.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class LocalWallet {
    private static LocalWallet _instance = null;
    private static final String WALLET_FILE = "coinwallet";

    private final List<EventListener> observers = new ArrayList<>();

    public void subscribe(EventListener listener){
        observers.add(listener);
    }

    private void  notifyObservers(WalletNotificationType type, EventMessage<?> content){
        for(EventListener l : observers){
            l.update(type, content);
        }
    }

    public static synchronized LocalWallet getInstance(){
        if (_instance == null){
            _instance = new LocalWallet();
        }
        return _instance;
    }

    private WalletInfo walletInfo;
    private WalletAppKit walletAppKit;
    private Wallet wallet;
    private File directory;
    private Context context;

    public Address getAddress(){
        try{
            return wallet.currentReceiveAddress();
        } catch (DeterministicUpgradeRequiredException e){
            //swallow
        }
        return null;
    }

    public HDPath getCurrentAccountPath(){
        if(walletInfo.accountIndex!=Integer.MAX_VALUE){
            return Constants.WALLET_STRUCTURE.accountPathFor(walletInfo.parameters, walletInfo.accountIndex);
        }
        return null;
    }

    public NetworkParameters parameters(){
        return walletInfo.parameters;
    }

    public Context getContext() {
        return context;
    }

    public void addAccount(int index, String password){
        if(index == 0){
            return;
        }
        DeterministicSeed seed = seed(password);
        HDPath accountPath = Constants.WALLET_STRUCTURE.accountPathFor(walletInfo.parameters, index);
        DeterministicKeyChain chain = DeterministicKeyChain.builder()
                .seed(seed)
                .outputScriptType(Script.ScriptType.P2PKH)
                .accountPath(accountPath)
                .build();
        KeyCrypter keyCrypter = wallet.getKeyCrypter();
        wallet.addAndActivateHDChain(chain.toEncrypted(keyCrypter, keyCrypter.deriveKey(password)));
        notifyObservers(WalletNotificationType.ACCOUNT_ADDED, null);
    }

    public List<Integer> allAccounts(){
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".wallet") && !name.startsWith("Backup"));
        if(files == null){
            return Collections.emptyList();
        }
        List<Integer> accounts = new ArrayList<>();
        for(File walletFile: files){
            accounts.addAll(WalletUtil.accounts(walletFile));
        }
        return accounts;
    }

    public DeterministicSeed seed(String password){
        DeterministicSeed seed = wallet.getKeyChainSeed();
        KeyCrypter keyCrypter = wallet.getKeyCrypter();
        return seed.decrypt(keyCrypter, "", keyCrypter.deriveKey(password));
    }

    public boolean isEncrypted(){ return wallet.isEncrypted(); }

    public String getPlainBalance(){
        return wallet.getBalance().toFriendlyString();
    }

    public String getExpectedBalance(){
        return wallet.getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString();
    }

    public String getLabel() {
        return walletInfo.label;
    }

    public Wallet wallet(){
        return wallet;
    }

    public List<Transaction> history(){
        return wallet.getTransactions(true)
                .stream().filter(tx->WalletUtil.isTxRelated(tx, wallet))
                .sorted(Transaction.SORT_TX_BY_UPDATE_TIME)
                .collect(Collectors.toList());
    }

    private void addListeners(){
        onReceive();
    }

    public void check(){
        final List<TransactionOutput> to = wallet.getUnspents();
        Log.e("HD","UTXO num: "+to.size());
        for(TransactionOutput txo : to){
            Log.e("HD","Transaction value "+txo.getValue().toPlainString()+", min non dust "+txo.getMinNonDustValue());
        }
    }

    public boolean checkPassword(String password) throws IllegalStateException{
        return wallet.checkPassword(password);
    }

    public void changePassword(String currentPassword, String newPassword) throws Wallet.BadWalletEncryptionKeyException {
        wallet.changeEncryptionPassword(currentPassword, newPassword);
    }

    public String generatePaymentRequest(double amount, String label, String message){
        Address address = getAddress();
        Coin amountToSend = Coin.ofBtc(BigDecimal.valueOf(amount));
        return BitcoinURI.convertToBitcoinURI(address, amountToSend, label, message);
    }

    public Transaction send(SendRequest sendRequest, String password) throws InsufficientMoneyException, Wallet.DustySendRequested,
            Wallet.ExceededMaxTransactionSize, Wallet.CouldNotAdjustDownwards, Wallet.BadWalletEncryptionKeyException {
        sendRequest.feePerKb = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
        sendRequest.coinSelector = Constants.DEFAULT_COIN_SELECTOR;
        if (password != null){
            sendRequest.aesKey = Objects.requireNonNull(wallet.getKeyCrypter()).deriveKey(password);
        }
        final Wallet.SendResult sendResult = wallet.sendCoins(walletAppKit.peerGroup(), sendRequest);
        Log.e("HD","Sending");
        sendResult.broadcastComplete.addListener(() -> {
            Log.e("HD", "Tx broadcast completed");
            notifyObservers(WalletNotificationType.TX_BROADCAST_COMPLETED, null);
        }, Runnable::run);
        return sendResult.tx;
    }

    public Transaction sendOffline(SendRequest sendRequest, String password) throws InsufficientMoneyException, Wallet.DustySendRequested,
            Wallet.ExceededMaxTransactionSize, Wallet.CouldNotAdjustDownwards, Wallet.BadWalletEncryptionKeyException {
        sendRequest.feePerKb = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
        if (password != null){
            sendRequest.aesKey = Objects.requireNonNull(wallet.getKeyCrypter()).deriveKey(password);
        }
        return wallet.sendCoinsOffline(sendRequest);
    }

    private void onReceive(){
        wallet.addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> {
            Coin value = tx.getValueSentToMe(wallet);
            Log.e("HD","Received tx for " + value.toFriendlyString() + ": " + tx);
            notifyObservers(WalletNotificationType.TX_RECEIVED, new EventMessage<>(tx));
            Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
                @Override
                public void onSuccess(TransactionConfidence result) {
                    Log.e("HD","Receipt of "+result.getTransactionHash().toString()+" reached 1 confirmation");
                    notifyObservers(WalletNotificationType.TX_ACCEPTED, new EventMessage<>(tx));
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("HD","Dead receipt");
                }
            }, Runnable::run);
        });
    }

    public boolean hasChainFileDownloaded(String prefix){
        File chainFile = new File(directory, prefix + ".spvchain");
        return chainFile.exists();
    }

    public void registerDirectory(File directory){
        this.directory = directory;
    }

    public void registerAccount(WalletInfo walletInfo){
        this.walletInfo = walletInfo;
    }

    public void configWalletAppKit(){
        if(this.walletInfo == null){
            throw new IllegalArgumentException();
        }
        context = new Context(walletInfo.parameters);
        walletAppKit = new WalletAppKit(walletInfo.parameters, Script.ScriptType.P2PKH, Constants.WALLET_STRUCTURE,
                directory, WALLET_FILE, walletInfo.accountIndex) {
            @Override
            protected void onSetupCompleted() {
                Log.e("HD", "Set up complete");
                wallet = LocalWallet.this.walletAppKit.wallet();
                notifyObservers(WalletNotificationType.SETUP_COMPLETED, null);
            }

        };
        DownloadProgressTracker BTCListener = new DownloadProgressTracker() {
            @Override
            protected void startDownload(int blocks) {
                Log.e("HD","Start download blocks");
                notifyObservers(WalletNotificationType.SYNC_STARTED, null);
            }

            @Override
            public void progress(double pct, int blocksSoFar, Date date) {
                Log.e("HD","Syncing..."+pct+"%");
                notifyObservers(WalletNotificationType.SYNC_PROGRESS, new EventMessage<>(pct));
            }
            @Override
            public void doneDownload() {
                Log.e("HD","Sync Done.");
                notifyObservers(WalletNotificationType.SYNC_COMPLETED,null);
            }
        };
        walletAppKit.addListener(new Service.Listener() {
            @Override
            public void terminated(@NonNull Service.State from) {
                super.terminated(from);
                notifyObservers(WalletNotificationType.SYNC_STOPPED, null);
            }
        }, Runnable::run);
        walletAppKit.setDownloadListener(BTCListener);
        walletAppKit.setBlockingStartup(false);
        walletAppKit.setCheckpoints(walletInfo.checkPoint);
        if(walletInfo.restoreSeed != null){
            walletAppKit.restoreWalletFromSeed(walletInfo.restoreSeed, walletInfo.restoreAccounts);
        }
    }

    public void initWallet(){
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        addListeners();
    }

    public boolean isOnline(){
        return walletAppKit.isRunning();
    }

    public void initWalletOffline(){
        File walletFile = new File(directory, WALLET_FILE + ".wallet");
        if(walletFile.exists()){
            try (FileInputStream walletStream = new FileInputStream(walletFile)) {
                List<WalletExtension> extensions = ImmutableList.of();
                WalletExtension[] extArray = extensions.toArray(new WalletExtension[extensions.size()]);
                Protos.Wallet proto = WalletProtobufSerializer.parseToProto(walletStream);
                final WalletProtobufSerializer serializer = new WalletProtobufSerializer();
                wallet = serializer.readWallet(walletInfo.parameters, extArray, Constants.WALLET_STRUCTURE, walletInfo.accountIndex, proto);
                notifyObservers(WalletNotificationType.SETUP_COMPLETED, null);
            } catch (IOException | UnreadableWalletException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void stopWallet(){
        walletAppKit.stopAsync();
        walletAppKit.awaitTerminated();
    }

    public void encryptWallet(String passphrase){
        walletAppKit.wallet().encrypt(passphrase);
    }

    public void decryptWallet(String passphrase){
        try{
            wallet.decrypt(passphrase);
        } catch (Wallet.BadWalletEncryptionKeyException e){
            Log.e("HD","Wrong password");
        } catch (KeyCrypterException ke){
            Log.e("HD","Key crypter fail");
        }
    }

    /***
     * Require synced blockchain to process
     */
    public boolean handleBluetoothReceivedTx(final Transaction tx) {
        Log.i("HD",tx.getTxId() + " arrived via bluetooth");

        try {
            if (wallet.isTransactionRelevant(tx)) {
                wallet.receivePending(tx, null);

                walletAppKit.peerGroup().broadcastTransaction(tx);
            } else {
                Log.e("HD", tx.getTxId() + " tx is irrelevant");
            }

            return true;
        } catch (final VerificationException x) {
            Log.e("HD","cannot verify tx " + tx.getTxId() + " received via bluetooth");
        }

        return false;
    }

    public static class WalletInfo{
        public DeterministicSeed restoreSeed;
        public List<HDPath> restoreAccounts;
        public String label;
        public InputStream checkPoint;
        public int accountIndex;
        public NetworkParameters parameters;
    }

    public static class EventMessage<T> {
        T content;

        public EventMessage(T content) {
            this.content = content;
        }

        public T getContent() {
            return content;
        }
    }

    public interface EventListener {
        void update(WalletNotificationType type,@Nullable EventMessage<?> content);
    }
}
