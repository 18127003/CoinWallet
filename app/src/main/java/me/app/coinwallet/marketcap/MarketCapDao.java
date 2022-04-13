package me.app.coinwallet.marketcap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MarketCapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(MarketCapEntry marketCapEntry);

    @Query("SELECT * FROM market_caps ORDER BY name COLLATE LOCALIZED ASC")
    LiveData<List<MarketCapEntry>> findAll();

    @Query("SELECT * FROM market_caps WHERE name LIKE '%' || :constraint || '%' ORDER BY name " +
            "COLLATE LOCALIZED ASC")
    LiveData<List<MarketCapEntry>> findByConstraint(String constraint);

    @Query("SELECT * FROM market_caps WHERE name = :name")
    MarketCapEntry findByName(String name);
}
