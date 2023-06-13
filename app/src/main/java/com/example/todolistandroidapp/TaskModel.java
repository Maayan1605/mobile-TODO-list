package com.example.todolistandroidapp;

import java.io.Serializable;

public class TaskModel implements Serializable {
    private String id, title, deadline, imageUrl, description, submissionDate;

    public TaskModel(String id, String title, String deadline, String imageUrl, String description, String submissionDate) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.imageUrl = imageUrl;
        this.description = description;
        this.submissionDate = submissionDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }
}
