package com.example.laba6genba;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewRemindersActivity extends AppCompatActivity {

    private ListView reminderListView;
    private List<Reminder> reminderList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ReminderDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reminders);

        reminderListView = findViewById(R.id.reminderListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        reminderListView.setAdapter(adapter);

        reminderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position);
            }
        });

        databaseHelper = new ReminderDatabaseHelper(this);
        loadReminders();
    }

    private void loadReminders() {
        reminderList.clear();
        adapter.clear();

        reminderList = databaseHelper.getAllReminders();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Reminder reminder : reminderList) {
            String reminderString =
                    "Заголовок: " + reminder.getTitle() +
                            "\nТекст: " + reminder.getText() +
                            "\nДата: " + dateFormat.format(reminder.getDate());

            adapter.add(reminderString);
        }
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы уверены, что хотите удалить это напоминание?")
                .setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReminder(position);
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteReminder(int position) {
        long id = reminderList.get(position).getId();
        databaseHelper.deleteReminder(id);
        reminderList.remove(position);
        adapter.remove(adapter.getItem(position));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReminders();
    }
}