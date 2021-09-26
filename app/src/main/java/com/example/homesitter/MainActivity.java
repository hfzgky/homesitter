package com.example.homesitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

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