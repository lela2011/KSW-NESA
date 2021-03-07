package ch.kanti.nesa;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import ch.kanti.nesa.daos.AbsenceDAO;
import ch.kanti.nesa.tables.Absence;

public class AbsenceRepository {
    AbsenceDAO absenceDAO;
    Context context;


    public AbsenceRepository(Application application) {
        Database database = Database.getInstance(application);
        absenceDAO = database.absenceDAO();
        context = application.getApplicationContext();
    }

    public AbsenceRepository(Context context) {
        Database database = Database.getInstance(context);
        absenceDAO = database.absenceDAO();
        this.context = context;
    }

    public void insert(List<Absence> absences) {
        Database.databaseWriteExecutor.execute(() -> {

           List<Absence> oldAbsences = absenceDAO.getAbsencesSync();

           if (oldAbsences.size() > absences.size()) {
               absenceDAO.deleteAll();
           } else if (oldAbsences.size() != absences.size()) {
               for (int i = 0; i < oldAbsences.size(); i++) {
                   Absence oldAbs = oldAbsences.get(i);
                   for (int k = 0; k < absences.size(); k++) {
                       Absence newAbs = absences.get(k);
                       if (oldAbs.getDate().equals(newAbs.getDate()) &&
                               oldAbs.getType() == newAbs.getType() &&
                               oldAbs.getCourse().equals(newAbs.getCourse()) &&
                               oldAbs.getTime().equals(newAbs.getTime()) &&
                               oldAbs.getExcused() == newAbs.getExcused()) {
                           absences.remove(k);
                       }
                   }
               }
               absenceDAO.insertAll(absences);
           }
        });
    }

    public LiveData<List<Absence>> getAbsences() {
        return absenceDAO.getAbsences();
    }
}
