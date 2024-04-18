package net.lucas.custom_view.download;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.lucas.custom_view.R;

import java.util.Random;

public class DownloadProgressActivity extends AppCompatActivity {

    private DownloadProgressButton mDownloadProgressButton;
    private Button mReset;
    private Runnable downloadTask;
    private Handler handler;
    private Random random;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_progress_layout);
        mReset = (Button) findViewById(R.id.reset);
        mDownloadProgressButton = (DownloadProgressButton) findViewById(R.id.download_btn);
        handler = new Handler();
        random = new Random();

        mDownloadProgressButton.setCurrentText("安装");
        mDownloadProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTheButton();
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDownloadTimer();
                mDownloadProgressButton.setState(DownloadProgressButton.STATE_NORMAL);
                mDownloadProgressButton.setCurrentText("安装");
                mDownloadProgressButton.setProgress(0);
            }
        });
    }

    private void showTheButton() {

        if (mDownloadProgressButton.getState() == DownloadProgressButton.STATE_NORMAL
                || mDownloadProgressButton.getState() == DownloadProgressButton.STATE_PAUSE) {
            startDownloadTimer();
            return;
        }

        if (mDownloadProgressButton.getState() == DownloadProgressButton.STATE_DOWNLOADING) {
            cancelDownloadTimer();
            mDownloadProgressButton.setState(DownloadProgressButton.STATE_PAUSE);
            mDownloadProgressButton.setCurrentText("继续");
        }

        if (mDownloadProgressButton.getState() == DownloadProgressButton.STATE_AVAILABLE) {
            Toast.makeText(DownloadProgressActivity.this, "已完成安装", Toast.LENGTH_SHORT).show();
        }

    }

    private void startDownloadTimer() {
        downloadTask = new Runnable() {
            @Override
            public void run() {
                mDownloadProgressButton.setState(DownloadProgressButton.STATE_DOWNLOADING);
                if (mDownloadProgressButton.getProgress() >= mDownloadProgressButton.getMaxProgress()) {
//                    mDownloadProgressButton.setProgressText("下载中", mDownloadProgressButton.getMaxProgress());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadProgressButton.setState(DownloadProgressButton.STATE_FINISH);
                            mDownloadProgressButton.setCurrentText("安装中");
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    mDownloadProgressButton.setState(DownloadProgressButton.STATE_AVAILABLE);
                                    mDownloadProgressButton.setCurrentText("打开");
                                }
                            }, 2000);
                        }
                    }, 2000);
                } else {
                    mDownloadProgressButton.setProgressText("下载中", mDownloadProgressButton.getProgress() + random.nextInt(5) + 5);
                    handler.postDelayed(downloadTask, 1200);
                }
            }
        };
        handler.post(downloadTask);
    }

    private void cancelDownloadTimer() {
        if (downloadTask != null) {
            handler.removeCallbacks(downloadTask);
        }
    }

}
