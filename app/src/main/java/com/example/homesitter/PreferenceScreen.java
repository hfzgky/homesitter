package com.example.homesitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.EditText;

import androidx.preference.PreferenceManager;

public class PreferenceScreen extends PreferenceActivity {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    EditText editTextPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        editTextPreference = (EditText) findViewById(R.id.emergency) ;
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        String data = editTextPreference.getText().toString();
        Intent intent = new Intent(PreferenceScreen.this, MainActivity.class);
        intent.putExtra("emergency",data);
        startActivity(intent);
        boolean alr = pref.getBoolean("alert", true);
        Integer emernum = pref.getInt("emernum", 112);
    }
}