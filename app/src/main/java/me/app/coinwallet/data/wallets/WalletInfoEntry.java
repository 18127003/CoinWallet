package me.app.coinwallet.data.wallets;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = WalletInfoEntry.TABLE_NAME)
public class WalletInfoEntry implements Serializable {
    public static final String TABLE_NAME = "wallet_info";

    @NonNull
    @ColumnInfo(name = "label")
    private String label;

    @NonNull
    @ColumnInfo(name = "network")
    private String networkId;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "account")
    private Integer accountIndex;

    public WalletInfoEntry(final int accountIndex, @NonNull String networkId, @NonNull final String label) {
        this.accountIndex = accountIndex;
        this.label = label;
        this.networkId = networkId;
    }

    @Ignore
    public WalletInfoEntry(@NonNull String networkId, @NonNull final String label) {
        this.accountIndex = 0;
        this.label = label;
        this.networkId = networkId;
    }

    public void setAccountIndex(@NonNull Integer accountIndex) {
        this.accountIndex = accountIndex;
    }

    @NonNull
    public String getLabel() {
        return label;
    }

    @NonNull
    public Integer getAccountIndex(){return accountIndex;}

    @NonNull
    public String getNetworkId() {
        return networkId;
    }
}
