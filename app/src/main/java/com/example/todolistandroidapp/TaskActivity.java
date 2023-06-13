package com.example.todolistandroidapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class TaskActivity extends AppCompatActivity {
    private static final Picasso picasso = Picasso.get();
    private static final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private String id, title, deadline, imageUrl, description, submissionDate, status;
    private ImageView taskImage;
    private TextView titleLabel, deadlineLabel, subDateLabel, statusLabel, descriptionLabel;
    private Button submitButton, editButton, goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("View task");
        setContentView(R.layout.activity_task);
        titleLabel = findViewById(R.id.task_title_lbl);
        deadlineLabel = findViewById(R.id.task_deadline_lbl);
        subDateLabel = findViewById(R.id.task_submission_date_lbl);
        statusLabel = findViewById(R.id.task_status_lbl);
        descriptionLabel = findViewById(R.id.task_description_lbl);
        taskImage = findViewById(R.id.task_image);
        submitButton = findViewById(R.id.task_submit_btn);
        editButton = findViewById(R.id.task_go_to_edit_btn);
        goBackButton = findViewById(R.id.task_go_back_btn);
        submitButton.setOnClickListener(view -> {
            if (DateDifference.isCorrectFormat(submissionDate))
                    submissionDate = "";
            else
                submissionDate = DateDifference.nowToString();
            updateTaskSubmission();
        });
        goBackButton.setOnClickListener(view -> onBackPressed());
        editButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), EditTaskActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("title", title);
            intent.putExtra("deadline", deadline);
            intent.putExtra("imageUrl", imageUrl);
            intent.putExtra("description", description);
            intent.putExtra("subDate", submissionDate);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStrings();
        try {
            picasso
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(taskImage);
        }
        catch (Exception e) { // in case of empty image url
            taskImage.setImageResource(R.drawable.placeholder);
        }
        titleLabel.setText(title);
        deadlineLabel.setText("Deadline: " + deadline);
        descriptionLabel.setText(description);
        updateSubmissionComponents();
    }

    private void updateStrings() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");
        deadline = intent.getStringExtra("deadline");
        submissionDate = intent.getStringExtra("subDate");
        imageUrl = intent.getStringExtra("imageUrl");
        description = intent.getStringExtra("description");
        updateStatus();
    }

    private void updateStatus() {
        if (DateDifference.isCorrectFormat(submissionDate)) {
            DateDifference dateDifference = DateDifference.between(submissionDate, deadline);
            status = dateDifference.toString(
                    "Task was submitted \0 before deadline",
                    "Task was submitted \0 late"
            );
        }
        else {
            DateDifference dateDifference = DateDifference.fromNow(deadline);
            status = dateDifference.toString(
                    "Time left: \0",
                    "Time over \0 ago"
            );
        }
    }

    private void updateTaskSubmission() {
        try {
            DocumentReference documentReference = database.collection("tasks").document(id);
            documentReference
                    .update("submissionDate", submissionDate)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Task updated successfully!", Toast.LENGTH_LONG).show();
                            updateSubmissionComponents();
                        } else {
                            Toast.makeText(getApplicationContext(), "Could not update task.", Toast.LENGTH_LONG).show();
                            onBackPressed(); // go back to home page
                        }
                    });
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateSubmissionComponents() {
        if (DateDifference.isCorrectFormat(submissionDate)) {
            subDateLabel.setVisibility(View.VISIBLE);
        subDateLabel.setText("Submitted at: " + submissionDate);
        submitButton.setText("Cancel submission");
        }
        else {
            subDateLabel.setVisibility(View.INVISIBLE);
            submitButton.setText("Submit");
        }
        updateStatus();
        statusLabel.setText(status);
    }
}