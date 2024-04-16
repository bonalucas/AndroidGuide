package net.lucas.custom_dialog.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import net.lucas.custom_dialog.R;

/**
 * 自定义对话框
 */
public class DemoDialog {

    private Context context;

    private OnClickListener listener;

    private Dialog dialog;

    private EditText editText;

    public DemoDialog(Context context) {
        this.context = context;
        View dialogView = LayoutInflater.from(context).inflate(R.layout.demo_dialog, null);
        editText = dialogView.findViewById(R.id.input_edit);
        Button cancelButton = dialogView.findViewById(R.id.cancel_button);
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancel(editText.getText().toString());
                }
            }
        });
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(dialogView);
        dialog = builder.create();
        // 设置 shape
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.dialog_shape);
        dialog.getWindow().setBackgroundDrawable(drawable);
        // 设置点击空白和返回键 Dialog 不关闭
        dialog.setCancelable(false);
        // 设置对话框的位置和动画
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setWindowAnimations(R.style.BottomDialogAnimationStyle);
        // 设置对话框的宽度和高度
        WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(attributes);
    }

    public void close() {
        dialog.dismiss();
    }

    public void show() {
        editText.setText("");
        dialog.show();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {

        void onConfirm();

        void onCancel(String input);

    }

}
