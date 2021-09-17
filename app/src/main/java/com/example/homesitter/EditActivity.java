package com.example.homesitter;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;


public class EditActivity extends AppCompatActivity {
    private EditText mEditName, mEditPhone;
    private int mItem = -1; //인덱스
    ImageView imagePerson;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

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
    }
}