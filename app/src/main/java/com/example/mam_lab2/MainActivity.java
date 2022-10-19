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

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Button playButton;
    Button stopButton;
    Button choiceButton;
    Uri selectedFileURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        playButton.setEnabled(false);
        stopButton.setEnabled(false);
        choiceButton = (Button) findViewById(R.id.choiceButton);

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

        choiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFile();
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
        return Environment.isExternalStorageManager();
    }

    public void requestStoragePermission(){
        Intent intent = new Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID));
        startActivityForResult(intent, 999);
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 100);
    }

    public void createMediaPlayer(){
        mediaPlayer = MediaPlayer.create(this, selectedFileURI);
        mediaPlayer.setVolume(50,50);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK && requestCode == 999)
        {
            if(checkStoragePermission())
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            else{
                Toast.makeText(getApplicationContext(), "No storage access", Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == RESULT_OK && requestCode == 100 && resultData != null && resultData.getData() != null){
            playButton.setEnabled(true);
            stopButton.setEnabled(true);
            if(selectedFileURI == null) {
                selectedFileURI = resultData.getData();
                createMediaPlayer();
            }
            else{
                selectedFileURI = resultData.getData();
                mediaPlayer.reset();
                createMediaPlayer();
            }
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
            createMediaPlayer();
    }
}