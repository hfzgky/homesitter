package com.example.homesitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendActivity extends AppCompatActivity {

    //멤버변수 선언
    private ListView mListView;
    private SimpleAdapter mSAdapter;
    private ArrayList<HashMap<String,String>> mListData; // 여러개의 정보를 저장하기 위해 HashMap 객체 사용
    private int mISelectedItem = -1;    //인덱스 값 저장, 현재 선택된 항목 없음

    //추가 버튼 클릭 시
    public void onClickAdd(View v) {
        Intent intent = new Intent(getApplicationContext(), EditActivity.class);    //인텐트 생성하여 EditActivity로 이동
        intent.putExtra("item", -1);    //mIselectedItem
        startActivityForResult(intent, 200);
    }

    //수정 버튼 클릭 시
    public void onClickEdit(View v) {
        //선택된 항목이 없을 경우
        if(mISelectedItem == -1) {
            Toast.makeText(getApplicationContext(), "수정할 항목을 선택해주세요. ", Toast.LENGTH_LONG).show();  // 선택된 항목이 없음을 토스트창으로 알려줌
            return; //종료
        }

        //선택된 항목이 있을 경우
        HashMap<String,String> item = ((HashMap<String,String>)mSAdapter.getItem(mISelectedItem));  //선택항목을 읽어옴
        Intent intent = new Intent(FriendActivity.this, EditActivity.class);
        intent.putExtra("year", item.get("year"));  // year, month, day, title, diary을 putExtra로 넘겨줌
        intent.putExtra("month", item.get("month"));
        intent.putExtra("day", item.get("day"));
        intent.putExtra("title", item.get("title"));
        intent.putExtra("diary", item.get("diary"));
        intent.putExtra("item", mISelectedItem); // 현재 선택된 인덱스
        startActivityForResult(intent, 200);    //응답을 받음
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
    }

    public void onClickMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickClip(View view) {
        Intent intent = new Intent(this, ClipActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickPreference(View view) {
        Intent intent = new Intent(this, PreferenceScreen.class);
        startActivity(intent);
        finish();
    }

}