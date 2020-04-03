package fi.example.fancynotes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {
    String CHANNEL_ID;

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        Log.d("NOTIFIKAATIO", "worker");
    }

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        triggerNotification();

        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }

    public void triggerNotification() {
        Log.d("NOTIFIKAATIO", "notif2");
//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
//            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//            mBuilder.setContentTitle("It\\'s time!");
//            mBuilder.setContentText("It\\'s time! Check your note.");
//
//            Notification notification = mBuilder.build();
//
//            NotificationManager mNotificationManager =
//                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//            int mId = 1;
//            mNotificationManager.notify(mId, notification);

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