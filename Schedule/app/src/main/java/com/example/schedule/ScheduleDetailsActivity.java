package com.example.schedule;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ScheduleDetailsActivity extends AppCompatActivity {
    Spinner tittleSpinner;
    Spinner typeSpinner;
    Spinner officeSpinner;
    DataBaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    public static String[] masTable;

    ScheduleActivity scheduleActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        scheduleActivity = new ScheduleActivity();

        tittleSpinner = findViewById(R.id.spinTittle);
        typeSpinner = findViewById(R.id.spinType);
        officeSpinner = findViewById(R.id.spinOffice);

        databaseHelper = new DataBaseHelper(getApplicationContext());
        databaseHelper.create_db(); //Создаем базу данных
    }

    @Override
    public void onResume() {
        super.onResume(); //Открываем подключение
        db = databaseHelper.open();

        userCursor =  db.rawQuery("select * from \"Discipline\"", null); //Получаем данные из БД в виде курсора
        String[] arraySpinner = new String[userCursor.getCount()];
        masTable = new String[userCursor.getCount() * 2];
        userCursor.moveToFirst();
        int j = 0;
        for(int i = 0; i < arraySpinner.length; i++){ //Переписывваем данные из таблицы в массив
            masTable[j] = userCursor.getString(0); //id группы
            j++;
            arraySpinner[i] = masTable[j] = userCursor.getString(1); //Название группы
            j++;
            userCursor.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner); //Создаем адаптер
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tittleSpinner.setAdapter(adapter); //Устанавливаем адаптер в spinner
        //tittleSpinner.setSelection(); В скобках поставить id из списка, который надо выбрать в спиннере
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); //Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }
}