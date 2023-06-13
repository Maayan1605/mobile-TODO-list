package com.example.todolistandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class CreateTaskActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText titleEt, imageUrlEt, deadlineEt, descriptionEt;
    private Button cancelBtn, createBtn;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Create new task");
        setContentView(R.layout.activity_create_task);
        titleEt = findViewById(R.id.task_title_create_et);
        imageUrlEt = findViewById(R.id.task_image_url_create_et);
        deadlineEt = findViewById(R.id.task_deadline_create_et);
        descriptionEt = findViewById(R.id.task_description_create_et);
        cancelBtn = findViewById(R.id.cancel_create_btn);
        createBtn = findViewById(R.id.create_btn);
        // set onclick listener for buttons
        cancelBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            if (v.getId() == R.id.create_btn) {
                tryAddTask();
            }
            else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void tryAddTask() throws Exception {
        String title = titleEt.getText().toString(),
                deadline = deadlineEt.getText().toString(),
                imageUrl = imageUrlEt.getText().toString(),
                description = descriptionEt.getText().toString();
        if (title.trim().length() == 0) {
            throw new Exception("Task must have a title.");
        }
        if (deadline.trim().length() == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deadline = DateDifference.dateToString(LocalDateTime.now().plusHours(1));
            }
            else {
                throw new Exception("Task must have a deadline.");
            }
        }
        if (!DateDifference.isCorrectFormat(deadline)) {
            deadlineEt.setText("");
            throw new Exception("Deadline must be in \"dd/MM/YYYY hh:mm:ss\" format.");
        }
        createNewTask(title, deadline, imageUrl, description);
    }

    private void createNewTask(String title, String deadline, String imageUrl, String description) {
        Map<String, String> task = new HashMap<>();
        task.put("title", title);
        task.put("deadline", deadline);
        task.put("imageUrl", imageUrl);
        task.put("description", description);
        task.put("submissionDate", "");
        database.collection("tasks").add(task)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Task created successfully.", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "Error while creating a new task", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
    }
}