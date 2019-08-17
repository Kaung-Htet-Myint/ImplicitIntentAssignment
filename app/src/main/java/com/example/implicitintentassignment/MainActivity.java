package com.example.implicitintentassignment;

import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_VIDEO_GET = 100;
    private static final int PICK_CONTACT = 10;

    private VideoView vvSelectedVideo;
    TextView tvSelectedContactName, tvSelectedContactNumber;

    Chronometer simpleChronometer;
    Button start, stop, restart, btnCreateDateEvent, btnWebSearch;

    private Uri fileUri;
    EditText etWebSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //Timer
        simpleChronometer = findViewById(R.id.cm_timer);
        start = findViewById(R.id.btn_start);
        stop = findViewById(R.id.btn_stop);
        restart = findViewById(R.id.btn_restart);

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                simpleChronometer.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleChronometer.stop();
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpleChronometer.setBase(SystemClock.elapsedRealtime());
            }
        });


        // Creating a date Event
        btnCreateDateEvent = findViewById(R.id.btn_select_date);
        btnCreateDateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "hi", Toast.LENGTH_LONG).show();
                Calendar calendarEvent = Calendar.getInstance();
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", calendarEvent.getTimeInMillis());
                intent.putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60 * 1000);
                intent.putExtra("title", "Sample Event");
                intent.putExtra("allDay", true);
                intent.putExtra("rule", "FREQ=YEARLY");
                startActivity(intent);
            }
        });


        //Capturing a Video
        vvSelectedVideo = findViewById(R.id.vv_video_captured);
        Button btnSelectVideo = findViewById(R.id.btn_select_video);
        btnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_GET);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No camera on device", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Respond to Select Contact Button
        tvSelectedContactName = findViewById(R.id.tv_contact_name);
        tvSelectedContactNumber = findViewById(R.id.tv_contact_phone_no);
        Button btnSelectContact = findViewById(R.id.btn_select_contact);
        btnSelectContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contactPickIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(contactPickIntent, PICK_CONTACT);
            }
        });

        //Performing a Web Search
        etWebSearch = findViewById(R.id.et_web_search);
        btnWebSearch = findViewById(R.id.btn_web_search);
        btnWebSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webSearchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                webSearchIntent.putExtra(SearchManager.QUERY,etWebSearch.getText().toString());
                startActivity(webSearchIntent);
            }
        });


    }

    public void playbackRecordedVideo() {
        vvSelectedVideo.setVideoURI(fileUri);
        vvSelectedVideo.setMediaController(new MediaController(this));
        vvSelectedVideo.requestFocus();
        vvSelectedVideo.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_GET && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            playbackRecordedVideo();
        } else if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {

            Uri contactData = data.getData();
            Cursor cursor = managedQuery(contactData, null, null, null, null);
            cursor.moveToFirst();

            String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Contactables.DISPLAY_NAME));
            String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

            tvSelectedContactName.setText(name);

            tvSelectedContactNumber.setText(number);

        }


    }


}

