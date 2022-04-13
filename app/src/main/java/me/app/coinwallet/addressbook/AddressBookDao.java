package me.app.coinwallet.addressbook;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
import java.util.Set;

@Dao
public interface AddressBookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(AddressBookEntry addressBookEntry);

    @Query("DELETE FROM address_book WHERE address = :address")
    void delete(String address);

    @Query("SELECT label FROM address_book WHERE address = :address")
    String resolveLabel(String address);

    @Query("SELECT * FROM address_book WHERE address LIKE '%' || :filter || '%' OR label LIKE '%' || :filter || '%' ORDER BY label COLLATE LOCALIZED ASC")
    List<AddressBookEntry> get(String filter);

    @Query("SELECT * FROM address_book ORDER BY label COLLATE LOCALIZED ASC")
    LiveData<List<AddressBookEntry>> getAll();

    @Query("SELECT * FROM address_book WHERE address NOT IN (:except) ORDER BY label COLLATE LOCALIZED ASC")
    LiveData<List<AddressBookEntry>> getAllExcept(Set<String> except);
}
