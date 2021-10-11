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

    public void onClickClip(View view) {
        Intent intent = new Intent(this, ClipActivity.class);
        startActivity(intent);
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
        childreference = firebaseDatabase.getReference().child("cctv/VideoLink/");
        storage2 = FirebaseStorage.getInstance();
        Handler mHandler = new Handler(new Handler.Callback() {
            @SuppressLint("DefaultLocale")
            @Override
            public boolean handleMessage(Message msg) {
                Calendar cal = Calendar.getInstance();
                int yy = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH) + 1;
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
                        videoRef = storage2.getReferenceFromUrl("gs://homesitter-54d69.appspot.com").child("/cctv/Video/"+storagelink);
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

    public void deleteVideo() {
        //link에 속한 애들 length-1만큼 돌려서 삭제

        for (delete_count = 0; delete_count < link.length - 1; delete_count++) {
            storage2.getReferenceFromUrl("gs://homesitter-54d69.appspot.com").child("/cctv/Video/" + link[delete_count] + ".h264").delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // removeValue 말고 setValue(null)도 삭제가능
                            childreference.child(link[delete_count_link++]).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(link[delete_count_link2++], "파일,링크 삭제완료");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(link[delete_count_link2++], "링크 삭제실패");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(link[delete_count] + ".h264", "파일,링크 삭제실패");
                }
            });
        }
    }

}