package com.example.homesitter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity{
    private TextView mDayView;
    private TextView mTimeView;

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
}