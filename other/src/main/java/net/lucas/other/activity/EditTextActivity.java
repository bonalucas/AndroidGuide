package net.lucas.other.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.lucas.other.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text_layout);

        EditText edit_text = findViewById(R.id.edit_text);
        edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String editText = s.toString();
                Pattern p = Pattern.compile("[^0-9]");
                Matcher matcher = p.matcher(editText);
                String res = matcher.replaceAll("").trim();
                if (!editText.equals(res)) {
                    edit_text.setText(res);
                    edit_text.setSelection(res.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
