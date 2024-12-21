package com.example.mealdiary;

import android.content.Context;
import android.util.Log;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class JsonHelper {
    private static final String FILE_NAME = "meals.json"; // Имя файла для сохранения данных
    private final Moshi moshi; // Библиотека для работы с JSON
    private final DatabaseHelper dbHelper; // Помощник для работы с базой данных
    private final Context context; // Контекст приложения

    // Конструктор для инициализации JsonHelper
    public JsonHelper(Context context) {
        this.context = context; // Сохранение контекста
        this.moshi = new Moshi.Builder().build(); // Инициализация Moshi
        this.dbHelper = new DatabaseHelper(context); // Инициализация DatabaseHelper
    }

    // Метод для экспорта данных о приемах пищи в JSON
    public void exportMealsToJson() {
        List<Meal> meals = dbHelper.getAllMeals(); // Получение всех приемов пищи из базы данных
        String json = moshi.adapter(Types.newParameterizedType(List.class, Meal.class)).toJson(meals); // Преобразование списка в JSON

        try {
            File file = new File(context.getExternalFilesDir(null), FILE_NAME); // Определение файла для сохранения
            BufferedWriter writer = new BufferedWriter(new FileWriter(file)); // Создание BufferedWriter для записи
            writer.write(json); // Запись JSON в файл
            writer.close(); // Закрытие потока записи
        } catch (IOException e) {
            Log.e("JsonHelper", "Ошибка при экспорте данных в JSON", e); // Логирование ошибки
        }
    }

    // Метод для импорта данных о приемах пищи из JSON
    public void importMealsFromJson() {
        try {
            File file = new File(context.getExternalFilesDir(null), FILE_NAME); // Определение файла для чтения
            BufferedReader reader = new BufferedReader(new FileReader(file)); // Создание BufferedReader для чтения
            StringBuilder jsonBuilder = new StringBuilder(); // Строковый строитель для хранения JSON
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line); // Чтение строки и добавление к JSON
            }
            reader.close(); // Закрытие потока чтения

            // Приведение результата к типу List<Meal>
            List<Meal> meals = (List<Meal>) moshi.adapter(Types.newParameterizedType(List.class, Meal.class)).fromJson(jsonBuilder.toString());
            if (meals != null) {
                for (Meal meal : meals) {
                    dbHelper.addMeal(meal); // Добавление каждого приема пищи в базу данных
                }
            }
        } catch (IOException e) {
            Log.e("JsonHelper", "Ошибка при импорте данных из JSON", e); // Логирование ошибки
        }
    }
}