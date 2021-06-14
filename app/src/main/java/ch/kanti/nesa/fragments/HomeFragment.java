package ch.kanti.nesa.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.kanti.nesa.App;
import ch.kanti.nesa.activities.MainActivity;
import ch.kanti.nesa.ViewModel;

import ch.kanti.nesa.R;
import ch.kanti.nesa.adapters.TimetableAdapter;
import ch.kanti.nesa.databinding.FragmentHomeBinding;
import ch.kanti.nesa.scrapers.ContentScrapers;
import ch.kanti.nesa.tables.Lesson;
import ch.kanti.nesa.tables.Subject;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static ch.kanti.nesa.activities.MainActivity.SHORTCUT_ABSENCE;
import static ch.kanti.nesa.activities.MainActivity.SHORTCUT_BANK;
import static ch.kanti.nesa.activities.MainActivity.SHORTCUT_GRADES;
import static ch.kanti.nesa.activities.MainActivity.SHORTCUT_STUDENTS;

public class HomeFragment extends Fragment {

    public FragmentHomeBinding binding;
    private HomeFragmentShortcut shortcut;
    public static final DecimalFormat df = new DecimalFormat("#.###");
    public ViewModel viewModel;
    public RecyclerView recyclerView;
    public TimetableAdapter adapter = new TimetableAdapter();
    int factor;

    int col1, col2, col3, col4;
    float range3, range4;

    public interface HomeFragmentShortcut {
        void onShortcutClicked(int shortcut);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        col1 = App.sharedPreferences.getInt("colCol1",  requireContext().getColor(R.color.gold));
        col2 = App.sharedPreferences.getInt("colCol2",  requireContext().getColor(R.color.green));
        col3 = App.sharedPreferences.getInt("colCol3",  requireContext().getColor(R.color.orange));
        col4 = App.sharedPreferences.getInt("colCol4",  requireContext().getColor(R.color.red));
        range3 = App.sharedPreferences.getFloat("colRange1", 5f);
        range4 = App.sharedPreferences.getFloat("colRange2", 4f);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        int lesson = App.getLessonIndex(LocalTime.now()) + 1;
        String date = LocalDate.now().toString();
        if(lesson == -1) {
            date = LocalDate.now().plusDays(1).toString();
            lesson = 1;
        }
        getLesson(date, lesson);
    }

