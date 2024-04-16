package net.lucas.custom_dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.lucas.custom_dialog.dialog.DemoDialog;
import net.lucas.custom_dialog.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DemoDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_dialog_activity);
        EventBus.getDefault().register(this);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoDialog demoDialog = new DemoDialog(DemoDialogActivity.this);
                demoDialog.setOnClickListener(new DemoDialog.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.csdn.net/"));
                        startActivity(intent);
                    }

                    @Override
                    public void onCancel(String input) {
                        MessageEvent event = new MessageEvent(input);
                        EventBus.getDefault().post(event);
                        demoDialog.close();
                    }
                });
                demoDialog.show();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Toast.makeText(DemoDialogActivity.this, event.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}