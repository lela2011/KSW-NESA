package ch.kanti.nesa;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import ch.kanti.nesa.daos.StudentDAO;
import ch.kanti.nesa.daos.SubjectsDAO;
import ch.kanti.nesa.tables.Grade;
import ch.kanti.nesa.tables.Student;
import ch.kanti.nesa.tables.Subject;

public class StudentRepository {
    final StudentDAO studentDAO;
    final Context context;

    public StudentRepository(Application application) {
        Database database = Database.getInstance(application);
        studentDAO = database.studentDAO();
        context = application.getApplicationContext();
    }

    public StudentRepository(Context context) {
        Database database = Database.getInstance(context);
        studentDAO = database.studentDAO();
        this.context = context;
    }

    public void insert(List<Student> students) {
        studentDAO.deleteAll();
        for (Student student : students) {
            studentDAO.insert(student);
        }
    }

    public LiveData<List<Student>> getStudents() {
        return studentDAO.getStudents();
    }


}
