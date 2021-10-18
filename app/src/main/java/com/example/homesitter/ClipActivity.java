package com.example.homesitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ClipActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private ListView mListView;
    private SimpleAdapter mSAdapter;
    private ArrayList<HashMap<String,String>> mListData; // 여러개의 정보를 저장하기 위해 HashMap 객체 사용
    private int mISelectedItem = -1;    //인덱스 값 저장, 현재 선택된 항목 없음
    private ImageButton btnFrd;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);

        ImageButton btnBack;
        btnBack = findViewById(R.id.imageButton4);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void onClickMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*  public void onClickFriends(View view) {
          Intent intent = new Intent(this, FriendActivity.class);
          startActivity(intent);
          finish();
      }
  */
    public void onClickPreference(View view) {
        Intent intent = new Intent(this, PreferenceScreen.class);
        intent.putExtra("item", -1);
        startActivityForResult(intent, 200);
    }

    public void onClickDel(View v) {
        int count, checked ;
        count = mSAdapter.getCount() ;

        if(mISelectedItem == -1) {
            Toast.makeText(getApplicationContext(), "삭제할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return; //종료
        }

        if (count > 0) {
            // 현재 선택된 아이템의 position 획득.
            checked = mListView.getCheckedItemPosition();

            if (checked > -1 && checked < count) {
                mListData.remove(checked) ;  // 아이템 삭제
                mListView.clearChoices();  // listview 선택 초기화.
                mSAdapter.notifyDataSetChanged();   // listview 갱신.
            }
        }
    }

}