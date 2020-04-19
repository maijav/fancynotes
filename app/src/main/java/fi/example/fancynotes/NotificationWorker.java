package fi.example.fancynotes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Worker for triggering notification on selected point of time.
 *
 * @author  Maija Visala
 * @version 3.0
 * @since   2020-03-09
 */
public class NotificationWorker extends Worker {
    String CHANNEL_ID;

    /**
     * Constructor for NotificationWorker.
     * @param context application context.
     * @param params parameters that enable modification of the worker.
     */
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    /**
     * Called when worker is ready to trigger desired task.
     * @return Result tells WorkManager if task should be done again later or not.
     */
    @NonNull
    @Override
    public Result doWork() {
        triggerNotification();
        return Result.success();
    }

    /**
     * Build and trigger a notification. Notification texts are specified in strings.xml.
     */
    public void triggerNotification() {
        CHANNEL_ID = "Notification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getResources().getString(R.string.channel_name);
            String description = getApplicationContext().getResources().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(
                    getApplicationContext(), CHANNEL_ID);
            builder.setContentTitle(getApplicationContext().getResources().getString(R.string.channel_name))
                    .setSmallIcon(R.mipmap.ic_launcher);

            notificationManager.notify(1, builder.build());
        }
    }
}