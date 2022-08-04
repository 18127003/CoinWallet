package me.app.coinwallet.bitcoinj;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.KeyChainGroupStructure;

public class Bip44KeyChainGroupStructure implements KeyChainGroupStructure {
    @Override
    public HDPath accountPathFor(int i, Script.ScriptType scriptType) {
        if (scriptType != null && scriptType != Script.ScriptType.P2PKH) {
            if(scriptType == Script.ScriptType.P2SH){
                return HDPath.M(new ChildNumber(44, true))
                        .extend(new ChildNumber(0, true))  // Bitcoin
                        .extend(new ChildNumber(i, true)); // Account 0
            }
            if (scriptType == Script.ScriptType.P2WPKH) {
                return DeterministicKeyChain.ACCOUNT_ONE_PATH;
            } else {
                throw new IllegalArgumentException(scriptType.toString());
            }
        } else {
            return HDPath.M(new ChildNumber(44, true))
                    .extend(new ChildNumber(0, true))  // Bitcoin
                    .extend(new ChildNumber(i, true)); // Account 0
        }
    }

    private static Bip44KeyChainGroupStructure instance;

    private Bip44KeyChainGroupStructure(){}

    public static Bip44KeyChainGroupStructure get(){
        if(instance==null){
            instance = new Bip44KeyChainGroupStructure();
        }
        return instance;
    }
}
