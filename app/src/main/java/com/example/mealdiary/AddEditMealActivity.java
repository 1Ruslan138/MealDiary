package com.example.mealdiary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AddEditMealActivity extends AppCompatActivity {
    private EditText editTextName, editTextCalories; // Поля для ввода названия и калорий
    private TextView textViewSelectedTime; // Поле для отображения выбранного времени
    private Button buttonSave, buttonSelectTime; // Кнопки для сохранения и выбора времени
    private DatabaseHelper dbHelper; // Помощник для работы с базой данных
    private int mealId = -1; // ID приема пищи (-1 означает, что мы добавляем новую запись)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_meal); // Установка макета активности

        // Инициализация элементов интерфейса
        editTextName = findViewById(R.id.editTextName);
        editTextCalories = findViewById(R.id.editTextCalories);
        textViewSelectedTime = findViewById(R.id.textViewSelectedTime);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSelectTime = findViewById(R.id.buttonSelectTime);
        dbHelper = new DatabaseHelper(this); // Инициализация DatabaseHelper

        Intent intent = getIntent(); // Получение Intent
        if (intent.hasExtra("mealId")) {
            mealId = intent.getIntExtra("mealId", -1); // Получение ID приема пищи для редактирования
            loadMealData(mealId); // Загрузка данных приема пищи
        }

        // Установка слушателя для выбора времени приема пищи
        buttonSelectTime.setOnClickListener(v -> showTimePickerDialog());

        // Установка слушателя для сохранения данных о приеме пищи
        buttonSave.setOnClickListener(v -> saveMeal());
    }

    // Метод для отображения диалогового окна выбора времени
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance(); // Получение текущего времени
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // Час
        int minute = calendar.get(Calendar.MINUTE); // Минута

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            String time = String.format("%02d:%02d", selectedHour, selectedMinute); // Форматирование времени
            textViewSelectedTime.setText(time); // Отображение выбранного времени
        }, hour, minute, true);
        timePickerDialog.show(); // Показ диалогового окна
    }

    // Метод для загрузки данных о приеме пищи для редактирования
    private void loadMealData(int mealId) {
        Meal meal = dbHelper.getMeal(mealId); // Получение приема пищи из базы данных
        if (meal != null) {
            editTextName.setText(meal.getName()); // Установка названия
            editTextCalories.setText(String.valueOf(meal.getCalories())); // Установка калорий
            textViewSelectedTime.setText(meal.getTime()); // Установка времени
        }
    }

    // Метод для сохранения данных о приеме пищи
    private void saveMeal() {
        String name = editTextName.getText().toString().trim(); // Получение названия
        String caloriesStr = editTextCalories.getText().toString().trim(); // Получение калорий
        String time = textViewSelectedTime.getText().toString(); // Получение времени

        // Проверка на заполненность всех полей
        if (name.isEmpty() || caloriesStr.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show(); // Уведомление о пустых полях
            return;
        }

        int calories = Integer.parseInt(caloriesStr); // Преобразование калорий в целое число

        if (mealId == -1) {
            // Добавление нового приема пищи
            Meal newMeal = new Meal(0, name, calories, time); // Создание нового объекта Meal
            dbHelper.addMeal(newMeal); // Сохранение в базе данных
            scheduleNotification(time, name); // Запланировать уведомление
            Toast.makeText(this, "Прием пищи добавлен", Toast.LENGTH_SHORT).show(); // Уведомление об успешном добавлении
        } else {
            // Обновление существующего приема пищи
            Meal updatedMeal = new Meal(mealId, name, calories, time); // Создание объекта Meal для обновления
            dbHelper.updateMeal(updatedMeal); // Обновление в базе данных
            Toast.makeText(this, "Прием пищи обновлен", Toast.LENGTH_SHORT).show(); // Уведомление об успешном обновлении
        }
        finish(); // Закрытие активности
    }

    // Метод для планирования уведомления
    private void scheduleNotification(String time, String mealName) {
        // Разделение времени на часы и минуты
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);

        Calendar calendar = Calendar.getInstance(); // Получение текущего времени
        calendar.set(Calendar.HOUR_OF_DAY, hour); // Установка часа
        calendar.set(Calendar.MINUTE, minute); // Установка минуты
        calendar.set(Calendar.SECOND, 0); // Установка секунд
        calendar.set(Calendar.MILLISECOND, 0); // Установка миллисекунд

        // Создание Intent для запуска MealReminderService
        Intent intent = new Intent(this, MealReminderService.class);
        intent.putExtra("mealName", mealName); // Передача имени приема пищи
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT); // Создание PendingIntent

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); // Получение AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); // Установка будильника
    }
}