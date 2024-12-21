package com.example.mealdiary;

public class Meal {
    private int id; // Уникальный идентификатор приема пищи
    private String name; // Название приема пищи
    private int calories; // Количество калорий
    private String time; // Время приема пищи

    // Конструктор для создания объекта Meal
    public Meal(int id, String name, int calories, String time) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.time = time;
    }

    // Геттеры для доступа к полям
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public String getTime() {
        return time;
    }
}