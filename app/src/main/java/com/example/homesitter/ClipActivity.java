package com.example.homesitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ClipActivity extends AppCompatActivity {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private ListView mListView;
    private SimpleAdapter mSAdapter;
    private ArrayList<HashMap<String,String>> mListData;
    private int mISelectedItem = -1;
    private ImageButton btnFrd;


    private ListView list;
    ArrayList<String> listdata;
    ArrayAdapter<String> adapter;
    private int selectedPosition;


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference childreference = firebaseDatabase.getReference().child("cctv/videoLink/real/");


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

        list = (ListView) findViewById(R.id.listView1);
        listdata = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listdata);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setOnItemClickListener(listener2);

        //db에서 가져와서 list 보여주는 코드
        childreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String message = messageData.getKey();
                    String datename = message.substring(0,4)+"-"+message.substring(4,6)+"-"+message.substring(6,8)+"  "
                            +message.substring(8,10)+":"+message.substring(10,12)+":"+message.substring(12,14);
                    listdata.add(datename);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    AdapterView.OnItemClickListener listener2= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            String temp = (String) adapterView.getItemAtPosition(position);
            String selected_item = temp.substring(0, 4)+temp.substring(5,7)+temp.substring(8,10)
                    +temp.substring(12,14)+temp.substring(15,17)+temp.substring(18,20);
            Intent intent10 = new Intent(getApplicationContext(), Video_each.class);
            intent10.putExtra("selected_item", selected_item);
            startActivity(intent10);
        }
    };

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

