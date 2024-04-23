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

public class OtherActivity extends AppCompatActivity implements DownloadService.CallbackInterface {

    private DownloadProgressButton downloadButton;
    private boolean isBindService;
    private static final int REQUEST_CODE = 1;
    private AlertDialog dialog;
    private DownloadService downloadService;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadService = ((DownloadService.LocalBinder) service).getService();
            downloadService.setCallback(OtherActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_layout);
        downloadButton = (DownloadProgressButton) findViewById(R.id.download_btn);
        downloadButton.setCurrentText("开始下载");
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTheButton();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            int state = intent.getIntExtra("state", DownloadProgressButton.STATE_NORMAL);
            float progress = intent.getFloatExtra("progress", 0);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!DownloadActivity.havePermission) {
            requestPermission();
        }
        if (DownloadActivity.isStartService && !isBindService) {
            Intent intent = new Intent(OtherActivity.this, DownloadService.class);
            bindService(intent, serviceConnection, 0);
            isBindService = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        isBindService = false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("state", downloadButton.getState());
        intent.putExtra("progress", downloadButton.getProgress());
        setResult(0, intent);
        finish();
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
            if (DownloadActivity.havePermission) {
                // 设置按钮状态
                downloadButton.setState(DownloadProgressButton.STATE_DOWNLOADING);
                downloadButton.setProgressText("下载中", 0);
                Intent intent = new Intent(OtherActivity.this, DownloadService.class);
                if (!DownloadActivity.isStartService) {
                    // 开启下载服务
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                    DownloadActivity.isStartService = true;
                }
                // 绑定下载服务进行通信
                bindService(intent, serviceConnection, 0);
                isBindService = true;
            } else {
                Toast.makeText(OtherActivity.this, "无文件访问权限，无法下载", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(OtherActivity.this, "已完成安装", Toast.LENGTH_SHORT).show();
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
                                DownloadActivity.havePermission = false;
                                Toast.makeText(OtherActivity.this, "已拒绝外部存储权限", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.setData(Uri.parse("package:" + OtherActivity.this.getPackageName()));
                                startActivity(intent);
                            }
                        }).create();
                dialog.show();
            } else {
                DownloadActivity.havePermission = true;
            }
        } else {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(OtherActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    DownloadActivity.havePermission = true;
                }
            } else {
                DownloadActivity.havePermission = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DownloadActivity.havePermission = true;
            } else {
                DownloadActivity.havePermission = false;
                Toast.makeText(this, "已拒绝外部存储权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
