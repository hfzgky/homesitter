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

    //멤버변수 선언
    private ListView mListView;
    private SimpleAdapter mSAdapter;
    private ArrayList<HashMap<String,String>> mListData; // 여러개의 정보를 저장하기 위해 HashMap 객체 사용
    private int mISelectedItem = -1;    //인덱스 값 저장, 현재 선택된 항목 없음
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
        intent.putExtra("item", -1);
        startActivityForResult(intent, 200);
    }

    public void onClickPreference(View view) {
        Intent intent = new Intent(this, PreferenceScreen.class);
        intent.putExtra("item", -1);
        startActivityForResult(intent, 200);
    }

    //이름 클릭 시
    public void onClickInfo(View view) {
        Intent intent= new Intent(getApplicationContext(), InfoActivity.class);
        intent.putExtra("item",-1);
        startActivityForResult(intent, 200);
    }

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
            Toast.makeText(getApplicationContext(), "수정할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            // 선택된 항목이 없음을 토스트창으로 알려줌
            return; //종료
        }

        //선택된 항목이 있을 경우
        HashMap<String,String> item = ((HashMap<String,String>)mSAdapter.getItem(mISelectedItem));  //선택항목을 읽어옴
        Intent intent = new Intent(FriendActivity.this, EditActivity.class);

        // name을 putExtra로 넘겨줌
        intent.putExtra("name", item.get("name"));
        intent.putExtra("item", mISelectedItem); // 현재 선택된 인덱스

        startActivityForResult(intent, 200);    //응답을 받음
    }

    //삭제 버튼 클릭 시
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


        //삭제 버튼 눌렀을 때 알러트창 나오게하기.. 근데 에러남....
/*        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("삭제").setMessage("선택하신 항목을 삭제하시겠습니까?");
        builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int count, checked ;
                count = mSAdapter.getCount() ;

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
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "삭제가 취소되었습니다", Toast.LENGTH_SHORT).show();
                mSAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();     */

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        //name
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

        if(requestCode != 200)  //editActivity에서 온 requestCode인지 확인하고 아닐경우 종료
            return;
        if(resultCode == RESULT_OK) {
            int item = data.getIntExtra("item", -1);

            HashMap<String,String> hitem = new HashMap<>();     //HashMap 생성
            //값을 넣어줌
            hitem.put("name", data.getStringExtra("name"));
            if(item == -1)  //새로운 항목 추가
                mListData.add(hitem);
            else    //수정
                mListData.set(item, hitem); //item 위치에 hitem을 저장
            mSAdapter.notifyDataSetChanged();

        } //else
       // Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_LONG).show(); //RESULT_OK가 아닐 경우 Toast창 알림
    }
}