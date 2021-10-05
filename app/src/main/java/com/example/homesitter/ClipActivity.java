package com.example.homesitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ClipActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis()>backKeyPressedTime + 2500){
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
    }

    public void onClickMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickFriends(View view) {
        Intent intent = new Intent(this, FriendActivity.class);
        startActivity(intent);
        finish();
    }

   public void onClickPreference(View view) {
        Intent intent = new Intent(this, PreferenceScreen.class);
        intent.putExtra("item", -1);
        startActivityForResult(intent, 200);
    }

}