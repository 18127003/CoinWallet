package me.app.coinwallet.utils;

import android.util.Log;
import androidx.annotation.Nullable;
import com.google.common.collect.ImmutableList;
import me.app.coinwallet.Constants;
import me.app.coinwallet.bitcoinj.WalletNotificationType;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptException;
import org.bitcoinj.wallet.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WalletUtil {

    /***
     * Get send to address from a tx script
     */
    @Nullable
    public static Address getToAddress(final Script script, NetworkParameters parameters) {
        try {
            return script.getToAddress(parameters, true);
        } catch (final ScriptException x) {
            return null;
        }
    }

    /***
     * Get first address of tx output which is not belong to wallet user
     */
    @Nullable
    public static List<Address> getSentAddress(final Transaction tx, final Wallet wallet) {
        List<Address> receivers = new ArrayList<>();
        for (final TransactionOutput output : tx.getOutputs()) {
            if (!output.isMine(wallet)) {
                final Script script = output.getScriptPubKey();
                final Address address = getToAddress(script, wallet.getNetworkParameters());
                if (address != null)
                    receivers.add(address);
            }
        }
        return receivers;
    }

    /***
     * Get first address of tx output which is belong to wallet user
     */
    @Nullable
    public static Address getReceivedAddress(final Transaction tx, final Wallet wallet) {
        for (final TransactionOutput output : tx.getOutputs()) {
            if (output.isMine(wallet)) {
                final Script script = output.getScriptPubKey();
                final Address address = getToAddress(script, wallet.getNetworkParameters());
                if (address != null)
                    return address;
            }
        }

        return null;
    }

    /***
     * Check if all tx input and output/ connected output is belong to wallet user
     */
    public static boolean isEntirelySelf(final Transaction tx, final Wallet wallet) {
        for (final TransactionInput input : tx.getInputs()) {
            final TransactionOutput connectedOutput = input.getConnectedOutput();
            if (connectedOutput == null || !connectedOutput.isMine(wallet))
                return false;
        }

        for (final TransactionOutput output : tx.getOutputs()) {
            if (!output.isMine(wallet))
                return false;
        }

        return true;
    }

    public static boolean isTxRelated(final Transaction tx, final Wallet wallet) {
        for (final TransactionInput input: tx.getInputs()){
            final TransactionOutput connectedOutput = input.getConnectedOutput();
            if (connectedOutput != null && connectedOutput.isMine(wallet)){
                return true;
            }
        }

        for (final TransactionOutput output: tx.getOutputs()) {
            if(output.isMine(wallet)){
                return true;
            }
        }
        return false;
    }

    /***
     * Check if tx contains too much outputs
     */
    public static boolean isPayToManyTransaction(final Transaction transaction) {
        return transaction.getOutputs().size() > 20;
    }

    /***
     * Returned coin is negative if is sent value and positive otherwise
     */
    public static Coin getRelatedValue(final Transaction transaction, TransactionBag wallet){
        Coin amount = transaction.getValueSentFromMe(wallet);
        Coin received = transaction.getValueSentToMe(wallet);
        if(amount.isZero()){
            amount = received;
        } else {
            amount = amount.minus(received).negate();
        }
        return amount;
    }

    public static String encryptMnemonic(DeterministicSeed seed, String label){
        String content = seed.getMnemonicString() + "," + seed.getCreationTimeSeconds();
        return CryptoEngine.getInstance().cipher(label, content);
    }

    public static DeterministicSeed decryptMnemonic(String encrypted, String label) {
        String decrypted = CryptoEngine.getInstance().decipher(label, encrypted);
        String mnemonic = decrypted.split(",")[0];
        String creationTime = decrypted.split(",")[1];
        long creationTimeL = 0L;
        try {
            creationTimeL = Long.parseLong(creationTime);
        } catch (NumberFormatException e){
            // swallow
        }
        try {
            return new DeterministicSeed(mnemonic, null, "", creationTimeL);
        } catch (UnreadableWalletException e) {
            return null;
        }
    }

    public static List<Integer> accounts(File walletFile){
        if(walletFile.exists()){
            try (FileInputStream walletStream = new FileInputStream(walletFile)) {
                Protos.Wallet proto = WalletProtobufSerializer.parseToProto(walletStream);
                KeyChainGroup keyChainGroup;
                if (proto.hasEncryptionParameters()) {
                    Protos.ScryptParameters encryptionParameters = proto.getEncryptionParameters();
                    KeyCrypterScrypt keyCrypter = new KeyCrypterScrypt(encryptionParameters);
                    keyChainGroup = KeyChainGroup.fromProtobufEncrypted(proto.getKeyList(), keyCrypter);
                } else {
                    keyChainGroup = KeyChainGroup.fromProtobufUnencrypted(proto.getKeyList());
                }
                List<HDPath> accounts = keyChainGroup.accounts();
                return accounts.stream().map(Constants.WALLET_STRUCTURE::accountIndexOf).collect(Collectors.toList());
            } catch (IOException | UnreadableWalletException ioException) {
                ioException.printStackTrace();
            }
        }
        return Collections.emptyList();
    }
}
