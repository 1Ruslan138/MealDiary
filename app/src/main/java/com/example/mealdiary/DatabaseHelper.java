package com.example.mealdiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "meals.db"; // Имя базы данных
    private static final int DATABASE_VERSION = 1; // Версия базы данных
    private static final String TABLE_MEALS = "meals"; // Имя таблицы для хранения приемов пищи

    // Конструктор для инициализации DatabaseHelper
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // Вызов конструктора родительского класса
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL-запрос для создания таблицы приемов пищи
        String CREATE_MEALS_TABLE = "CREATE TABLE " + TABLE_MEALS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, calories INTEGER, time TEXT)";
        db.execSQL(CREATE_MEALS_TABLE); // Выполнение SQL-запроса
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEALS); // Удаление старой таблицы, если она существует
        onCreate(db); // Создание новой таблицы
    }

    // Метод для добавления нового приема пищи в базу данных
    public void addMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase(); // Получение объекта базы данных для записи
        ContentValues values = new ContentValues(); // Создание объекта для хранения значений
        values.put("name", meal.getName()); // Добавление названия
        values.put("calories", meal.getCalories()); // Добавление калорий
        values.put("time", meal.getTime()); // Добавление времени
        db.insert(TABLE_MEALS, null, values); // Вставка данных в таблицу
        db.close(); // Закрытие базы данных
    }

    // Метод для обновления существующего приема пищи
    public void updateMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase(); // Получение объекта базы данных для записи
        ContentValues values = new ContentValues(); // Создание объекта для хранения значений
        values.put("name", meal.getName()); // Обновление названия
        values.put("calories", meal.getCalories()); // Обновление калорий
        values.put("time", meal.getTime()); // Обновление времени

        db.update("meals", values, "id = ?", new String[]{String.valueOf(meal.getId())}); // Обновление записи в таблице
    }

    // Метод для получения всех приемов пищи из базы данных
    public List<Meal> getAllMeals() {
        List<Meal> mealList = new ArrayList<>(); // Список для хранения приемов пищи
        SQLiteDatabase db = this.getReadableDatabase(); // Получение объекта базы данных для чтения
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_MEALS, null); // Выполнение SQL-запроса для получения всех записей

        if (cursor.moveToFirst()) { // Проверка, есть ли записи
            do {
                // Создание объекта Meal из данных курсора
                Meal meal = new Meal(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3)
                );
                mealList.add(meal); // Добавление объекта Meal в список
            } while (cursor.moveToNext()); // Переход к следующей записи
        }
        cursor.close(); // Закрытие курсора
        db.close(); // Закрытие базы данных
        return mealList; // Возвращение списка приемов пищи
    }

    // Метод для получения конкретного приема пищи по ID
    public Meal getMeal(int id) {
        SQLiteDatabase db = this.getReadableDatabase(); // Получение объекта базы данных для чтения
        Cursor cursor = db.query("meals", new String[]{"id", "name", "calories", "time"},
                "id=?", new String[]{String.valueOf(id)}, null, null, null); // Выполнение запроса для получения записи по ID

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Получение индексов столбцов
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int caloriesIndex = cursor.getColumnIndex("calories");
                int timeIndex = cursor.getColumnIndex("time");

                // Проверка на валидность индексов
                if (idIndex != -1 && nameIndex != -1 && caloriesIndex != -1 && timeIndex != -1) {
                    // Создание объекта Meal из данных курсора
                    Meal meal = new Meal(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getInt(caloriesIndex),
                            cursor.getString(timeIndex)
                    );
                    cursor.close(); // Закрытие курсора
                    return meal; // Возвращение объекта Meal
                } else {
                    // Логирование ошибки, если один из столбцов не найден
                    Log.e("DatabaseHelper", "Один или несколько столбцов не найдены");
                }
            }
            cursor.close(); // Закрытие курсора
        }
        return null; // Если ничего не найдено, возвращаем null
    }
}
