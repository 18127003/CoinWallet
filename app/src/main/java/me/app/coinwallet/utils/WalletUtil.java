package me.app.coinwallet.utils;

import androidx.annotation.Nullable;
import me.app.coinwallet.Constants;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptException;
import org.bitcoinj.wallet.Wallet;

public class WalletUtil {

    /***
     * Get send to address from a tx script
     */
    @Nullable
    public static Address getToAddress(final Script script) {
        try {
            return script.getToAddress(Constants.NETWORK_PARAMETERS, true);
        } catch (final ScriptException x) {
            return null;
        }
    }

    /***
     * Get first address of tx output which is not belong to wallet user
     */
    @Nullable
    public static Address getSentAddress(final Transaction tx, final Wallet wallet) {
        for (final TransactionOutput output : tx.getOutputs()) {
            if (!output.isMine(wallet)) {
                final Script script = output.getScriptPubKey();
                final Address address = getToAddress(script);
                if (address != null)
                    return address;
            }
        }
        return null;
    }

    /***
     * Get first address of tx output which is belong to wallet user
     */
    @Nullable
    public static Address getReceivedAddress(final Transaction tx, final Wallet wallet) {
        for (final TransactionOutput output : tx.getOutputs()) {
            if (output.isMine(wallet)) {
                final Script script = output.getScriptPubKey();
                final Address address = getToAddress(script);
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

}
