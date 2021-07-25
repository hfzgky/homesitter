package com.example.homesitter;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.preference.Preference;

public class Preferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}