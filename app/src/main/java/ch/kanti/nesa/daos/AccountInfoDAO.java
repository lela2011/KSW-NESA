package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ch.kanti.nesa.tables.AccountInfo;

import java.util.List;

@Dao
public interface AccountInfoDAO {
    //insert into account_table
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AccountInfo> infos);

    //select all items from account_table and order by ascending id
    @Query("SELECT * FROM account_table ORDER BY `order` ASC")
    LiveData<List<AccountInfo>> getAccountInfoLive();

    @Query("SELECT * FROM account_table ORDER BY `order` ASC")
    List<AccountInfo> getAccountInfo();

    @Query("DELETE FROM account_table")
    void deleteAll();
}