    private void getLesson(String date, int lesson) {
        viewModel.getNextLesson(date, lesson).observe(getViewLifecycleOwner(), new Observer<List<Lesson>>() {
            @Override
            public void onChanged(List<Lesson> lessons) {
                if (lessons.size() == 0) {
                    if(lesson == 17) {
                        getLesson(LocalDate.parse(date).plusDays(1).toString(), 1);
                    } else {
                        getLesson(date, lesson+1);
                    }
                }

                HashMap<Integer, Integer> parallelLessons = new HashMap<>();
                parallelLessons.put(0,0);
                parallelLessons.put(1,0);
                parallelLessons.put(2,0);
                parallelLessons.put(3,0);
                parallelLessons.put(4,0);
                parallelLessons.put(5,0);
                parallelLessons.put(6,0);
                parallelLessons.put(7,0);
                parallelLessons.put(8,0);
                parallelLessons.put(9,0);
                parallelLessons.put(10,0);
                parallelLessons.put(11,0);
                parallelLessons.put(12,0);
                parallelLessons.put(13,0);
                parallelLessons.put(14,0);
                parallelLessons.put(15,0);
                parallelLessons.put(16,0);

                for(Lesson temp : lessons) {
                    int startTime = temp.getLesson();
                    int parallelLessonCount = parallelLessons.get(startTime);
                    parallelLessons.put(startTime, parallelLessonCount+1);
                }

                /*List<Integer> factors_ = lessons.stream()
                        .filter(c -> c.getSiblingLessons() != 0)
                        .map(Lesson::getSiblingLessons)
                        .distinct()
                        .collect(Collectors.toList());*/

                List<Integer> factors = parallelLessons.values()
                        .stream()
                        .filter(c -> c != 0)
                        .distinct()
                        .collect(Collectors.toList());

                factor = 1;

                for (int tempFactor : factors) {
                    factor = factor * tempFactor;
                }

                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), factor);
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        //return factor;
                        if (lessons.get(position).getLesson() == 0) {
                            return factor;
                        } else {
                            return  factor / parallelLessons.get(lessons.get(position).getLesson());
                        }
                    }
                });

                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                adapter.setStatements(lessons);
                adapter.setOnItemClickListener(new TimetableAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Lesson lesson) {
                        shortcut.onShortcutClicked(MainActivity.SHORTCUT_TIMETABLE);
                    }
                });
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        recyclerView = binding.recyclerView;
        recyclerView.setAdapter(adapter);

        ArrayList<TextView> personalInfoTextViews = new ArrayList<>(8);
        personalInfoTextViews.add(binding.name);
        personalInfoTextViews.add(binding.address);
        personalInfoTextViews.add(binding.city);
        personalInfoTextViews.add(binding.birthdate);
        personalInfoTextViews.add(binding.major);
        personalInfoTextViews.add(binding.familyorigin);
        personalInfoTextViews.add(binding.phone);
        personalInfoTextViews.add(binding.mobilephone);
        personalInfoTextViews.add(binding.schoolMail);
        personalInfoTextViews.add(binding.privatemail);

        MainActivity.viewModel.getAccountInfo().observe(getViewLifecycleOwner(), accountInfos -> {
            for(int i = 0; i < accountInfos.size(); i++){
                personalInfoTextViews.get(i).setText(accountInfos.get(i).value);
            }
        });

        viewModel.getBalance().observe(getViewLifecycleOwner(), aFloat -> {
            if(aFloat != null){
                String balance = aFloat + " " + getString(R.string.chf);
                binding.balance.setText(balance);
                if(aFloat >= 100){
                    binding.balance.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
                } else if(aFloat > 0) {
                    binding.balance.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange));
                } else {
                    binding.balance.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                }
            }
        });

        viewModel.getSubjectAverage().observe(getViewLifecycleOwner(), aFloat -> {
            if(aFloat != null) {
                binding.gradeAverage.setText(String.valueOf(df.format(aFloat)));
                if (aFloat == 6.0f) {
                    binding.gradeAverage.setTextColor(col1);
                } else if (range3 > range4 && aFloat >= range3) {
                    binding.gradeAverage.setTextColor(col2);
                } else if (range4 > range3 && aFloat >= range4) {
                    binding.gradeAverage.setTextColor(col2);
                } else if (range3 > range4 && aFloat >= range4) {
                    binding.gradeAverage.setTextColor(col3);
                } else if (range4 > range3 && aFloat >= range3) {
                    binding.gradeAverage.setTextColor(col4);
                } else if (range3 > range4 && aFloat < range4 && aFloat != -1) {
                    binding.gradeAverage.setTextColor(col4);
                } else if (range4 > range3 && aFloat < range3 && aFloat != -1) {
                    binding.gradeAverage.setTextColor(col3);
                }
            } else {
                binding.gradeAverage.setText("-");
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = requireContext().getTheme();
                theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
                binding.gradeAverage.setTextColor(typedValue.data);
            }
        });

        viewModel.getSubjects().observe(getViewLifecycleOwner(), subjects -> {
            if (subjects != null) {
                float pluspoints = ContentScrapers.calculatePromotionPoints(subjects);
                binding.pluspoints.setText(df.format(pluspoints));
                if (pluspoints > 2) {
                    binding.pluspoints.setTextColor(requireContext().getColor(R.color.green));
                } else if (pluspoints <= 2 && pluspoints >= 0) {
                    binding.pluspoints.setTextColor(requireContext().getColor(R.color.orange));
                } else if (pluspoints < 0) {
                    binding.pluspoints.setTextColor(requireContext().getColor(R.color.red));
                } else if (pluspoints == -10.0f) {
                    binding.pluspoints.setText("-");
                    TypedValue typedValue = new TypedValue();
                    Resources.Theme theme = binding.pluspoints.getContext().getTheme();
                    theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true);
                    binding.pluspoints.setTextColor(typedValue.data);
                }
            }

        });

        viewModel.getAbsenceSize().observe(getViewLifecycleOwner(), integer -> {
            if (integer == 0) {
                binding.openAbsencesHome.setText("-");
            } else {
                binding.openAbsencesHome.setText(String.valueOf(integer));
            }
        });

        binding.bank.setOnClickListener(v -> shortcut.onShortcutClicked(SHORTCUT_BANK));

        binding.marks.setOnClickListener(v -> shortcut.onShortcutClicked(SHORTCUT_GRADES));

        binding.openAbsencesCard.setOnClickListener(view1 -> shortcut.onShortcutClicked(SHORTCUT_ABSENCE));

        binding.studentShortcut.setOnClickListener(v -> shortcut.onShortcutClicked(SHORTCUT_STUDENTS));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof HomeFragmentShortcut) {
            shortcut = (HomeFragmentShortcut) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "must implement HomeFragmentShortcut");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        shortcut = null;
    }
}
