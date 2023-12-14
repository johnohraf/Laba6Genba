package com.example.laba6genba;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText textEditText;
    private Button dateButton;
    private Button timeButton;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        titleEditText = findViewById(R.id.titleEditText);
        textEditText = findViewById(R.id.textEditText);
        dateButton = findViewById(R.id.dateButton);
        timeButton = findViewById(R.id.timeButton);

        calendar = Calendar.getInstance();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                calendar.set(year, month, dayOfMonth);
                                dateButton.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(AddReminderActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                timeButton.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReminder();
            }
        });
    }

    private void saveReminder() {
        String title = titleEditText.getText().toString();
        String text = textEditText.getText().toString();

        if (title.isEmpty() || text.isEmpty()) {
            Toast.makeText(this, "Пожалуйста впишите заголовок и текст", Toast.LENGTH_SHORT).show();
            return;
        }

        ReminderDatabaseHelper databaseHelper = new ReminderDatabaseHelper(this);
        Reminder reminder = new Reminder(title, text, calendar.getTime());

        long rowId = databaseHelper.insertReminder(reminder);

        if (rowId != -1) {
            Toast.makeText(this, "Напоминание успешно создано", Toast.LENGTH_SHORT).show();

            // Добавляем уведомление
            NotificationHelper.sendNotification(this, title, text);

            finish();
        } else {
            Toast.makeText(this, "Ошибка сохранения напоминания", Toast.LENGTH_SHORT).show();
        }
    }
}