package ch.kanti.nesa;

import android.app.Application;

import java.util.List;

import ch.kanti.nesa.daos.AbsenceDAO;
import ch.kanti.nesa.tables.Absence;

public class AbsenceRepository {
    AbsenceDAO absenceDAO;

    public AbsenceRepository(Application application) {
        Database database = Database.getInstance(application);
        absenceDAO = database.absenceDAO();
    }

    public void insert(List<Absence> absences) {
        Database.databaseWriteExecutor.execute(() -> {
           absenceDAO.deleteAll();
           absenceDAO.insertAll(absences);
        });
    }
}
