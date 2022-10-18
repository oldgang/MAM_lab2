package com.example.mam_lab2;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Button playButton;
    Button stopButton;
    boolean externalStorage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        mediaPlayer.setVolume(50,50);

        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playMusic();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMusic();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(!checkStoragePermission()){
            requestStoragePermission();
        }
    }

    public boolean checkStoragePermission(){
        if(Environment.isExternalStorageManager())
            Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
        return Environment.isExternalStorageManager();
    }

    public void requestStoragePermission(){
        Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID));
        startActivityForResult(intent, 999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK && requestCode == 999)
        {
            checkStoragePermission();
        }
    }

    public void playMusic(){
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playButton.setText(getString(R.string.pauseButton_text));
        }
        else if (mediaPlayer.isPlaying()){
            pauseMusic();
        }
    }

    public void pauseMusic(){
        mediaPlayer.pause();
        playButton.setText(getString(R.string.playButton_text));
    }

    public void stopMusic(){
            mediaPlayer.stop();
            playButton.setText(getString(R.string.playButton_text));
            mediaPlayer = MediaPlayer.create(this, R.raw.song);
    }
}