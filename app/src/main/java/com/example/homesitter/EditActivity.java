package com.example.homesitter;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;



public class EditActivity extends AppCompatActivity {
    private EditText mEditName, mEditPhone;
    private ImageButton mImagePerson;
    private int mItem = -1; //인덱스
    ImageView btnSave;
    ImageButton imagePerson;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mEditName = findViewById(R.id.editTextName);
        mEditPhone = findViewById(R.id.editTextPhone);
        mImagePerson = findViewById(R.id.imagePerson);

        Intent intent = getIntent();    //intent객체를 가져옴
        if (intent != null) {    //추가
            mItem = intent.getIntExtra("item", -1);

            if (mItem != -1) {   //수정
                //값을 가져와서 보여줌
                mEditName.setText(intent.getStringExtra("name"));
                mEditPhone.setText(intent.getStringExtra("phone"));
            }
        }

        //이미지를 클릭하면 갤러리에서 이미지를 가져옴
        imagePerson = findViewById(R.id.imagePerson); // 이미지 객체를 얻어옴
        imagePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);   //이미지를 가져오기 위해 암시적 인텐트 사용
                intent.setType("image/*");       //모든 형태의 이미지
                startActivityForResult(intent, 101);
            }
        });

        //취소버튼
        ImageButton btnBack;
        btnBack = findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //저장버튼
        btnSave = findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sName = mEditName.getText().toString().trim();
                String sPhone = mEditPhone.getText().toString().trim();

                if(sName.isEmpty() || sPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("item", mItem);
                intent.putExtra("name", sName);
                intent.putExtra("phone", sPhone);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101)  {
            if(resultCode == RESULT_OK) {
                ContentResolver contentResolver = getContentResolver();
                try {
                    InputStream inputStream = contentResolver.openInputStream(data.getData());    //파일 내용을 읽어옴
                    Bitmap image = BitmapFactory.decodeStream(inputStream);
                    imagePerson.setImageBitmap(image);   //선택한 이미지를 화면에 보여줌
                } catch (FileNotFoundException e) {     //오류가 생길경우 토스트창으로 알려줌
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "이미지로드 오류", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}