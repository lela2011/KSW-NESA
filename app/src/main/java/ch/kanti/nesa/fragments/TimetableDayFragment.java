package ch.kanti.nesa.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.activities.LessonDetailView;
import ch.kanti.nesa.adapters.TimetableAdapter;
import ch.kanti.nesa.databinding.FragmentTimetableDayBinding;
import ch.kanti.nesa.tables.Lesson;

public class TimetableDayFragment extends Fragment {

    public FragmentTimetableDayBinding binding;
    public ViewModel viewModel;
    public RecyclerView recyclerView;
    public TimetableAdapter adapter = new TimetableAdapter();
    int factor;


    LocalDate date = LocalDate.now();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTimetableDayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
        recyclerView = binding.recyclerView;
        recyclerView.setAdapter(adapter);
        LocalDate now = LocalDate.now();

        binding.title.setText(now.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
        setRecyclerView();

        binding.previous.setOnClickListener(v -> {
            date = date.minusDays(1);
            setRecyclerView();
        });

        binding.next.setOnClickListener(v -> {
            date = date.plusDays(1);
            setRecyclerView();
        });

        binding.datePicker.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), listener, date.getYear(), date.getMonthValue()-1, date.getDayOfMonth());
            datePickerDialog.show();
        });

        adapter.setOnItemClickListener(lesson -> {
            Intent intent = new Intent(getContext(), LessonDetailView.class);
            intent.putExtra("subject", lesson.getSubject());
            intent.putExtra("date", lesson.getDay());
            intent.putExtra("time", String.format("%s - %s", lesson.getStartTime(), lesson.getEndTime()));
            intent.putExtra("teacher", lesson.getTeacherShort());
            intent.putExtra("room", lesson.getRoom());
            intent.putExtra("marking", lesson.getMarking());
            intent.putExtra("comment", lesson.getComment());
            startActivity(intent);
        });
    }

    private void setRecyclerView () {
        viewModel.getLessons(date.toString()).observe(getViewLifecycleOwner(), lessons -> {

            HashMap<String, Integer> parallelLessons = new HashMap<>();
            parallelLessons.put("07:40",0);
            parallelLessons.put("08:30",0);
            parallelLessons.put("09:35",0);
            parallelLessons.put("10:25",0);
            parallelLessons.put("11:20",0);
            parallelLessons.put("12:10",0);
            parallelLessons.put("13:00",0);
            parallelLessons.put("13:50",0);
            parallelLessons.put("14:45",0);
            parallelLessons.put("15:35",0);
            parallelLessons.put("16:30",0);
            parallelLessons.put("17:20",0);
            parallelLessons.put("18:00",0);
            parallelLessons.put("19:00",0);
            parallelLessons.put("20:00",0);
            parallelLessons.put("21:00",0);

            for(Lesson temp : lessons) {
                String startTime = temp.getStartTime();
                int parallelLessonCount = parallelLessons.get(startTime);
                parallelLessons.put(startTime, parallelLessonCount+1);
            }

            List<Integer> factors = lessons.stream()
                    .filter(c -> c.getSiblingLessons() != 0)
                    .map(Lesson::getSiblingLessons)
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
                        return  factor / parallelLessons.get(lessons.get(position).getStartTime());
                    }

                }
            });

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            adapter.setStatements(lessons);

            binding.title.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
        });
    }

    @SuppressLint("DefaultLocale")
    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            date = LocalDate.parse(String.format("%04d-%02d-%02d",year, month+1, dayOfMonth));
            binding.title.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)));
            setRecyclerView();
        }
    };
}
