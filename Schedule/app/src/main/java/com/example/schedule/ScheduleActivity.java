package com.example.schedule;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class ScheduleActivity extends AppCompatActivity {
    public static String[] masTable;

    ListView scheduleList;
    Button btnAdd;

    DataBaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursorMinTab;
    Cursor userCursor;

    MainActivity mainActivity;

    public static int positionSelectedItem;
    public static String globalIdSelectedItem;
    public static String dataBtn = "";
    public static String[] masOffice;
    public static int[] masIdSelectedDiscipline;
    public static int[] masIdSelectedType;
    String[] masIdGlobalItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mainActivity = new MainActivity();

        btnAdd = findViewById(R.id.btnAdd);
        scheduleList = findViewById(R.id.scheduleList);
        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionSelectedItem = position;
                globalIdSelectedItem = masIdGlobalItems[position];

                Intent intent = new Intent(ScheduleActivity.this, ScheduleDetailsActivity.class);
                startActivity(intent);
            }
        });

        databaseHelper = new DataBaseHelper(getApplicationContext());
        databaseHelper.create_db(); //Создаем базу данных
    }

    public void schedule1Output(View view) { //Показ расписания группы на 1.12
        dataBtn = "1.12";
        btnAdd.setVisibility(View.VISIBLE); //btnAdd.setVisibility(View.INVISIBLE);

        super.onResume(); //Открываем подключение
        db = databaseHelper.open();

        userCursor =  db.rawQuery("select id_time, id_discipline, id_type, Office, id_class from \"Class\" where Data = 1.12 and id_group = "
                + mainActivity.getIdSelectedGroup(), null); //Получаем данные из БД в виде курсора
        String[] arrayList = new String[userCursor.getCount()];
        userCursor.moveToFirst();
        masOffice = new String[userCursor.getCount()];
        masIdSelectedDiscipline = new int[userCursor.getCount()];
        masIdSelectedType = new int[userCursor.getCount()];
        masIdGlobalItems = new String[userCursor.getCount()];

        for(int i = 0; i < arrayList.length; i++){ //Переписывваем данные из таблицы в массив
            masOffice[i] = userCursor.getString(3);
            masIdSelectedDiscipline[i] = Integer.parseInt(userCursor.getString(1)) - 1;
            masIdSelectedType[i] = Integer.parseInt(userCursor.getString(2)) - 1;
            masIdGlobalItems[i] = userCursor.getString(4);

            arrayList[i] = " - " + ConvertIdToValue("select * from \"Time_class\"", userCursor.getString(0))
                    + "\n" + ConvertIdToValue("select * from \"Discipline\"", userCursor.getString(1))
                    + "\n" + ConvertIdToValue("select * from \"Type_class\"", userCursor.getString(2))
                    + ", " + userCursor.getString(3) + "\n";
            userCursor.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList); //Создаем адаптер
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        scheduleList.setAdapter(adapter);
    }

    public void schedule2Output(View view) { //Показ расписания группы на 2.12
        dataBtn = "2.12";
        btnAdd.setVisibility(View.VISIBLE);

        super.onResume(); //Открываем подключение
        db = databaseHelper.open();

        userCursor = db.rawQuery("select id_time, id_discipline, id_type, Office, id_class from \"Class\" where Data = 2.12 and id_group = "
                + mainActivity.getIdSelectedGroup(), null); //Получаем данные из БД в виде курсора
        String[] arrayList = new String[userCursor.getCount()];
        userCursor.moveToFirst();
        masOffice = new String[userCursor.getCount()];
        masIdSelectedDiscipline = new int[userCursor.getCount()];
        masIdSelectedType = new int[userCursor.getCount()];
        masIdGlobalItems = new String[userCursor.getCount()];

        for (int i = 0; i < arrayList.length; i++) { //Переписывваем данные из таблицы в массив
            masOffice[i] = userCursor.getString(3);
            masIdSelectedDiscipline[i] = Integer.parseInt(userCursor.getString(1)) - 1;
            masIdSelectedType[i] = Integer.parseInt(userCursor.getString(2)) - 1;
            masIdGlobalItems[i] = userCursor.getString(4);

            arrayList[i] = " - " + ConvertIdToValue("select * from \"Time_class\"", userCursor.getString(0))
                    + "\n" + ConvertIdToValue("select * from \"Discipline\"", userCursor.getString(1))
                    + "\n" + ConvertIdToValue("select * from \"Type_class\"", userCursor.getString(2))
                    + ", " + userCursor.getString(3) + "\n";
            userCursor.moveToNext();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList); //Создаем адаптер
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        scheduleList.setAdapter(adapter); //Устанавливаем адаптер в spinner
    }

    public String ConvertIdToValue(String sql, String id){
        userCursorMinTab =  db.rawQuery(sql, null); //Получаем данные из БД в виде курсора
        masTable = new String[userCursorMinTab.getCount() * 2];
        userCursorMinTab.moveToFirst();
        for(int i = 0; i < masTable.length; i++){ //Переписывваем данные из таблицы в массив
            masTable[i] = userCursorMinTab.getString(0); //id группы
            i++;
            masTable[i] = userCursorMinTab.getString(1); //Название группы
            userCursorMinTab.moveToNext();
        }

        for(int i = 0; i < masTable.length; i++){
            if(Objects.equals(masTable[i], id)) { return masTable[i + 1]; }
            i++;
        }
        return "";
    }

    public void ClickAddSchedule(View view) {
        Intent intent = new Intent(ScheduleActivity.this, AddScheduleActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); //Закрываем подключение и курсор
        db.close();
        userCursor.close();
        userCursorMinTab.close();
    }
}