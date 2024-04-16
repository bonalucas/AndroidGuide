package net.lucas.file_storage;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;

import net.lucas.file_storage.pojo.Demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.file.Files;

public class FileStorageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 1;
    private boolean havePermission;

    private EditText jsonText;

    private TextView beanText;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_storage_layout);
        jsonText = findViewById(R.id.json_input);
        beanText = findViewById(R.id.bean_output);
        Button storeInside = findViewById(R.id.store_inside);
        storeInside.setOnClickListener(this);
        Button storeOutside = findViewById(R.id.store_outside);
        storeOutside.setOnClickListener(this);
        Button storeSD =  findViewById(R.id.store_sd);
        storeSD.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.store_inside) {
            storeInside(jsonText.getText().toString());
            beanText.setText("内部私有存储：" + parseJson(readInside()));
        } else if (v.getId() == R.id.store_outside) {
            storeOutside(jsonText.getText().toString());
            beanText.setText("外部私有存储" + parseJson(readOutSide()));
        } else if (v.getId() == R.id.store_sd) {
            if (havePermission) {
                storeSDCard(jsonText.getText().toString());
                beanText.setText("SDCard外部共享存储" + parseJson(readSDCard()));
            } else {
                requestPermission();
            }
        }
    }

    private void storeInside(String json) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("demo.json", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readInside() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput("demo.json");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    private void storeOutside(String json) {
        File externalFilesDir = getExternalFilesDir("json");
        if (externalFilesDir != null) {
            File file = new File(externalFilesDir, "demo.json");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readOutSide() {
        File externalFilesDir = getExternalFilesDir("json");
        File file = new File(externalFilesDir, "demo.json");
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath())));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    private void storeSDCard(String json) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/json";
        String fileName = "demo.json";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File tmp = new File(dir.getPath());
        if (!tmp.exists()) {
            tmp.mkdirs();
        }
        File file = new File(tmp, fileName);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fis = null;
        try {
            fis = new FileOutputStream(file);
            fis.write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String readSDCard() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/json";
        String fileName = "demo.json";
        InputStream in = null;
        Reader reader = null;
        BufferedReader bufferedReader = null;
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                return "";
            }
            File file = new File(dir, fileName);
            if (!file.exists()) {
                return "";
            }
            in = new FileInputStream(file);
            reader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(reader);
            StringBuilder content = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    private String parseJson(String json) {
        if (TextUtils.isEmpty(json)) return "";
        Gson gson = new Gson();
        Demo demo = gson.fromJson(json, Demo.class);
        return demo.toString();
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
                                Toast.makeText(FileStorageActivity.this, "已拒绝外部存储权限", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                intent.setData(Uri.parse("package:" + FileStorageActivity.this.getPackageName()));
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
                    ActivityCompat.requestPermissions(FileStorageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
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
