package com.olympicwinners.olympia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MoodChoice extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 500;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final String TAG = "MoodChoice";
    private final Handler mHideHandler = new Handler();
    ArrayList<String> urls;
    Button joyButton;
    Button euphoriaButton;
    Button impatientButton;
    Button calmButton;
    Button excitedButton;
    Button angryButton;
    Button sadButton;
    Button melancholicButton;
    MoodChoice moodChoice;
    MoodChoice.ParseURL parseURL;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
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
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_choice);
        urls = new ArrayList<String>();
        urls.addAll(geturls());
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

        //mVisible = true;
        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        //mContentView = findViewById(R.id.fullscreen_content);


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        // findViewById(R.id.button4   ).setOnTouchListener(mDelayHideTouchListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    public void openMoodMenu(View view) {
        Intent myIntent = new Intent(MoodChoice.this,MoodMenu.class);
        MoodChoice.this.startActivity(myIntent);

    }

    View.OnClickListener myOnlyhandler = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.joyBtn:

                    randSong(urls.get(0));
                    break;
                case R.id.euphoriaBtn:
                    randSong(urls.get(1));
                    break;
                case R.id.impatientBtn:
                    randSong(urls.get(2));
                    break;
                case R.id.calmBtn:
                    randSong(urls.get(3));
                    break;
                case R.id.excitedBtn:
                    randSong(urls.get(4));
                    break;
                case R.id.angryBtn:
                    randSong(urls.get(5));
                    break;
                case R.id.sadBtn:
                    randSong(urls.get(6));
                    break;
                case R.id.melancholicBtn:
                    randSong(urls.get(7));
                    break;
            }

        }
    };

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MoodChoice Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public ArrayList<String> geturls() {
        InputStream inputStream = getResources().openRawResource(R.raw.urlstream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ArrayList<String> data = new ArrayList<String>();
        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Text Data", byteArrayOutputStream.toString());
        try {
            // Parse the data into jsonobject to get original data in form of json.
            JSONObject jObject = new JSONObject(
                    byteArrayOutputStream.toString());
            JSONObject jObjectResult = jObject.getJSONObject("Url");
            String cat_Id = "";
            String cat_name = "";
            Iterator<String> iter = jObjectResult.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    data.add((String) jObjectResult.get(key));
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public class ParseURL extends AsyncTask<String, Void, ArrayList<String>> {
        Random randomGenerator;
        ArrayList<String> linksToSongs;
        Songs SONGS;

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            randomGenerator = new Random();
            linksToSongs = new ArrayList<String>();
            SONGS = Songs.getInstance();


            StringBuilder buffer = new StringBuilder("");
       //     buffer.append("http://incompetech.com/music/royalty-free/");
            try {
                //Log.d("JSwa", "Connecting to [" + strings[0] + "]");
                Document doc = Jsoup.connect(strings[0]).get();
                //Log.d("JSwa", "Connected to [" + strings[0] + "]");
                // Get document (HTML page) title
                String title = doc.title();
                //Log.d("JSwA", "Title [" + title + "]");
/*                buffer.append("Title: " + title + "rn");

                // Get meta info
                Elements metaElems = doc.select("meta");
                buffer.append("META DATArn");
                for (Element metaElem : metaElems) {
                    String name = metaElem.attr("name");
                    String content = metaElem.attr("content");
                    buffer.append("name [" + name + "] - content [" + content + "] rn");
                }*/

                Elements topicList = doc.select("a[href$=.mp3]");
                for (Element topic : topicList) {
                    String data = topic.text();
                    data = data.replace("Download ","");
                    data = data.replace(" as mp3","");
                    data = data.replaceAll("\"","");
                    buffer.append("http://incompetech.com/music/royalty-free/mp3-royaltyfree/"+data+".mp3");
                    linksToSongs.add(buffer.toString());
                    buffer.setLength(0);
                    //Log.d("JSwa", "parsed values [" + linksToSongs.toString() + "]");
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            Log.d("JSwa", "parsed values ["+linksToSongs.toString()+"]");
            SONGS.addFile(anyItem());
            return linksToSongs;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);

        }

        public String anyItem()
        {
            int index = randomGenerator.nextInt(linksToSongs.size());
            String item = linksToSongs.get(index);
            Log.d("JSwa", "Managers choice this week " + item + " our recommendation to you");
            linksToSongs.clear();
            return item;
        }
    }
    void randSong(String s){
        (new ParseURL() ).execute(s);
    }
}