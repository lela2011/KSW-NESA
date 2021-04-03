package ch.kanti.nesa.objects;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;

import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;

public class LoginAndScrape {

    private final boolean loginCorrect;
    private final boolean checkSuccessful;
    private final ArrayList<AccountInfo> accountInfos;
    private final ArrayList<Absence> absences;
    private final ArrayList<BankStatement> bankStatements;
    private final SubjectsAndGrades subjectsAndGrades;

    public LoginAndScrape(boolean loginCorrect, boolean checkSuccessful, ArrayList<AccountInfo> accountInfos, ArrayList<Absence> absences, ArrayList<BankStatement> bankStatements, SubjectsAndGrades subjectsAndGrades) {
        this.loginCorrect = loginCorrect;
        this.checkSuccessful = checkSuccessful;
        this.accountInfos = accountInfos;
        this.absences = absences;
        this.bankStatements = bankStatements;
        this.subjectsAndGrades = subjectsAndGrades;
    }

    public boolean isLoginCorrect() {
        return loginCorrect;
    }

    public boolean isCheckSuccessful() {
        return checkSuccessful;
    }

    public ArrayList<AccountInfo> getAccountInfos() {
        return accountInfos;
    }

    public ArrayList<Absence> getAbsences() {
        return absences;
    }

    public ArrayList<BankStatement> getBankStatements() {
        return bankStatements;
    }

    public SubjectsAndGrades getSubjectsAndGrades() {
        return subjectsAndGrades;
    }
}
