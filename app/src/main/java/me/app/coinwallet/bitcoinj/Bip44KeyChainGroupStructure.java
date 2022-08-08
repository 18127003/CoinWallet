package me.app.coinwallet.bitcoinj;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.HDPath;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.KeyChainGroupStructure;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Bip44KeyChainGroupStructure implements KeyChainGroupStructure {
    @Override
    public HDPath accountPathFor(NetworkParameters parameters, int accountIndex) {
        Integer network = NETWORKS.get(parameters.getId());
        if (network == null){
            throw new IllegalArgumentException(parameters.getId());
        }
        return HDPath.M(new ChildNumber(44, true))
                .extend(new ChildNumber(network, true))  // Bitcoin
                .extend(new ChildNumber(accountIndex, true)); // Account 0
    }

    public int accountIndexOf(HDPath accountPath) {
        return accountPath.get(accountPath.size()-1).num();
    }

    public int getNetworkOf(HDPath accountPath) {
        return accountPath.get(accountPath.size()-2).num();
    }
//
//    public int nextAvailableAccount(String networkId, List<HDPath> accounts){
//        Integer network = NETWORKS.get(networkId);
//        Multimap<Integer, Integer> mapByNetwork = HashMultimap.create();
//        for(HDPath path: accounts){
//            mapByNetwork.put(getNetworkOf(path), accountIndexOf(path));
//        }
//        Collection<Integer> accountsOfNetwork = mapByNetwork.get(network);
//        return accountsOfNetwork.size();
//    }

    private static final Map<String, Integer> NETWORKS = Stream.of(
            new AbstractMap.SimpleEntry<>("org.bitcoin.production", 0),
            new AbstractMap.SimpleEntry<>("org.bitcoin.test", 1)
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static Bip44KeyChainGroupStructure instance;

    private Bip44KeyChainGroupStructure(){}

    public static Bip44KeyChainGroupStructure get(){
        if(instance==null){
            instance = new Bip44KeyChainGroupStructure();
        }
        return instance;
    }
}
