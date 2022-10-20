package com.example.mam_lab2;

import static android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    MediaRecorder mediaRecorder;
    Button playButton;
    Button stopButton;
    Button choiceButton;
    Button startRecordingButton;
    Button stopRecordingButton;
    Uri selectedFileURI;
    File rootDir = Environment.getExternalStorageDirectory();
    File recordingFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = (Button) findViewById(R.id.playButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        choiceButton = (Button) findViewById(R.id.choiceButton);
        startRecordingButton = (Button) findViewById(R.id.startRecordingButton);
        stopRecordingButton = (Button) findViewById(R.id.stopRecordingButton);

        playButton.setEnabled(false);
        stopButton.setEnabled(false);
        stopRecordingButton.setEnabled(false);

        playButton.setOnClickListener(v -> playMusic());
        stopButton.setOnClickListener(v -> stopMusic());
        choiceButton.setOnClickListener(view -> selectFile());
        startRecordingButton.setOnClickListener(view -> startRecording());
        stopRecordingButton.setOnClickListener(view -> stopRecording());

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
        mediaPlayer.release();
        mediaPlayer = null;
        playButton.setText(getString(R.string.playButton_text));
        createMediaPlayer();
    }

    public boolean checkAudioRecordingPermission(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestAudioRecordingPermission(){
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},200);
    }


    public void startRecording(){
        if(!checkAudioRecordingPermission())
            requestAudioRecordingPermission();
        if(!checkStoragePermission())
            requestStoragePermission();
        if(checkAudioRecordingPermission() && checkStoragePermission()) {
            try {
                recordingFile = File.createTempFile("Recording", ".mp3", rootDir);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Temp File exception", Toast.LENGTH_SHORT).show();
                return;
            }
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioChannels(1);
            mediaRecorder.setAudioSamplingRate(8000);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(recordingFile.getAbsolutePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            mediaRecorder.start();
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();
            startRecordingButton.setEnabled(false);
            stopRecordingButton.setEnabled(true);
        }
    }

    public void stopRecording(){
        Toast.makeText(getApplicationContext(), "Recording ended", Toast.LENGTH_SHORT).show();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        startRecordingButton.setEnabled(true);
        stopRecordingButton.setEnabled(false);
    }
}