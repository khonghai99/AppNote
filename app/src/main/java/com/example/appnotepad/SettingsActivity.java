package com.example.appnotepad;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SettingsActivity extends AppCompatActivity {
    private TextView textViewBG, textViewFont, textViewColor, textViewSize;
    int DefaultColor;
    RelativeLayout relativeLayout;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Setting");

        textViewBG = (TextView) findViewById(R.id.txtBackGround);
        textViewFont = (TextView) findViewById(R.id.txtFont);
        textViewSize = (TextView) findViewById(R.id.txtSize);
        textViewColor = (TextView) findViewById(R.id.txtColor);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayoutAddEdit);

        textViewBG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenColorPickerDialog(false);
            }
        });
    }

    private void OpenColorPickerDialog(boolean AlphaSupport) {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(SettingsActivity.this, DefaultColor, AlphaSupport, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int color) {

                DefaultColor = color;

                constraintLayout.setBackgroundColor(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {

                Toast.makeText(SettingsActivity.this, "Color Picker Closed", Toast.LENGTH_SHORT).show();
            }
        });
        ambilWarnaDialog.show();

    }

}
