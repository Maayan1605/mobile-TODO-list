package com.example.todolistandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EditTaskActivity extends AppCompatActivity {
    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private String id, title, deadline, imageUrl, description, submissionDate;
    EditText titleEt, deadlineEt, imageUrlEt, descriptionEt;
    Button cancelButton, deleteButton, updateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Edit task");
        setContentView(R.layout.activity_edit_task);
        titleEt = findViewById(R.id.task_title_et);
        deadlineEt = findViewById(R.id.task_deadline_et);
        imageUrlEt = findViewById(R.id.task_image_url_et);
        descriptionEt = findViewById(R.id.task_description_et);
        cancelButton = findViewById(R.id.cancel_edit_btn);
        deleteButton = findViewById(R.id.delete_btn);
        updateButton = findViewById(R.id.update_btn);
        getExtraData();
        setEditTexts();
        cancelButton.setOnClickListener(view -> onBackPressed());
        deleteButton.setOnClickListener(view -> deleteTask());
        updateButton.setOnClickListener(view -> updateTask());

    }

    private void getExtraData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        deadline = intent.getStringExtra("deadline");
        imageUrl = intent.getStringExtra("imageUrl");
        submissionDate = intent.getStringExtra("subDate");
        description = intent.getStringExtra("description");
    }

    private void setEditTexts() {
        titleEt.setText(title);
        deadlineEt.setText(deadline);
        imageUrlEt.setText(imageUrl);
        descriptionEt.setText(description);
    }

    private void deleteTask() {
        database
                .collection("tasks")
                .document(id)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getApplicationContext(), "Task deleted successfully!", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), "Cold not delete task", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                });
    }

    private void updateTask() {
        try {
            checkFieldsValidity();
            Map<String, Object> task = new HashMap<>();
            task.put("title", title);
            task.put("deadline", deadline);
            task.put("imageUrl", imageUrl);
            task.put("description", description);
            database
                    .collection("tasks")
                    .document(id)
                    .update(task)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(
                                getApplicationContext(),
                                "Task updated successfully!",
                                Toast.LENGTH_LONG
                        ).show();
                        Intent intent = new Intent(getApplicationContext(), TaskActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("id", id);
                        intent.putExtra("title", title);
                        intent.putExtra("deadline", deadline);
                        intent.putExtra("imageUrl", imageUrl);
                        intent.putExtra("description", description);
                        intent.putExtra("subDate", submissionDate);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                getApplicationContext(),
                                "Failed to update task.",
                                Toast.LENGTH_LONG
                        ).show();
                        onBackPressed();
                    });
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void checkFieldsValidity() throws Exception {
        title = titleEt.getText().toString();
        deadline = deadlineEt.getText().toString();
        imageUrl = imageUrlEt.getText().toString();
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
    }
}