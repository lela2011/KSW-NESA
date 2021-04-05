package ch.kanti.nesa.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ch.kanti.nesa.activities.ActivityDetailedStudent;
import ch.kanti.nesa.ViewModel;
import ch.kanti.nesa.adapters.StudentAdapter;
import ch.kanti.nesa.databinding.FragmentStudentsBinding;
import ch.kanti.nesa.tables.Student;

public class StudentsFragment extends Fragment {

    FragmentStudentsBinding binding;
    public ViewModel viewModel;

    public static RecyclerView recyclerView;
    public StudentAdapter studentAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);

        recyclerView = binding.recyclerViewStudents;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        studentAdapter = new StudentAdapter();
        recyclerView.setAdapter(studentAdapter);

        viewModel.getStudents().observe(getViewLifecycleOwner(), new Observer<List<Student>>() {
            @Override
            public void onChanged(List<Student> students) {
                studentAdapter.setStatements(students);
            }
        });

        studentAdapter.setOnItemClickListener(new StudentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Student student) {
                Intent intent = new Intent(getContext(), ActivityDetailedStudent.class);
                intent.putExtra("name", student.getName());
                intent.putExtra("address", student.getAddress());
                intent.putExtra("phone", student.getPhone());
                intent.putExtra("major", student.getDegree());
                intent.putExtra("course", student.getCourse());
                intent.putExtra("bilingual", student.getBilingual());
                intent.putExtra("gender", student.getGender());
                intent.putExtra("addCourses", student.getAdditionalCourses());
                intent.putExtra("status", student.getStatus());
                startActivity(intent);
            }
        });
    }
}
