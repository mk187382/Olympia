package com.olympicwinners.olympia;

/**
 * Created by Zarow on 22.11.2016.
 */

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    Songs songs;
    String stream;
    ArrayList<String> songsList;
    private int length = 0;
    private Random randomGenerator;

    public MusicService() {
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPlayer.start();
    }

    public class ServiceBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        songsList = new ArrayList<>();
        randomGenerator = new Random();
        mPlayer.setOnErrorListener(this);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(100, 100);
        }


        mPlayer.setOnErrorListener(new OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int
                    extra) {

                onError(mPlayer, what, extra);
                return true;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        songs = Songs.getInstance();
        if(songs.getFiles()==null){
            stream ="http://incompetech.com/music/royalty-free/mp3-royaltyfree/Killers.mp3";
        }else {
            songsList.addAll(songs.getFiles());
            stream = anyItem();
        }
        //Log.d("Stream","Stream="+stream);
        Uri uri = Uri.parse(stream);
        Log.d("Stream","Stream="+uri);
        mPlayer = MediaPlayer.create(this, uri);
        try {
            mPlayer.setDataSource(stream);
        } catch (IllegalArgumentException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly1!", Toast.LENGTH_LONG).show();
        } catch (SecurityException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly2!", Toast.LENGTH_LONG).show();
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly3!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            //Toast.makeText(getApplicationContext(), "You might not set the URI correctly4!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.setOnErrorListener(this);
        mPlayer.start();
        return START_STICKY;
    }

    public void  playSong(){
        // Play song
        try {
            mPlayer.reset();
            mPlayer.setDataSource(anyItem());
            Log.d("Stream","Stream="+anyItem());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getSongsList() {
        return songsList;
    }

    public void setSongsList(ArrayList<String> songsList) {
        this.songsList = songsList;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public void pauseMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            length = mPlayer.getCurrentPosition();
        }
    }

    public void resumeMusic() {
        if (mPlayer.isPlaying() == false) {
            mPlayer.seekTo(length);
            mPlayer.start();
        }
    }

    public void stopMusic() {
        mPlayer.stop();
        mPlayer.release();
        //mPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                songsList.clear();
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer != null) {
            try {
                songsList.clear();
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }

    public String anyItem()
    {
        int index = randomGenerator.nextInt(songsList.size());
        String item = songsList.get(index);
        Log.d("JSwa", "Managers choice this week " + item + " our recommendation to you");
        return item;
    }
}
