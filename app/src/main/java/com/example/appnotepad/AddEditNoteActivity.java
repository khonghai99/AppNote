package com.example.appnotepad;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddEditNoteActivity extends AppCompatActivity {
    private static final int MODE_CREATE = 1;
    private static final int MODE_EDIT = 2;

    private EditText textTitle;
    private EditText textContent;
    private Button buttonSave;
    private Button buttonCancel;
    private ImageView imageDate;
    private ImageView imageTime;
    private TextView textViewDate;
    private TextView textViewTime;

    private Note note;
    private boolean needRefresh;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);
        setTitle("Save");
        this.textTitle = (EditText) this.findViewById(R.id.editText_note_title);
        this.textContent = (EditText) this.findViewById(R.id.editText_note_content);

        this.buttonSave = (Button) findViewById(R.id.button_save);
        this.buttonCancel = (Button) findViewById(R.id.button_cancel);

        this.imageDate = (ImageView) findViewById(R.id.image_pick_date);
        this.imageTime = (ImageView) findViewById(R.id.image_pick_time);

        this.textViewDate = (TextView) findViewById(R.id.textView_date);
        this.textViewTime = (TextView) findViewById(R.id.textView_time);

        this.buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonSaveClicked();
            }
        });

        this.buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonCancelClicked();
            }
        });

        this.imageDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickDate();
            }
        });

        this.imageTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickTime();
            }
        });

        Intent intent = this.getIntent();
        this.note = (Note) intent.getSerializableExtra("note");
        if (note == null) {
            this.mode = MODE_CREATE;
        } else {
            this.mode = MODE_EDIT;
            this.textTitle.setText(note.getNoteTitle());
            this.textContent.setText(note.getNoteContent());
            this.textViewDate.setText(SplitDateTime(note.getNoteDate())[0]);
            this.textViewTime.setText(SplitDateTime(note.getNoteDate())[1]);
        }
    }

    //split date
    public String[] SplitDateTime(String str) {
        String[] words = str.split("\\s");
        return words;
    }

    // User Click on the image date
    private void PickDate() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                textViewDate.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    // User Click on the image time
    private void PickTime() {
        final Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(0, 0, 0, hourOfDay, minute);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                textViewTime.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, hours, minute, true);
        timePickerDialog.show();
    }

    // User Click on the Save button.
    public void buttonSaveClicked() {
        DBNoteHelper db = new DBNoteHelper(this);

        String title = this.textTitle.getText().toString();
        String content = this.textContent.getText().toString();
        String datetime = this.textViewDate.getText().toString() + " " + this.textViewTime.getText().toString();

        if (title.equals("") || content.equals("") || datetime.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please enter enough information", Toast.LENGTH_LONG).show();
            return;
        }

        if (mode == MODE_CREATE) {
            this.note = new Note(title, content, datetime);
            db.addNote(note);
        } else {
            this.note.setNoteTitle(title);
            this.note.setNoteContent(content);
            this.note.setNoteDate(datetime);
            db.updateNote(note);
        }

        this.needRefresh = true;

        // Back to MainActivity.
        this.onBackPressed();
    }

    // User Click on the Cancel button.
    public void buttonCancelClicked() {
        // Do nothing, back MainActivity.
        this.onBackPressed();
    }

    // When completed this Activity,
    // Send feedback to the Activity called it.
    @Override
    public void finish() {

        // Create Intent
        Intent data = new Intent();

        // Request MainActivity refresh its ListView (or not).
        data.putExtra("needRefresh", needRefresh);

        // Set Result
        this.setResult(Activity.RESULT_OK, data);
        super.finish();
    }
}
