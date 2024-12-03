package com.example.schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Spinner groupSpinner;
    DataBaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

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

        groupSpinner = (Spinner) findViewById(R.id.spinner);

        databaseHelper = new DataBaseHelper(getApplicationContext());
        databaseHelper.create_db(); //Создаем базу данных
    }

    @Override
    public void onResume() {
        super.onResume(); //Открываем подключение
        db = databaseHelper.open();

        userCursor =  db.rawQuery("select Tittle_group from \"Group\"", null); //Получаем данные из БД в виде курсора
        String[] arraySpinner = new String[userCursor.getCount()]; //Массив заполнения spinner
        userCursor.moveToFirst();
        for(int i = 0; i < arraySpinner.length; i++){ //Переписывваем данные из таблицы в массив
            arraySpinner[i] = userCursor.getString(0);
            userCursor.moveToNext();
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
        userCursor.close();
    }

    public void groupSelection(View view) { //Выбор группы по кнопке "Выбрать"
        builder.setTitle("Выбор группы")
                .setMessage("Вы действительно хотите выбрать группу " + groupSpinner.getSelectedItem().toString() + "?")
                .setCancelable(true)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) { //Переход на страницу с расписанием этой группы

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
}