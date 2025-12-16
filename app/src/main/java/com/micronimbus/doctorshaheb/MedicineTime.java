package com.micronimbus.doctorshaheb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.List;

public class MedicineTime extends AppCompatActivity {

    private AlarmDatabaseHelper dbHelper;
    private LinearLayout alarmContainer;
    private EditText etMedicineName;
    private Button btnPickTime, btnAddAlarm;
    private TextView tvSelectedTime;

    private int selectedHour = -1;
    private int selectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_time);
        ImageButton backBtn = findViewById(R.id.back);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // optional (removes current activity from stack)
        });

        dbHelper = new AlarmDatabaseHelper(this);

        etMedicineName = findViewById(R.id.etMedicineName);
        btnPickTime = findViewById(R.id.btnPickTime);
        btnAddAlarm = findViewById(R.id.btnAddAlarm);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        alarmContainer = findViewById(R.id.alarmContainer);

        btnPickTime.setOnClickListener(v -> showTimePickerDialog());

        btnAddAlarm.setOnClickListener(v -> {
            String medicineName = etMedicineName.getText().toString().trim();
            if (medicineName.isEmpty()) {
                Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedHour == -1 || selectedMinute == -1) {
                Toast.makeText(this, "Pick a time first", Toast.LENGTH_SHORT).show();
                return;
            }

            long id = dbHelper.addAlarm(new MedicineAlarm(0, medicineName, selectedHour, selectedMinute));
            setAlarm((int) id, medicineName, selectedHour, selectedMinute);

            Toast.makeText(this, "Alarm Added", Toast.LENGTH_SHORT).show();

            etMedicineName.setText("");
            tvSelectedTime.setText("No time selected");
            selectedHour = -1;
            selectedMinute = -1;

            loadAlarms();
        });

        loadAlarms();
        requestIgnoreBatteryOptimizations();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minute1;
                    tvSelectedTime.setText(String.format("Selected time: %02d:%02d", selectedHour, selectedMinute));
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void loadAlarms() {
        alarmContainer.removeAllViews();
        List<MedicineAlarm> alarms = dbHelper.getAllAlarms();
        for (MedicineAlarm alarm : alarms) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView text = new TextView(this);
            text.setText(alarm.medicineName + " at " + String.format("%02d:%02d", alarm.hour, alarm.minute));
            text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

            Button edit = new Button(this);
            edit.setText("Edit");
            edit.setOnClickListener(v -> openEditDialog(alarm));

            Button delete = new Button(this);
            delete.setText("Delete");
            delete.setOnClickListener(v -> {
                cancelAlarm(alarm.id);
                dbHelper.deleteAlarm(alarm.id);
                Toast.makeText(this, "Alarm Deleted", Toast.LENGTH_SHORT).show();
                loadAlarms();
            });

            row.addView(text);
            row.addView(edit);
            row.addView(delete);

            alarmContainer.addView(row);
        }
    }

    private void openEditDialog(MedicineAlarm alarm) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    alarm.hour = hourOfDay;
                    alarm.minute = minute;
                    dbHelper.updateAlarm(alarm);
                    setAlarm(alarm.id, alarm.medicineName, hourOfDay, minute);
                    Toast.makeText(this, "Alarm Updated", Toast.LENGTH_SHORT).show();
                    loadAlarms();
                }, alarm.hour, alarm.minute, true);
        timePickerDialog.show();
    }

    private void setAlarm(int id, String medicineName, int hour, int minute) {
        Intent intent = new Intent(this, MyBroadReciver.class);
        intent.putExtra("medicineName", medicineName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm(int id) {
        Intent intent = new Intent(this, MyBroadReciver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    private void requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            String packageName = getPackageName();
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            }
        }
    }

}
