package net.lucas.download_service.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import net.lucas.download_service.R;

import java.util.Locale;

public class DownloadService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private static final String CHANNEL_ID = "download_message";
    private static final int NOTIFICATION_ID = 1;
    private static final String url = "https://dl1.msshuo.cn/market/apk/X7Market-4.107.999_1036.4537-prod-official-release.apk";
    private NotificationManager notificationManager;
    private int downloadId;
    private FileDownloader fileDownloader;
    private CallbackInterface callbackInterface;
    private final FileDownloadListener listener =  new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            updateProgressNotification(soFarBytes, totalBytes);
            if (callbackInterface != null) {
                String format = String.format(Locale.CHINA, "%.2f", ((double) soFarBytes / totalBytes) * 100);
                callbackInterface.updateDownload(Float.parseFloat(format));
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            completedNotification();
            if (callbackInterface != null) {
                callbackInterface.finishDownload();
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            pausedNotification();
            if (callbackInterface != null) {
                callbackInterface.pauseDownload();
            }
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            errorNotification();
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            errorNotification();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        createMessageNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startNotification();
        downloadApp();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        callbackInterface = null;
        return super.onUnbind(intent);
    }

    public void continueDownloadTask() {
        downloadApp();
    }

    public void pauseDownloadTask() {
        fileDownloader.pause(downloadId);
    }

    public class LocalBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }

    }

    private void downloadApp() {
        FileDownloader.setup(this);
        fileDownloader = FileDownloader.getImpl();
        downloadId = fileDownloader
                .create(DownloadService.url)
                .setPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/X7App/X7Market.apk")
                .setListener(listener)
                .start();
    }

    private void startNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("进度提醒")
                .setContentText("准备下载任务")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    private void updateProgressNotification(int progressCurrent, int progressMax) {
        String progress = String.format(Locale.CHINA, "%.2f", ((double) progressCurrent / progressMax) * 100);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("进度通知")
                .setContentText("下载中：" + progress + "%")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setProgress(progressMax, progressCurrent, false)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void completedNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("进度通知")
                .setContentText("下载完成")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(false)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void pausedNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("进度通知")
                .setContentText("暂停下载")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void errorNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("错误通知")
                .setContentText("下载出现错误")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(false)
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void createMessageNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = this.getString(R.string.app_name);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public interface CallbackInterface {
        void updateDownload(float progress);

        void pauseDownload();

        void finishDownload();
    }

    public void setCallback(CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

}
