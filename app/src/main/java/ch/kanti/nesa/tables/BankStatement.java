package ch.kanti.nesa.tables;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bank_table")
public class BankStatement {
    @PrimaryKey
    @NonNull
    private String pk;
    private int order;
    private String date;
    private String title;
    private float amount;
    private float balance;

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

    public void setPk(@NonNull String pk) {
        this.pk = pk;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
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
