package com.example.homesitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Runnable{
    private Thread mWorker = null;
    private LinearLayout mViewLayout= null;
    private TextView mTimeView = null;
    private TextView mDayView = null;
    private boolean mRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

//            mDayView.setText(String.format("%04d-%02d-%02d",yy,mm,dd));
//            mTimeView.setText(String.format("%02d:%02d:%02d",h,m,s));

            wait(200);
        }
    }

    private void wait(int ms) {
        SystemClock.sleep(ms);
    }

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
        Intent tt = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + 112));
        startActivity(tt);
    }

    public void onClickPreference(View view) {
        Intent intent = new Intent(this, PreferenceScreen.class);
        startActivity(intent);
        finish();
    }

}