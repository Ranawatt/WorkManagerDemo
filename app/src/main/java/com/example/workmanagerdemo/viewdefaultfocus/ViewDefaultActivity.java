package com.example.workmanagerdemo.viewdefaultfocus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.workmanagerdemo.R;

public class ViewDefaultActivity extends AppCompatActivity {

    EditText editText;
    Button removeFocus, gainFocus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_default);

        editText = findViewById(R.id.editText);
        removeFocus = findViewById(R.id.removeFocus);
        gainFocus = findViewById(R.id.gainFocus);
        gainFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setFocusableInTouchMode(true);
                editText.setFocusable(true);
            }
        });
        removeFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setFocusableInTouchMode(false);
                editText.setFocusable(false);
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Toast.makeText(ViewDefaultActivity.this, "focus loosed", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ViewDefaultActivity.this, "focused", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
