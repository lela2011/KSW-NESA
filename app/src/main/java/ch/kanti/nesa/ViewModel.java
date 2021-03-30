package ch.kanti.nesa;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grades;
import ch.kanti.nesa.tables.Subjects;
import ch.kanti.nesa.tables.User;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private final InfoRepository infoRepository;
    private final BankRepository bankRepository;
    private final GradesRepository gradesRepository;
    private final SubjectsRepository subjectsRepository;
    private final AbsenceRepository absenceRepository;
    //initialize repository
    public ViewModel(@NonNull Application application) {
        super(application);
        infoRepository = new InfoRepository(application);
        bankRepository = new BankRepository(application);
        gradesRepository = new GradesRepository(application);
        subjectsRepository = new SubjectsRepository(application);
        absenceRepository = new AbsenceRepository(application);
    }

    public void insertInfo(List<AccountInfo> info) {
        infoRepository.insert(info);
    }

    public LiveData<List<AccountInfo>> getAccountInfo() {
        return infoRepository.getAccountInfo();
    }

    public void insertBank(List<BankStatement> statements) {
        bankRepository.insert(statements);
    }

    public void deleteAllBank() {
        bankRepository.deleteAll();
    }

    public LiveData<List<BankStatement>> getBankStatements() {
        return bankRepository.getBankStatement();
    }

    public LiveData<Float> getBalance(){
        return bankRepository.getBalance();
    }

    public void insertGrades(List<Grades> grades) {
        gradesRepository.insert(grades);
    }

    public LiveData<List<Grades>> getGradeBySubject(String passedSubject) {
        return gradesRepository.getBySubject(passedSubject);
    }

    public void insertSubjects(List<Subjects> subjects) {
        subjectsRepository.insert(subjects);
    }

    public LiveData<List<Subjects>> getSubjects() {
        return subjectsRepository.getSubjects();
    }

    public LiveData<Float> getSubjectAverage() {
        return subjectsRepository.getAverage();
    }

    public LiveData<Float> getPluspoints() {
        return subjectsRepository.getPluspoints();
    }

    public void updateNameSubject(String id, String name) {
        subjectsRepository.updateName(id, name);
    }

    public void insertAbsences(List<Absence> absences) {
        absenceRepository.insert(absences);
    }

    public LiveData<List<Absence>> getAbsences () {
        return absenceRepository.getAbsences();
    }

    public LiveData<Integer> getAbsenceSize() {
        return absenceRepository.getAbsenceSize();
    }


}