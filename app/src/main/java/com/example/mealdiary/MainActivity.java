package com.example.mealdiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MealAdapter.OnMealClickListener {
    private RecyclerView recyclerViewMeals; // RecyclerView для отображения списка приемов пищи
    private MealAdapter mealAdapter; // Адаптер для управления отображением приемов пищи
    private List<Meal> mealList; // Список приемов пищи
    private DatabaseHelper dbHelper; // Помощник для работы с базой данных
    private JsonHelper jsonHelper; // Помощник для работы с JSON
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1; // Код запроса разрешения на уведомления

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Включение режима "от края до края"
        setContentView(R.layout.activity_main); // Установка макета активности

        // Установка слушателя для обработки отступов экрана
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom); // Установка отступов
            return insets;
        });

        dbHelper = new DatabaseHelper(this); // Инициализация DatabaseHelper
        jsonHelper = new JsonHelper(this); // Инициализация JsonHelper
        mealList = new ArrayList<>(); // Инициализация списка приемов пищи
        recyclerViewMeals = findViewById(R.id.recyclerViewMeals); // Получение ссылки на RecyclerView
        recyclerViewMeals.setLayoutManager(new LinearLayoutManager(this)); // Установка менеджера компоновки

        // Кнопка для добавления нового приема пищи
        Button buttonAddMeal = findViewById(R.id.buttonAddMeal);
        buttonAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditMealActivity.class); // Переход к AddEditMealActivity
            startActivity(intent);
        });

        // Кнопка для экспорта данных о приемах пищи в JSON
        Button buttonExportMeals = findViewById(R.id.buttonExportMeals);
        buttonExportMeals.setOnClickListener(v -> {
            jsonHelper.exportMealsToJson(); // Экспорт данных
            Toast.makeText(this, "Приемы пищи экспортированы", Toast.LENGTH_SHORT).show(); // Сообщение об успешном экспорте
        });

        // Кнопка для импорта данных о приемах пищи из JSON
        Button buttonImportMeals = findViewById(R.id.buttonImportMeals);
        buttonImportMeals.setOnClickListener(v -> {
            jsonHelper.importMealsFromJson(); // Импорт данных
            loadMeals(); // Обновляем список после импорта
            Toast.makeText(this, "Приемы пищи импортированы", Toast.LENGTH_SHORT).show(); // Сообщение об успешном импорте
        });

        // Запрос разрешения на отправку уведомлений
        requestNotificationPermission();

        loadMeals(); // Загрузка приемов пищи из базы данных
    }

    // Метод для запроса разрешения на уведомления
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Обработка результата запроса разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, можно отправлять уведомления
                Toast.makeText(this, "Разрешение на уведомления предоставлено", Toast.LENGTH_SHORT).show();
            } else {
                // Разрешение не предоставлено, уведомления не будут отправлены
                Toast.makeText(this, "Разрешение на уведомления не предоставлено", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для загрузки приемов пищи из базы данных
    private void loadMeals() {
        mealList.clear(); // Очистка списка приемов пищи
        mealList.addAll(dbHelper.getAllMeals()); // Получение всех приемов пищи из базы данных
        mealAdapter = new MealAdapter(mealList, this); // Инициализация адаптера с новым списком
        recyclerViewMeals.setAdapter(mealAdapter); // Установка адаптера для RecyclerView
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMeals(); // Перезагрузка списка приемов пищи при возвращении в активность
    }

    // Обработка клика по элементу списка приемов пищи
    @Override
    public void onMealClick(int mealId) {
        Intent intent = new Intent(MainActivity.this, AddEditMealActivity.class);
        intent.putExtra("mealId", mealId); // Передача ID приема пищи для редактирования
        startActivity(intent);
    }
}
