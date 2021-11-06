package com.example.homesitter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.preference.PreferenceManager;

public class PreferenceScreen extends PreferenceActivity {
    SharedPreferences pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean alert = pref.getBoolean("alert", true);
        Integer emernum = pref.getInt("emernum", 112);
    }
}