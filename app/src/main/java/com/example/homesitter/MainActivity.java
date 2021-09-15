package com.example.homesitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity{
/*    private Thread mWorker = null;
    private LinearLayout mViewLayout= null;
    private TextView mTimeView = null;
    private TextView mDayView = null;
    TextView clockTextView ;
    private boolean mRun = false;   */
    private TextView mDayView;
    private TextView mTimeView;
//    private Handler mHandler ;



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
        /**
         * 현재 사용자의 OS버전이 마시멜로우 인지 체크한다.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /**
             * 사용자 단말기의 권한 중 전화걸기 권한이 허용되어 있는지 체크한다.
             */
            int permissionResult = checkSelfPermission(Manifest.permission.CALL_PHONE);

            // call_phong의 권한이 없을 떄
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                // Package는 Android Application의 ID이다.
                /**
                 // * 사용자가 CALL_PHONE 권한을 한번이라도 거부한 적이 있는지 조사한다.
                 * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
                 * 거부한 이력이 없다면 false를 리턴한다.
                 */
                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"전화걸기\"권한이 필요합니다. 계속하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 위 리스너랑 다른 범위여서 마쉬멜로우인지 또 체크해주어야 한다.
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                                    }
                                }
                            })
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MainActivity.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create()
                            .show();
                }
                // 최초로 권한을 요청 할 때
                else {
                    // CALL_PHONE 권한을 안드로이드 OS에 요청합니다.
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1000);
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01011112222"));
                    startActivity(intent);
                }
            }
            // call_phonne의 권한이 있을 떄
            else {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01011112222"));
                startActivity(intent);
            }
        }
        // 사용자의 버전이 마시멜로우 이하일때
        else {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:01011112222"));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 사용자 요청, 요청한 권한들, 응답들
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            // 요청한 권한을 사용자가 허용했다면
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:010-1111-2222"));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
            } else {
                Toast.makeText(MainActivity.this, "권한요청을 거부했습니다.", Toast.LENGTH_SHORT).show();
            }

        }


    }

//        Intent tt = new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + 112));
//        startActivity(tt);}

    public void onClickPreference(View view) {
        Intent intent = new Intent(this, PreferenceScreen.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler mHandler = new Handler(new Handler.Callback() {
            @SuppressLint("DefaultLocale")
            @Override
            public boolean handleMessage(Message msg) {
                Calendar cal = Calendar.getInstance() ;
                int yy = cal.get(Calendar.YEAR);
                int mm = cal.get(Calendar.MONTH)+1;
                int dd = cal.get(Calendar.DAY_OF_MONTH);
                int h = cal.get(Calendar.HOUR_OF_DAY);
                int m = cal.get(Calendar.MINUTE);
                int s = cal.get(Calendar.SECOND);

                mDayView = (TextView)findViewById(R.id.day_view);
                mDayView.setGravity(Gravity.BOTTOM);
                mDayView.setTextSize(18);
                mDayView.setTextColor(Color.BLACK);
                mDayView.setText(String.format(""));

                mTimeView = (TextView)findViewById(R.id.time_view);
                mTimeView.setTextSize(18);
                mTimeView.setTextColor(Color.BLACK);
                mTimeView.setText(String.format(""));

                mDayView.setText(String.format("%04d-%02d-%02d",yy,mm,dd));
                mTimeView.setText(String.format("%02d:%02d:%02d",h,m,s));

                return true;
            }
        }) ;

        class NewRunnable implements Runnable {
            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace() ;
                    }

                    mHandler.sendEmptyMessage(0) ;
                }
            }
        }

        NewRunnable nr = new NewRunnable() ;
        Thread t = new Thread(nr) ;
        t.start() ;


    }

/*        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mViewLayout = (LinearLayout) findViewById(R.id.view_layout);

        mDayView = (TextView)findViewById(R.id.day_view);
        mDayView.setGravity(Gravity.BOTTOM);
        mDayView.setTextSize(18);
        mDayView.setTextColor(Color.LTGRAY);
        mDayView.setText(String.format(""));

        mTimeView = (TextView)findViewById(R.id.time_view);
        mTimeView.setTextSize(30);
        mTimeView.setTextColor(Color.LTGRAY);
        mTimeView.setText(String.format(""));

        start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
    }

    void start()
    {
        if(mWorker!= null)
        {
            stop();
        }

        mRun = true;
        mWorker =  new Thread(this);
        mWorker.start();
    }

    void stop()
    {
        if(mWorker != null && mWorker.isAlive())
        {
            mRun = false;
            mWorker.interrupt();
            wait(1000);
            mWorker = null;
        }
    }

    @Override
    public void run() {


        while(mRun)
        {
            Calendar calendar = Calendar.getInstance();

            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH)+1;
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            int h = calendar.get(Calendar.HOUR_OF_DAY);
            int m = calendar.get(Calendar.MINUTE);
            int s = calendar.get(Calendar.SECOND);

            mDayView.setText(String.format("%04d-%02d-%02d",yy,mm,dd));
            mTimeView.setText(String.format("%02d:%02d:%02d",h,m,s));

            wait(200);
        }
    }

    private void wait(int ms) {
        SystemClock.sleep(ms);
    }           */

}