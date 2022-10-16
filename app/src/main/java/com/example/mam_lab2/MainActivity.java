package com.example.mam_lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    Button playButton;
    Button stopButton;

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