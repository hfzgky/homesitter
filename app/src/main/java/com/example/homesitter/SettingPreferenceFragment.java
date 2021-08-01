package com.example.homesitter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceScreen;

public class SettingPreferenceFragment extends PreferenceFragment {

    SharedPreferences prefs;

    ListPreference soundPreference;
    PreferenceScreen keywordScreen;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*        addPreferencesFromResource(R.xml.settings_preference);
        soundPreference = (ListPreference)findPreference("sound_list");
        https://hijjang2.tistory.com/135*/
    }
}
