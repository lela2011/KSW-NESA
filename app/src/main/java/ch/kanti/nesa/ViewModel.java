package ch.kanti.nesa;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import ch.kanti.nesa.tables.Absence;
import ch.kanti.nesa.tables.AccountInfo;
import ch.kanti.nesa.tables.BankStatement;
import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Lesson;
import ch.kanti.nesa.tables.Student;
import ch.kanti.nesa.tables.Subject;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private final InfoRepository infoRepository;
    private final BankRepository bankRepository;
    private final GradesRepository gradesRepository;
    private final SubjectsRepository subjectsRepository;
    private final AbsenceRepository absenceRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    //initialize repository
    public ViewModel(@NonNull Application application) {
        super(application);
        infoRepository = new InfoRepository(application);
        bankRepository = new BankRepository(application);
        gradesRepository = new GradesRepository(application);
        subjectsRepository = new SubjectsRepository(application);
        absenceRepository = new AbsenceRepository(application);
        studentRepository = new StudentRepository(application);
        lessonRepository = new LessonRepository(application);
    }

    public void insertInfo(List<AccountInfo> info) {
        infoRepository.insert(info);
    }

    public LiveData<List<AccountInfo>> getAccountInfo() {
        return infoRepository.getAccountInfo();
    }

    public void deleteAllAccountInfo() {
        infoRepository.deleteAll();
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

    public void insertGrades(List<Grade> grades) {
        gradesRepository.insert(grades);
    }

    public LiveData<List<Grade>> getGradeBySubject(String passedSubject) {
        return gradesRepository.getBySubject(passedSubject);
    }

    public void deleteAllGrades() {
        gradesRepository.deleteAll();
    }

    public void insertSubjects(List<Subject> subjects) {
        subjectsRepository.insert(subjects);
    }

    public LiveData<List<Subject>> getSubjects() {
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

    public void deleteAllSubjects() {
        subjectsRepository.deleteAll();
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

    public void deleteAllAbsences() {
        absenceRepository.deleteAll();
    }

    public void insertStudents(List<Student> students) {
        studentRepository.insert(students);
    }

    public LiveData<List<Student>> getStudents() {
        return studentRepository.getStudents();
    }

    public void insertLessons(boolean week, List<Lesson> lessons, List<Lesson> exams) {
        lessonRepository.insert(week, lessons, exams);
    }

    public LiveData<List<Lesson>> getLessons(String day) {
        return lessonRepository.getLessons(day);
    }

    public LiveData<List<Lesson>> getNextLesson(String day, int lesson) {
        return lessonRepository.getNextLesson(day, lesson);
    }

    public void deleteAllStudents() {
        studentRepository.deleteAll();
    }

    public void deleteAllLessons() {
        lessonRepository.deleteAll();
    }
}