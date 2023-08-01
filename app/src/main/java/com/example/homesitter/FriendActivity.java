package com.example.homesitter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendActivity extends AppCompatActivity {
    /*    private ListView mListView;
        private SimpleAdapter mSAdapter;
        private ArrayList<HashMap<String,String>> mListData;
        private int mISelectedItem = -1;
        private long backKeyPressedTime = 0;
        private Toast toast;
     */
    private int mISelectedItem = -1;
    private Toast toast;
    private long backKeyPressedTime = 0;
    private ListView list;
    static ArrayList<String> listdata;
    ArrayAdapter<String> adapter;

    private int mISelectedID = 0;

    public ArrayList<String> getList() {
        return listdata;
    }

    public int getPosition() {
        return selectedPosition;
    }

    Button deleteButton;
    private int selectedPosition;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
   // DatabaseReference childreference = firebaseDatabase.getReference().child("cctv/PhotoLink/realname/");

    DatabaseReference childreference10 = firebaseDatabase.getReference().child("cctv/PhotoLink/realname");
    DatabaseReference childreference100 = firebaseDatabase.getReference().child("cctv/PhotoLink/name");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("cctv/Photo/");


    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
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
        Toast.makeText(getApplicationContext(), "모니터링을 원하는 시각을 선택해주세요.", Toast.LENGTH_SHORT).show();
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
        if (mISelectedItem == -1) {
            Toast.makeText(getApplicationContext(), "수정할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = adapter.getItem(mISelectedItem);
        Intent intent = new Intent(FriendActivity.this, EditActivity2.class);
        intent.putExtra("name", name);
        intent.putExtra("id",mISelectedID);
     //   intent.putExtra("item", mISelectedItem);
        startActivity(intent);
       // intent.putExtra("item", mISelectedItem);
       // startActivityForResult(intent, 200);
    }

    public void onClickDel(View v) {
        int count, checked;
        count = adapter.getCount();

        if (mISelectedItem == -1) {
            Toast.makeText(getApplicationContext(), "삭제할 항목을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (count > 0) {
            checked = list.getCheckedItemPosition();

            if (checked > -1 && checked < count) {

                String origin = listdata.get(mISelectedItem);


                //선택되어 있는 항목 storage에서 모든 사진 제거
          /*      childreference10.child(origin).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            String key = messageData.getKey() + ".png";

                            //String file = String.valueOf(messageData.child("name").getValue()+".png");
                            storageRef.child(origin).child(key).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(FriendActivity.this, "선택 인물 삭제", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {            }
                });

           */


                for(int i = 1; i < 50; i++) {
                    // "cctv/Photo/각각의 폴더/~.png"에서 각각의 폴더에 몇 개의 사진이 있는지 몰라서 임의로 50까지 설정함.
                        // -> 이 값을 어떻게 얻어오지?
                    storageRef.child(origin + "/" + i + ".png").delete();
                }

                //선택되어 있는 항목 db에서 제거
                childreference100.child(origin).removeValue();
                childreference10.child(origin).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {                    }
                });

               // finish();


                listdata.remove(checked);
                list.clearChoices();
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);


        list = (ListView) findViewById(R.id.listView);
        listdata = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listdata);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mISelectedItem=i;
                String selected_item = (String) adapter.getItem(i);
               /* Intent intent7 = new Intent(getApplicationContext(), EditActivity.class);
                intent7.putExtra("selected_item", selected_item);
                startActivity(intent7);
*/            }
        });

        childreference10.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {

                    String msg2 = messageData.getKey();
                    adapter.add(msg2);
                }
                adapter.notifyDataSetChanged();
                list.setSelection(adapter.getCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
