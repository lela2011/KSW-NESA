package ch.kanti.nesa.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.stream.Collectors;

import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.activities.LessonDetailView;
import ch.kanti.nesa.adapters.TimetableAdapter;
import ch.kanti.nesa.daos.LessonDAO;
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

        binding.previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = date.minusDays(1);
                setRecyclerView();
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = date.plusDays(1);
                setRecyclerView();
            }
        });

        binding.datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), listener, date.getYear(), date.getMonthValue()-1, date.getDayOfMonth());
                datePickerDialog.show();
            }
        });

        adapter.setOnItemClickListener(new TimetableAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Lesson lesson) {
                Intent intent = new Intent(getContext(), LessonDetailView.class);
                intent.putExtra("subject", lesson.getSubject());
                intent.putExtra("date", lesson.getDay());
                intent.putExtra("time", String.format("%s - %s", lesson.getStartTime(), lesson.getEndTime()));
                intent.putExtra("teacher", lesson.getTeacherShort());
                intent.putExtra("room", lesson.getRoom());
                intent.putExtra("marking", lesson.getMarking());
                intent.putExtra("comment", lesson.getComment());
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView () {
        viewModel.getLessons(date.toString()).observe(getViewLifecycleOwner(), lessons -> {
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
                    if (lessons.get(position).getLesson() == 0) {
                        return factor;
                    } else {
                        return factor / lessons.get(position).getSiblingLessons();
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
