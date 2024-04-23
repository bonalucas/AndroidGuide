package net.lucas.download_service;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import net.lucas.download_service.service.DownloadService;
import net.lucas.download_service.view.DownloadProgressButton;

public class DownloadActivity extends AppCompatActivity implements DownloadService.CallbackInterface {

    public static boolean isStartService, havePermission;
    private boolean isBindService;
    private static final int REQUEST_CODE = 1;
    private AlertDialog dialog;
    private DownloadProgressButton downloadButton;
    private DownloadService downloadService;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = ((DownloadService.LocalBinder) service).getService();
            downloadService.setCallback(DownloadActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_layout);
        downloadButton = (DownloadProgressButton) findViewById(R.id.download_btn);
        downloadButton.setCurrentText("开始下载");
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTheButton();
            }
        });
        Button turn_button = findViewById(R.id.turn);
        turn_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DownloadActivity.this, OtherActivity.class);
                intent.putExtra("state", downloadButton.getState());
                intent.putExtra("progress", downloadButton.getProgress());
                startActivityForResult(intent, 200);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!havePermission) {
            requestPermission();
        }
        if (isStartService && !isBindService) {
            Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
            bindService(intent, serviceConnection, 0);
            isBindService = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBindService) {
            unbindService(serviceConnection);
            isBindService = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(DownloadActivity.this, DownloadService.class));
        isStartService = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == 0) {
            if (data != null) {
                int state = data.getIntExtra("state", DownloadProgressButton.STATE_NORMAL);
                float progress = data.getFloatExtra("progress", 0);
                downloadButton.setState(state);
                if (state == DownloadProgressButton.STATE_NORMAL) {
                    downloadButton.setCurrentText("开始下载");
                    return;
                }
                if (state == DownloadProgressButton.STATE_PAUSE) {
                    downloadButton.setProgressText("下载中", progress);
                    downloadButton.setCurrentText("继续");
                    return;
                }
                if (state == DownloadProgressButton.STATE_DOWNLOADING) {
                    downloadButton.setProgressText("下载中", progress);
                    return;
                }
                if (state == DownloadProgressButton.STATE_AVAILABLE ||
                        state == DownloadProgressButton.STATE_FINISH) {
                    downloadButton.setProgressText("下载中", progress);
                    downloadButton.setCurrentText("打开");
                }
            }
        }
    }

    @Override
    public void updateDownload(float progress) {
        downloadButton.setState(DownloadProgressButton.STATE_DOWNLOADING);
        downloadButton.setProgressText("下载中", progress);
    }

    @Override
    public void pauseDownload() {
        downloadButton.setState(DownloadProgressButton.STATE_PAUSE);
        downloadButton.setCurrentText("继续");
    }

    @Override
    public void finishDownload() {
        downloadButton.setState(DownloadProgressButton.STATE_FINISH);
        downloadButton.setCurrentText("安装中");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                downloadButton.setState(DownloadProgressButton.STATE_AVAILABLE);
                downloadButton.setCurrentText("打开");
            }
        }, 2000);
    }

    private void showTheButton() {

        if (downloadButton.getState() == DownloadProgressButton.STATE_NORMAL) {
            if (havePermission) {
                // 设置按钮状态
                downloadButton.setState(DownloadProgressButton.STATE_DOWNLOADING);
                downloadButton.setProgressText("下载中", 0);
                // 开启下载服务
                Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
                isStartService = true;
                // 绑定下载服务进行通信
                bindService(intent, serviceConnection, 0);
                isBindService = true;
            } else {
                Toast.makeText(DownloadActivity.this, "无文件访问权限，无法下载", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (downloadButton.getState() == DownloadProgressButton.STATE_PAUSE) {
            if (downloadService != null) {
                downloadService.continueDownloadTask();
            }
            return;
        }

        if (downloadButton.getState() == DownloadProgressButton.STATE_DOWNLOADING) {
            if (downloadService != null) {
                downloadService.pauseDownloadTask();
            }
            return;
        }

        if (downloadButton.getState() == DownloadProgressButton.STATE_AVAILABLE) {
            Toast.makeText(DownloadActivity.this, "已完成安装", Toast.LENGTH_SHORT).show();
        }

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                dialog = new AlertDialog.Builder(this)
                        .setTitle("授权提示")
                        .setMessage("授权开启文件访问权限")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                havePermission = false;
                                Toast.makeText(DownloadActivity.this, "已拒绝外部存储权限", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.setData(Uri.parse("package:" + DownloadActivity.this.getPackageName()));
                                startActivity(intent);
                            }
                        }).create();
                dialog.show();
            } else {
                havePermission = true;
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DownloadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    havePermission = true;
                }
            } else {
                havePermission = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                havePermission = true;
            } else {
                havePermission = false;
                Toast.makeText(this, "已拒绝外部存储权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
