package me.app.coinwallet;

import android.util.Log;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

public class LocalWallet {
    private static LocalWallet _instance = null;

    private final List<LocalWalletListener> observers = new ArrayList<>();

    public void subscribe(LocalWalletListener listener){
        observers.add(listener);
    }

    private void notifyObservers(WalletNotificationType type, String content){
        for(LocalWalletListener l : observers){
            l.update(type, content);
        }
    }

    public static synchronized LocalWallet getInstance(){
        if (_instance == null){
            _instance = new LocalWallet();
        }
        return _instance;
    }

    private File directory;
    private WalletAppKit walletAppKit;
    private NetworkParameters parameters;

    public void setParameters(NetworkParameters parameters) {
        this.parameters = parameters;
    }

    public void setDirectory(File directory) { this.directory = directory; }

    public Address getAddress(){
        Log.e("HD","CRA: "+walletAppKit.wallet().currentReceiveAddress().toString());
        Log.e("HD","CCA: "+walletAppKit.wallet().currentChangeAddress().toString());
        for(ECKey k: walletAppKit.wallet().getIssuedReceiveKeys()){
            Log.e("HD","IRK: "+Address.fromKey(parameters,k, Script.ScriptType.P2PKH));
        }
        return walletAppKit.wallet().currentReceiveAddress();
    }

    public String getPlainBalance(){
        return walletAppKit.wallet().getBalance().toPlainString();
    }

    public void addKey(){
        String mnemonic = walletAppKit.wallet().getKeyChainSeed().getMnemonicString();
        Log.e("HD", mnemonic);
    }

    public Wallet wallet(){
        return walletAppKit.wallet();
    }

    public List<Transaction> history(){
        Set<Transaction> txs = walletAppKit.wallet().getTransactions(true);
        return new ArrayList<>(txs);
    }

    private void addListeners(){
        onReceive();
    }

    public void check(){
        final List<TransactionOutput> to = walletAppKit.wallet().getUnspents();
        Log.e("HD","UTXO num: "+to.size());
        for(TransactionOutput txo : to){
            Log.e("HD","Transaction value "+txo.getValue().toPlainString()+", min non dust "+txo.getMinNonDustValue());
        }
    }

    public void send(String sendAddress, double value){
        final Coin amountToSend = Coin.ofBtc(BigDecimal.valueOf(value));
        Log.e("HD","Amount to send: "+amountToSend.toPlainString());

        try {
            final Address sendTo = Address.fromString(parameters, sendAddress);
            SendRequest request = SendRequest.to(sendTo, amountToSend);
            request.feePerKb = Transaction.REFERENCE_DEFAULT_MIN_TX_FEE;
            final Wallet.SendResult sendResult = walletAppKit.wallet().sendCoins(walletAppKit.peerGroup(), request);
            Log.e("HD","Sending "+amountToSend.toPlainString()+" BTC");
            sendResult.broadcastComplete.addListener(() -> {
                Log.e("HD", "Tx accepted");
                notifyObservers(WalletNotificationType.TX_ACCEPTED, "");
            }, Runnable::run);
        } catch (InsufficientMoneyException e){
            Log.e("HD","Insufficient money");
        } catch (Wallet.DustySendRequested d){
            Log.e("HD","Dusty send request");
        } catch (Wallet.ExceededMaxTransactionSize m){
            Log.e("HD","Exceed max transaction size");
        } catch (AddressFormatException.WrongNetwork n) {
            Log.e("HD","Wrong network for this address");
        } catch(AddressFormatException a){
            Log.e("HD","Wrong address format");
        } catch (IllegalArgumentException i){
            Log.e("HD","Double spending");
        }
    }

    private void onReceive(){
        walletAppKit.wallet().addCoinsReceivedEventListener((wallet, tx, prevBalance, newBalance) -> {
            Coin value = tx.getValueSentToMe(wallet);
            Log.e("HD","Received tx for " + value.toFriendlyString() + ": " + tx);
            Futures.addCallback(tx.getConfidence().getDepthFuture(1), new FutureCallback<TransactionConfidence>() {
                @Override
                public void onSuccess(TransactionConfidence result) {
                    Log.e("HD","Receipt of "+result.getTransactionHash().toString()+" reached 1 confirmation");
                    notifyObservers(WalletNotificationType.TX_ACCEPTED,"");
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

    public void configWallet(String prefix){
        walletAppKit = new WalletAppKit(parameters, directory, prefix) {
            @Override
            protected void onSetupCompleted() {
                Log.e("HD", "Set up complete");
                notifyObservers(WalletNotificationType.SETUP_COMPLETED, "");
            }

        };
        DownloadProgressTracker BTCListener = new DownloadProgressTracker() {
            @Override
            public void progress(double pct, int blocksSoFar, Date date) {
                Log.e("HD","Syncing..."+pct+"%");
                notifyObservers(WalletNotificationType.SYNC_PROGRESS, String.valueOf(pct));
            }
            @Override
            public void doneDownload() {
                Log.e("HD","Sync Done.");
                notifyObservers(WalletNotificationType.SYNC_COMPLETED,"");
            }
        };
        walletAppKit.setDownloadListener(BTCListener);
        walletAppKit.setBlockingStartup(false);
    }

    public void initWallet(){
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();
        addListeners();

    }

    public void restoreWallet(String mnemonic){
        try{
            String passphrase = "";
            long creationTime = 1409478661L;
            walletAppKit.restoreWalletFromSeed(new DeterministicSeed(mnemonic, null, passphrase, creationTime));
        } catch (UnreadableWalletException ex){
            Log.e("HD","Unreadable wallet");
        }
    }
}
