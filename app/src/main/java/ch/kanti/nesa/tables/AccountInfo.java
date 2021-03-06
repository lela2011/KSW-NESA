package ch.kanti.nesa.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account_table")
public class AccountInfo {
    @PrimaryKey
    public final int order;
    public final String value;

    public AccountInfo(String value, int order) {
        this.value = value;
        this.order = order;
    }
}
