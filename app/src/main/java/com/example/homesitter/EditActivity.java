package com.example.homesitter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditActivity extends AppCompatActivity {
    private EditText mEditName, mEditPhone;
    private int mItem = -1; //인덱스
    ImageView imagePerson;


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
    }
}