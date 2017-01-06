package com.olympicwinners.olympia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Random;


public class MoodChoice extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 500;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    Songs SONGS;
    Button joyButton;
    Button euphoriaButton;
    Button impatientButton;
    Button calmButton;
    Button excitedButton;
    Button angryButton;
    Button sadButton;
    Button melancholicButton;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SONGS = Songs.getInstance();
        setContentView(R.layout.activity_mood_choice);

        joyButton = (Button) findViewById(R.id.joyBtn);
        euphoriaButton = (Button) findViewById(R.id.euphoriaBtn);
        impatientButton = (Button) findViewById(R.id.impatientBtn);
        calmButton = (Button) findViewById(R.id.calmBtn);
        excitedButton = (Button) findViewById(R.id.excitedBtn);
        angryButton = (Button) findViewById(R.id.angryBtn);
        sadButton = (Button) findViewById(R.id.sadBtn);
        melancholicButton = (Button) findViewById(R.id.melancholicBtn);
        joyButton.setOnClickListener(myOnlyhandler);
        euphoriaButton.setOnClickListener(myOnlyhandler);
        impatientButton.setOnClickListener(myOnlyhandler);
        calmButton.setOnClickListener(myOnlyhandler);
        excitedButton.setOnClickListener(myOnlyhandler);
        angryButton.setOnClickListener(myOnlyhandler);
        sadButton.setOnClickListener(myOnlyhandler);
        melancholicButton.setOnClickListener(myOnlyhandler);

        mVisible = true;

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            Intent myIntent = new Intent(MoodChoice.this, MoodMenu.class);
            Random r = new Random();
            int i1 = r.nextInt(2);
            switch (v.getId()) {
                case R.id.joyBtn:
                    if (i1 == 1) {
                        SONGS.addFile(getResources().getResourceName(R.raw.joyfull1));
                    } else {
                        SONGS.addFile(getResources().getResourceName(R.raw.joyfull2));
                    }
                    MoodChoice.this.startActivity(myIntent);
                    break;
                case R.id.euphoriaBtn:
                    if (i1 == 1) {
                        SONGS.addFile(getResources().getResourceName(R.raw.euphoric1));
                    } else {
                        SONGS.addFile(getResources().getResourceName(R.raw.euphoric2));
                    }
                    MoodChoice.this.startActivity(myIntent);
                    break;
                case R.id.impatientBtn:
                    if (i1 == 1) {
                        SONGS.addFile(getResources().getResourceName(R.raw.impatient1));
                    } else {
                        SONGS.addFile(getResources().getResourceName(R.raw.impatient2));
                    }
                    MoodChoice.this.startActivity(myIntent);
                    break;
                case R.id.calmBtn:
                    if (i1 == 1) {
                        SONGS.addFile(getResources().getResourceName(R.raw.calmness1));
                    } else {
                        SONGS.addFile(getResources().getResourceName(R.raw.calmness2));
                    }
                    MoodChoice.this.startActivity(myIntent);
                    break;
                case R.id.excitedBtn:
                    if (i1 == 1) {
                        SONGS.addFile(getResources().getResourceName(R.raw.excited1));
                    } else {
                        SONGS.addFile(getResources().getResourceName(R.raw.excited2));
                    }
                    MoodChoice.this.startActivity(myIntent);
                    break;
                case R.id.angryBtn:
                    if (i1 == 1) {
                        SONGS.addFile(getResources().getResourceName(R.raw.angry1));
                    } else {
                        SONGS.addFile(getResources().getResourceName(R.raw.angry2));
                    }
                    MoodChoice.this.startActivity(myIntent);
                    break;
                case R.id.sadBtn:
                    if (i1 == 1) {
                        SONGS.addFile(getResources().getResourceName(R.raw.sad1));
                    } else {
                        SONGS.addFile(getResources().getResourceName(R.raw.sad2));
                    }
                    MoodChoice.this.startActivity(myIntent);
                    break;
                case R.id.melancholicBtn:
                    if (i1 == 1) {
                        SONGS.addFile(getResources().getResourceName(R.raw.melanholic1));
                    } else {
                        SONGS.addFile(getResources().getResourceName(R.raw.melanholic2));
                    }
                    MoodChoice.this.startActivity(myIntent);
                    break;
            }
        }
    };

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void startMenu(View view) {
        Intent intent = new Intent(MoodChoice.this, MoodMenu.class);
        MoodChoice.this.startActivity(intent);
    }
}
