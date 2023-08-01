package com.example.homesitter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private TextView mDayView;
    private TextView mTimeView;
    FirebaseStorage storage2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference childreference;
    StorageReference videoRef;
    File localFile;
    VideoView videoView;
    String videoname;
    File videopath;
    String[] link;
    String target, storagelink;
    int delete_count = 0;
    int delete_count_link = 0;
    int delete_count_link2 = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis()>backKeyPressedTime + 2500){
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }

    public void onClickClip(View view) {
        Intent intent = new Intent(this, ClipActivity.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "모니터링을 원하는 시각을 선택해주세요.", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onClickFriends(View view) {
        Intent intent = new Intent(this, FriendActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickCall(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
        startActivity(intent);
    }

    public void onClickPreference(View view) {
        Intent intent = new Intent(this, PreferenceScreen.class);
        intent.putExtra("item", -1);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView) findViewById(R.id.videoView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        childreference = firebaseDatabase.getReference().child("cctv/videoLink/real/");
        storage2 = FirebaseStorage.getInstance();
        Handler mHandler = new Handler(new Handler.Callback() {
            @SuppressLint("DefaultLocale")
            @Override
            public boolean handleMessage(Message msg) {
                Calendar cal = Calendar.getInstance();
                int yy = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH)+1;
                int dd = cal.get(Calendar.DAY_OF_MONTH);
                int h = cal.get(Calendar.HOUR_OF_DAY);
                int m = cal.get(Calendar.MINUTE);
                int s = cal.get(Calendar.SECOND);

                mDayView = (TextView) findViewById(R.id.day_view);
                mDayView.setGravity(Gravity.BOTTOM);
                mDayView.setTextSize(18);
                mDayView.setTextColor(Color.BLACK);
                mDayView.setText(String.format(""));

                mTimeView = (TextView) findViewById(R.id.time_view);
                mTimeView.setTextSize(18);
                mTimeView.setTextColor(Color.BLACK);
                mTimeView.setText(String.format(""));

                mDayView.setText(String.format("%04d-%02d-%02d", yy, mm, dd));
                mTimeView.setText(String.format("%02d:%02d:%02d", h, m, s));

                return true;
            }
        });

        class NewRunnable implements Runnable {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(0);
                }
            }
        }

        NewRunnable nr = new NewRunnable();
        Thread t = new Thread(nr);
        t.start();

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                ValueEventListener valueEventListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int k=0;
                        long count = dataSnapshot.getChildrenCount()-2;
                        int int_count = (int)count;
                        link = new String[int_count];
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            link[k]=messageData.getKey();
                            k++;
                            if(k==int_count) break;
                        }
                        //확인용
                        for(int i=0;i<link.length;i++)
                            System.out.println(link[i]);
                        System.out.println("링크 갯수:"+link.length);
                        target=link[link.length-1];
                        storagelink=target+".mp4";
                        System.out.println("storage link:"+storagelink);
                        videoRef = storage2.getReferenceFromUrl("gs://homesitter-54d69.appspot.com").child("/cctv/video/"+storagelink);
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
                childreference.addListenerForSingleValueEvent(valueEventListener);
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

        MediaController controller = new MediaController(com.example.homesitter.MainActivity.this);
        videoView.setMediaController(controller);
        videoView.requestFocus();
        String path = getFilesDir() + "/realtime" + "/" + videoname;
        videoView.setVideoPath(path);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.seekTo(0);
                videoView.start();
                // deleteVideo();
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
            videoname=target+".mp4";
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
}