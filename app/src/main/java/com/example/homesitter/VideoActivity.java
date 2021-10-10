package com.example.homesitter;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    VideoView vv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        
        vv=findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        //임시 Uri
        vv.setMediaController(new MediaController(this));
        vv.setVideoURI(videoUri);
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                vv.start();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(vv!=null&&vv.isPlaying()) vv.pause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(vv!=null) vv.stopPlayback();
    }
}