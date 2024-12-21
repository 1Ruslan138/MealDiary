package com.example.mealdiary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MealReminderService extends Service {

    private static final String CHANNEL_ID = "meal_reminder_channel"; // Идентификатор канала уведомлений

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(); // Создание канала уведомлений при старте сервиса
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String mealName = intent.getStringExtra("mealName"); // Получение имени приема пищи из Intent
        showNotification(mealName); // Отображение уведомления
        return START_NOT_STICKY; // Сервис не будет перезапущен, если система завершит его
    }

    // Метод для отображения уведомления
    private void showNotification(String mealName) {
        Intent notificationIntent = new Intent(this, MainActivity.class); // Переход к MainActivity при нажатии на уведомление
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Создание уведомления
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Напоминание о приеме пищи") // Заголовок уведомления
                .setContentText("Не забудьте про " + mealName + "!") // Текст уведомления
                .setSmallIcon(R.drawable.icon) // Значок уведомления
                .setContentIntent(pendingIntent) // Установка PendingIntent
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Установка приоритета уведомления
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification); // Отправка уведомления
    }

    // Метод для создания канала уведомлений (требуется для Android 8.0 и выше)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Meal Reminder Channel", // Название канала
                    NotificationManager.IMPORTANCE_HIGH // Важность уведомлений
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel); // Создание канала
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Сервис не привязывается к другим компонентам
    }
}
