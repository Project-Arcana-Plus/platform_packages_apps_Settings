/*
 * Copyright (C) 2019-2021 The ConquerOS Project
 * Copyright (C) 2021 xdroid, xyzprjkt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arcana.grimoire.fragments;

import android.os.Bundle;
import android.content.Context;
import android.content.ContentResolver;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;

import android.widget.Toast;

import com.android.internal.util.arcana.ArcanaUtils;

import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class SystemSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TORCH_POWER_BUTTON_GESTURE = "torch_power_button_gesture";
    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";

    private ListPreference mTorchPowerButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
        
        addPreferencesFromResource(R.xml.grimoire_system);
        
       // screen off torch
        mTorchPowerButton = (ListPreference) findPreference(TORCH_POWER_BUTTON_GESTURE);
        int mTorchPowerButtonValue = Settings.System.getInt(resolver,
                Settings.System.TORCH_POWER_BUTTON_GESTURE, 0);
        mTorchPowerButton.setValue(Integer.toString(mTorchPowerButtonValue));
        mTorchPowerButton.setSummary(mTorchPowerButton.getEntry());
        mTorchPowerButton.setOnPreferenceChangeListener(this);
        
        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!ArcanaUtils.isVoiceCapable(getActivity())) {
                prefSet.removePreference(incallVibCategory);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARCANA;
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mTorchPowerButton) {
            int mTorchPowerButtonValue = Integer.valueOf((String) objValue);
            int index = mTorchPowerButton.findIndexOfValue((String) objValue);
            mTorchPowerButton.setSummary(
                    mTorchPowerButton.getEntries()[index]);
            Settings.System.putInt(resolver, Settings.System.TORCH_POWER_BUTTON_GESTURE,
                    mTorchPowerButtonValue);
            return true;
        }
        return false;
    }
    
    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.grimoire_system);

} 
