package com.example.todolistandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<TaskModel> {
    public TaskAdapter(@NonNull Context context, ArrayList<TaskModel> tasks) {
        super(context, R.layout.task_list_item, tasks);
    }
    private class ViewHolder {
        ImageView taskImage;
        TextView titleLabel, deadlineLabel, subDateLabel;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TaskModel taskModel = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.task_list_item, parent, false);
            viewHolder.taskImage = convertView.findViewById(R.id.task_item_image);
            viewHolder.titleLabel = convertView.findViewById(R.id.task_item_title_lbl);
            viewHolder.deadlineLabel = convertView.findViewById(R.id.task_item_deadline_lbl);
            viewHolder.subDateLabel = convertView.findViewById(R.id.task_item_sub_date_lbl);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.titleLabel.setText(taskModel.getTitle());
        viewHolder.deadlineLabel.setText("Deadline: " + taskModel.getDeadline());
        if (DateDifference.isCorrectFormat(taskModel.getSubmissionDate())) {
            viewHolder.subDateLabel.setText("Submitted at: " + taskModel.getSubmissionDate());
        }
        else {
            viewHolder.subDateLabel.setText("Task is not submitted yet");
        }
        try {
            Picasso
                    .get()
                    .load(taskModel.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(viewHolder.taskImage);
        }
        catch (Exception e) {
            viewHolder.taskImage.setImageResource(R.drawable.placeholder);
        }
        return convertView;
    }
}
