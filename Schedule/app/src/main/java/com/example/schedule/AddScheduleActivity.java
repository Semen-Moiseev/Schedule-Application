package com.example.schedule;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddScheduleActivity extends AppCompatActivity {
    Spinner timeSpinner;
    Spinner tittleSpinner;
    Spinner typeSpinner;
    EditText editTextOffice;

    DataBaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;

    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainActivity = new MainActivity();

        timeSpinner = findViewById(R.id.spinTime);
        tittleSpinner = findViewById(R.id.spinTittle);
        typeSpinner = findViewById(R.id.spinType);
        editTextOffice = findViewById(R.id.editTextOffice);

        databaseHelper = new DataBaseHelper(getApplicationContext());
        databaseHelper.create_db(); //Создаем базу данных

        GetScheduleData("select * from \"Time_class\"", timeSpinner); //Запись данные в spinner и editText из БД
        GetScheduleData("select * from \"Discipline\"", tittleSpinner);
        GetScheduleData("select * from \"Type_class\"", typeSpinner);
    }

    public void GetScheduleData(String sql, Spinner spinner) {
        super.onResume(); //Открываем подключение
        db = databaseHelper.open();

        userCursor =  db.rawQuery(sql, null); //Получаем данные из БД в виде курсора
        String[] arraySpinner = new String[userCursor.getCount()];
        userCursor.moveToFirst();
        for(int i = 0; i < arraySpinner.length; i++){ //Переписывваем данные из таблицы в массив
            arraySpinner[i] = userCursor.getString(1); //Название группы
            userCursor.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner); //Создаем адаптер
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter); //Устанавливаем адаптер в spinner
    }

    public void Add(View view) {
        ContentValues cv = new ContentValues();
        cv.put("Data", ScheduleActivity.dataBtn);
        cv.put("Office", editTextOffice.getText().toString());
        cv.put("Note", "");
        cv.put("id_time", tittleSpinner.getSelectedItemId() + 1);
        cv.put("id_type", typeSpinner.getSelectedItemId() + 1);
        cv.put("id_group", mainActivity.getIdSelectedGroup());
        cv.put("id_discipline", tittleSpinner.getSelectedItemId() + 1);

        db.insert("Class", null, cv);
        db.close();
        Intent intent = new Intent(this, ScheduleActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); //Закрываем подключение и курсор
        db.close();
        userCursor.close();
    }
}