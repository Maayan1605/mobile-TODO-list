package com.example.todolistandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private Button goToCreateBtn;
    private TextView noTasksLbl;
    private ListView listView;
    private ArrayList<TaskModel> taskModels;
    private TaskAdapter taskAdapter;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Home page");
        noTasksLbl = findViewById(R.id.no_tasks_lbl);
        goToCreateBtn = findViewById(R.id.go_to_create_btn);
        listView = findViewById(R.id.tasks_list_view);
        goToCreateBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        noTasksLbl.setVisibility(View.INVISIBLE);
        taskModels = new ArrayList<>();
        getAllTasks();
    }

    private void getAllTasks() {
        database
                .collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> data = document.getData();
                                TaskModel taskModel = new TaskModel(
                                        document.getId(),
                                        (String) data.get("title"),
                                        (String) data.get("deadline"),
                                        (String) data.get("imageUrl"),
                                        (String) data.get("description"),
                                        (String) data.get("submissionDate")
                                );
                                taskModels.add(taskModel);
                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error while getting all tasks", Toast.LENGTH_LONG).show();
                        }
                        if (taskModels.size() == 0)
                            noTasksLbl.setVisibility(View.VISIBLE);
                        taskAdapter = new TaskAdapter(getApplicationContext(), taskModels);
                        listView.setAdapter(taskAdapter);
                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            TaskModel taskModel = taskAdapter.getItem(position);
                            Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                            intent.putExtra("id", taskModel.getId());
                            intent.putExtra("title", taskModel.getTitle());
                            intent.putExtra("deadline", taskModel.getDeadline());
                            intent.putExtra("imageUrl", taskModel.getImageUrl());
                            intent.putExtra("subDate", taskModel.getSubmissionDate());
                            intent.putExtra("description", taskModel.getDescription());
                            startActivity(intent);
                        });
                    }
                });
    }
}