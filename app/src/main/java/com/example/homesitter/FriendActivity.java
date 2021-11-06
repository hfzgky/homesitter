package com.example.homesitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendActivity extends AppCompatActivity {
    private ListView mListView;
    private SimpleAdapter mSAdapter;
    private ArrayList<HashMap<String,String>> mListData;
    private int mISelectedItem = -1;
    private long backKeyPressedTime = 0;
    private Toast toast;

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

    public void onClickAdd(View v) {
        Intent intent = new Intent(getApplicationContext(), EditActivity.class);
        intent.putExtra("item", -1);
        startActivityForResult(intent, 200);
    }

    public void onClickEdit(View v) {
        if(mISelectedItem == -1) {
            Toast.makeText(getApplicationContext(), "수정할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String,String> item = ((HashMap<String,String>)mSAdapter.getItem(mISelectedItem));
        Intent intent = new Intent(FriendActivity.this, EditActivity.class);
        intent.putExtra("name", item.get("name"));
        intent.putExtra("item", mISelectedItem);
        startActivityForResult(intent, 200);
    }

    public void onClickDel(View v) {
        int count, checked ;
        count = mSAdapter.getCount() ;

        if(mISelectedItem == -1) {
            Toast.makeText(getApplicationContext(), "삭제할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (count > 0) {
            checked = mListView.getCheckedItemPosition();

            if (checked > -1 && checked < count) {
                mListData.remove(checked) ;
                mListView.clearChoices();
                mSAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        mListData = new ArrayList<>();
        mSAdapter = new SimpleAdapter(this, mListData, R.layout.list_item,
                new String[] {"name"}, new int[] {R.id.text1});
        mListView = findViewById(R.id.listView);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setAdapter(mSAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mISelectedItem = i;
                HashMap<String,String> item = (HashMap<String, String>) mSAdapter.getItem(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != 200)
            return;
        if(resultCode == RESULT_OK) {
            int item = data.getIntExtra("item", -1);

            HashMap<String,String> hitem = new HashMap<>();
            hitem.put("name", data.getStringExtra("name"));
            if(item == -1)
                mListData.add(hitem);
            else
                mListData.set(item, hitem);
            mSAdapter.notifyDataSetChanged();
        }
    }
}