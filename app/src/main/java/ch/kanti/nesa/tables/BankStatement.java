package ch.kanti.nesa.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bank_table")
public class BankStatement {
    @PrimaryKey
    @NonNull
    private final String pk;
    private final int order;
    private final String date;
    private final String title;
    private final float amount;
    private final float balance;

    public BankStatement(@NonNull String pk, int order, String date, String title, float amount, float balance) {
        this.pk = pk;
        this.order = order;
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.balance = balance;
    }

    @NonNull
    public String getPk() {
        return pk;
    }

    public int getOrder() {
        return order;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public float getAmount() {
        return amount;
    }

    public float getBalance() {
        return balance;
    }

    public boolean compare(BankStatement statement) {
        return this.getTitle().equals(statement.getTitle()) &&
                this.getDate().equals(statement.getDate()) &&
                this.getAmount() == statement.getAmount() &&
                this.getBalance() == statement.getBalance();

    }

    public boolean bankModified(BankStatement statement) {
        return this.getDate().equals(statement.getDate());

    }
}
