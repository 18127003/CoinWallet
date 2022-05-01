package me.app.coinwallet.data.wallets;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WalletInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(WalletInfoEntry walletInfoEntry);

    @Query("DELETE FROM wallet_info WHERE label = :label")
    void delete(String label);

    @Query("SELECT * FROM wallet_info ORDER BY label COLLATE LOCALIZED ASC")
    LiveData<List<WalletInfoEntry>> getAll();
}
