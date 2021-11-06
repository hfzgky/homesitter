package com.example.homesitter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.util.ObjectUtils;

import java.io.File;


public class Video_each extends AppCompatActivity {

    FirebaseStorage storage2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference childreference;
    StorageReference videoRef;
    File localFile;
    TextView textView, textView2;
    ProgressDialog dialog;
    VideoView videoView;
    String videoname;
    File videopath;
    String[] link;
    String target,storagelink;
    int delete_count=0;
    int delete_count_link=0;
    int delete_count_link2=0;
    String name_;
    String name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_each);
        FirebaseApp.initializeApp(this);

        videoView = (VideoView)findViewById(R.id.videoView);
        textView= (TextView)findViewById(R.id.textView);
        textView2= (TextView)findViewById(R.id.textView2);
        firebaseDatabase=FirebaseDatabase.getInstance();
        childreference=firebaseDatabase.getReference().child("cctv/videoLink/real/");
        storage2 = FirebaseStorage.getInstance();



        Intent intent = getIntent(); /*데이터 수신*/

        name = intent.getExtras().getString("selected_item");/*String형*/

        String datename = name.substring(0,4)+"-"+name.substring(4,6)+"-"+name.substring(6,8)+"  "+name.substring(8,10)+":"+name.substring(10,12)+":"+name.substring(12,14);
        textView.setText(datename);
        dialog = ProgressDialog.show(this, "영상 가져오기", "로딩 중 입니다.", true, true);

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0) {
                    dialog.dismiss();
                    playVideo(videoname);
                }

            }
        } ;

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                ValueEventListener valueEventListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        videoRef = storage2.getReferenceFromUrl("gs://homesitter-54d69.appspot.com").child("/cctv/video/"+name+".mp4");
                        downloadVideo(videoRef);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                childreference.addListenerForSingleValueEvent(valueEventListener); //맨 처음 한번만 호출됨

            }
        });
        th.start();

    }

    public void removeDir(File file) {
        File[] childFileList = file.listFiles();
        if (!(ObjectUtils.isEmpty(childFileList))) {
            for (File childFile : childFileList) {
                childFile.delete();    //하위 파일
            }
        }
    }

    public void playVideo(String videoname) {

        MediaController controller = new MediaController(com.example.homesitter.Video_each.this);
        videoView.setMediaController(controller);
        videoView.requestFocus();
        String path = getFilesDir()+"/realtime"+"/"+videoname;
        videoView.setVideoPath(path);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                dialog.dismiss();
                Toast.makeText(com.example.homesitter.Video_each.this,
                        "동영상이 준비되었습니다. 재생을 시작합니다.", Toast.LENGTH_SHORT).show();
                videoView.seekTo(0);
                videoView.start();
            }
        });

    }
    public void downloadVideo(StorageReference videoRef){
        try {
            videopath=new File(getFilesDir()+"/realtime");
            if(!videopath.exists()) {
                videopath.mkdir();
            }
            removeDir(videopath); //내부 저장소 내의 파일 모조리 삭제
            videoname=name_+".mp4";
            //  System.out.println(videoname);
            localFile = new File(getFilesDir()+"/realtime",videoname);

            videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d("Success ","영상 다운로드 완료");
                    playVideo(videoname);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void onClickCloseV(View view) {
        finish();
    }

}


