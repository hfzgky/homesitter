package com.example.homesitter;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;


public class EditActivity extends AppCompatActivity {
    private EditText mEditName, mEditPhone;
    private static final int REQUEST_CODE = 0;
    private int mItem = -1; //인덱스
    private ImageButton imgbtn;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        imgbtn = findViewById(R.id.imagePerson);
        mEditName = findViewById(R.id.editTextName);
        mEditPhone = findViewById(R.id.editTextPhone);

        Intent intent = getIntent();    //intent객체를 가져옴
        if (intent != null) {    //추가
            mItem = intent.getIntExtra("item", -1);

            if (mItem != -1) {   //수정
                //값을 가져와서 보여줌
                mEditName.setText(intent.getStringExtra("name"));
                mEditPhone.setText(intent.getStringExtra("phone"));
            }
        }
        ImageButton btnBack;
        btnBack = findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView btnSave;
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

        imgbtn.setOnClickListener(new View.OnClickListener() { //갤러리 가기
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    imgbtn.setImageBitmap(img);
                } catch (Exception e) {

                }
            }

            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }
}