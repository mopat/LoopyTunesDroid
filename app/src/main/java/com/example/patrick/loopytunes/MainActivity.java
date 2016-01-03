package com.example.patrick.loopytunes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MotionEvent;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.os.Handler;
import android.media.SoundPool;
import android.media.SoundPool.*;
import android.media.AudioManager;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

import java.io.File;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    SoundPool soundPool;
    Button recordButton, stopButton, playButton;
    MediaRecorder mRecorder;
    MediaPlayer mPlayer;
    Handler timerHandler;
    Runnable timerRunnable;
    long startTime = 0;
    long endTime = 0;
    long timeDif = 0;
    int rec = 0;
    int isRecording = 0;
    File directory = Environment.getExternalStorageDirectory();
    ArrayList<Integer> soundIDs = new ArrayList<Integer>();
    ArrayList<File> samples = new ArrayList<File>();

    String mFileName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        recordButton = (Button) findViewById(R.id.recordButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        playButton = (Button) findViewById(R.id.playButton);
        mRecorder = null;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
            }
        };
        File direct = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/LoopyTunes");

        if (!direct.exists()) {
            if (direct.mkdir()) ; //directory is created;
        }
        // startMetronome();
        Log.d("Time", String.valueOf(System.currentTimeMillis()));
        initClickListener();
    }

    private void initClickListener() {
        recordButton.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (rec == 0) {
                            startRecording();

                            startTime = System.currentTimeMillis();
                        } else {
                            isRecording = 1;
                        }

                }
                return false;
            }
        });
        stopButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (rec == 0) {
                            // RELEASED
                            endTime = System.currentTimeMillis();
                            timeDif = endTime - startTime;
                            Log.d("timeDif", String.valueOf(timeDif));

                            stopRecording();
                            Button b = (Button) v;
                            b.setEnabled(false);
                            startMetronome();
                            isRecording = 2;
                            rec += 1;
                        }
                }
                // TODO Auto-generated method stub
                return false;
            }
        });
 /*       playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playRecording();
            }
        });*/
    }

    private void startMetronome() {
        //runs without a timer by reposting this handler at the end of the runnable
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("ISRECORDING", String.valueOf(isRecording));
                if (rec > 0) {
                    if (isRecording == 1)
                        startRecording();
                    else if (isRecording == 0)
                        stopRecording();
                }
                //Do something after 100ms
                Log.e("over", "over");
                playSingle();
                startMetronome();

            }
        }, timeDif);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Log.d("OFF", "HEY");
    }

    private void startRecording() {
        Log.d("Start", "START");
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        File sample;
        try {
            sample = File.createTempFile("smp" + String.valueOf(rec), ".ogg", directory);
        } catch (IOException e) {
            Log.e("ERROR", "sdcard access error");
            return;
        }
        samples.add(sample);
        mFileName = sample.getAbsolutePath();
        mRecorder.setOutputFile(sample.getAbsolutePath());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        isRecording = 0;
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("PREPARE", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        int soundID = soundPool.load(mFileName, 1);
        Log.d("ADD", "STOP");
        soundIDs.add(soundID);
        isRecording = 2;
       /* mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void playSingle() {
        //mPlayer.start();
/*        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {

            }
        });*/
        for (int i = 0; i < soundIDs.size(); i++) {

            int curId = soundIDs.get(i);
            Log.d("id", String.valueOf(curId));
            soundPool.play(curId, 1f, 1f, 1, 0, 1f);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
