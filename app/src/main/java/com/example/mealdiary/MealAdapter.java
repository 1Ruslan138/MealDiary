package com.example.mealdiary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<Meal> mealList; // Список приемов пищи
    private OnMealClickListener onMealClickListener; // Интерфейс для обработки кликов по элементам

    // Конструктор для инициализации адаптера
    public MealAdapter(List<Meal> mealList, OnMealClickListener onMealClickListener) {
        this.mealList = mealList;
        this.onMealClickListener = onMealClickListener;
    }

    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создание нового элемента списка (ViewHolder)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position); // Получение текущего приема пищи
        holder.name.setText(meal.getName()); // Установка названия приема пищи
        holder.calories.setText("Калории: " + meal.getCalories()); // Установка количества калорий
        holder.time.setText("Время: " + meal.getTime()); // Установка времени приема пищи

        // Установка слушателя клика по элементу списка
        holder.itemView.setOnClickListener(v -> onMealClickListener.onMealClick(meal.getId()));
    }

    @Override
    public int getItemCount() {
        return mealList.size(); // Возвращение количества элементов в списке
    }

    // ViewHolder для представления каждого элемента списка
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView name, calories, time; // Текстовые поля для отображения данных о приеме пищи

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.meal_name); // Получение ссылки на текстовое поле для названия
            calories = itemView.findViewById(R.id.meal_calories); // Получение ссылки на текстовое поле для калорий
            time = itemView.findViewById(R.id.meal_time); // Получение ссылки на текстовое поле для времени
        }
    }

    // Интерфейс для обработки кликов по элементам
    public interface OnMealClickListener {
        void onMealClick(int mealId); // Метод, который будет вызываться при клике по элементу
    }
}
