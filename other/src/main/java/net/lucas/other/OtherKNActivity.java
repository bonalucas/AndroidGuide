package net.lucas.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.lucas.other.activity.EditTextActivity;
import net.lucas.other.activity.SpannableStringActivity;

public class OtherKNActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_kn_layout);

        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherKNActivity.this, EditTextActivity.class);
                startActivity(intent);
            }
        });

        Button spanButton = findViewById(R.id.span_button);
        spanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherKNActivity.this, SpannableStringActivity.class);
                startActivity(intent);
            }
        });
    }

}
