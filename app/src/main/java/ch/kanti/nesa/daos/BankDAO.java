package ch.kanti.nesa.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import ch.kanti.nesa.tables.BankStatement;

import java.util.List;

@Dao
public interface BankDAO {
    //to insert all of the scraped bank table at the beginning
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(List<BankStatement> statement);

    @Query("DELETE FROM bank_table")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM bank_table")
    int size();

    //get all of the rows in ascending order (oldest to newest)
    @Query("SELECT * FROM bank_table ORDER BY `order` ASC")
    LiveData<List<BankStatement>> getBankStatement();

    @Query("SELECT balance FROM bank_table ORDER BY `order` DESC LIMIT 1")
    LiveData<Float> getBalance();
}
