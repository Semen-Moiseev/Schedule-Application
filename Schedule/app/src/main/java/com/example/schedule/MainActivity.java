package com.example.schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static String[] masTable;
    public static String selectedGroup  = "";

    Spinner groupSpinner;
    DataBaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursorGroup;

    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        groupSpinner = findViewById(R.id.spinner);

        databaseHelper = new DataBaseHelper(getApplicationContext());
        databaseHelper.create_db(); //Создаем базу данных
    }

    @Override
    public void onResume() {
        super.onResume(); //Открываем подключение
        db = databaseHelper.open();

        userCursorGroup =  db.rawQuery("select * from \"Group\"", null); //Получаем данные из БД в виде курсора
        String[] arraySpinner = new String[userCursorGroup.getCount()];
        masTable = new String[userCursorGroup.getCount() * 2];
        userCursorGroup.moveToFirst();
        int j = 0;
        for(int i = 0; i < arraySpinner.length; i++){ //Переписывваем данные из таблицы в массив
            masTable[j] = userCursorGroup.getString(0); //id группы
            j++;
            arraySpinner[i] = masTable[j] = userCursorGroup.getString(1); //Название группы
            j++;
            userCursorGroup.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arraySpinner); //Создаем адаптер
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter); //Устанавливаем адаптер в spinner

        builder = new AlertDialog.Builder(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); //Закрываем подключение и курсор
        db.close();
        userCursorGroup.close();
    }

    public void groupSelection(View view) { //Выбор группы по кнопке "Выбрать"
        builder.setTitle("Выбор группы")
                .setMessage("Вы действительно хотите выбрать группу " + groupSpinner.getSelectedItem().toString() + "?")
                .setCancelable(true)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //Переход на страницу с расписанием этой группы
                        selectedGroup = groupSpinner.getSelectedItem().toString();
                        Intent intent = new Intent(MainActivity.this, ScheduleActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedGroup = "";
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    public void exitTheApplication(View view) { //Выход из приложения по кнопке "Выход"
        builder.setTitle("Выход из приложения")
                .setMessage("Вы действительно хотите выйти из приложения?")
                .setCancelable(true)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() { //Выход из приложения
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

    public String getIdSelectedGroup() {
        for(int i = 1; i < masTable.length; i++){
            if(Objects.equals(masTable[i], selectedGroup)) { return masTable[i - 1]; }
            i++;
        }
        return "";
    }
}