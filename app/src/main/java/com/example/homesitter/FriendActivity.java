package com.example.homesitter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
        // pic, name, phone을 putExtra로 넘겨줌
        intent.putExtra("name", item.get("name"));
        intent.putExtra("phone", item.get("phone"));
        intent.putExtra("item", mISelectedItem); // 현재 선택된 인덱스
        startActivityForResult(intent, 200);    //응답을 받음
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //pic ,name, phone
        mListData = new ArrayList<>();
        mSAdapter = new SimpleAdapter(this, mListData, R.layout.list_item,
                new String[] {"pic", "name", "phone"}, new int[] {R.id.image1, R.id.text1, R.id.text2,} );
        mListView = findViewById(R.id.listView);
        mListView.setAdapter(mSAdapter);

        // 각 항목을 클릭했을때를 위한 이벤트 처리
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mISelectedItem = i;
                HashMap<String,String> item = (HashMap<String, String>) mSAdapter.getItem(i);
                Toast.makeText(getApplicationContext(), item.get("name"), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != 200)  //editActivity에서 온 requestCode인지 확인하고 아닐경우 종료
            return;
        if(resultCode == RESULT_OK) {
            int item = data.getIntExtra("item", -1);

            HashMap<String,String> hitem = new HashMap<>();     //HashMap 생성
            //값을 넣어줌
            hitem.put("name", data.getStringExtra("name"));
            hitem.put("phone", data.getStringExtra("phone"));
            if(item == -1)  //새로운 항목 추가
                mListData.add(hitem);
            else    //수정
                mListData.set(item, hitem); //item 위치에 hitem을 저장
            mSAdapter.notifyDataSetChanged();

        } else
            Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_LONG).show(); //RESULT_OK가 아닐 경우 Toast창 알림
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
        intent.putExtra("item", -1);
        startActivityForResult(intent, 200);
    }

}