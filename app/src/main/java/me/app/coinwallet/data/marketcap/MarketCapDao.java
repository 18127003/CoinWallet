package me.app.coinwallet.data.marketcap;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MarketCapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(MarketCapEntity marketCapEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateAll(List<MarketCapEntity> marketCapEntries);

    @Query("SELECT * FROM market_caps ORDER BY market_cap_rank COLLATE LOCALIZED ASC Limit 20" )
    LiveData<List<MarketCapEntity>> findAll();

    @Query("SELECT * FROM market_caps WHERE name LIKE '%' || :filter || '%' ORDER BY name " +
            "COLLATE LOCALIZED ASC")
    LiveData<List<MarketCapEntity>> findByFilter(String filter);

    @Query("SELECT * FROM market_caps WHERE name = :name")
    MarketCapEntity findByName(String name);
}
