/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gzr.wolvesden.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;

import java.util.List;
import java.util.ArrayList;

import com.android.settings.R;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.Utils;

import com.android.settings.search.Indexable;
import com.android.settings.search.BaseSearchIndexProvider;

import com.android.internal.util.gzosp.GzospUtils;

public class Button extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, Indexable {

    private static final String CATEGORY_KEYS = "button_keys";
    private static final String KEYS_SHOW_NAVBAR_KEY = "navigation_bar_show";
    private static final String KEYS_DISABLE_HW_KEY = "hardware_keys_disable";

    private SwitchPreference mEnableNavBar;
    private SwitchPreference mDisabkeHWKeys;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.button);

        final ContentResolver resolver = getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        final int deviceKeys = getResources().getInteger(
                com.android.internal.R.integer.config_deviceHardwareKeys);
        final PreferenceCategory keysCategory =
                (PreferenceCategory) prefScreen.findPreference(CATEGORY_KEYS);

        if (deviceKeys == 0) {
            prefScreen.removePreference(keysCategory);
        } else {
            mEnableNavBar = (SwitchPreference) prefScreen.findPreference(
                   KEYS_SHOW_NAVBAR_KEY);

            mDisabkeHWKeys = (SwitchPreference) prefScreen.findPreference(
                    KEYS_DISABLE_HW_KEY);

            boolean showNavBarDefault = GzospUtils.deviceSupportNavigationBar(getActivity());
            boolean showNavBar = Settings.System.getInt(resolver,
                        Settings.System.NAVIGATION_BAR_SHOW, showNavBarDefault ? 1:0) == 1;
            mEnableNavBar.setChecked(showNavBar);

            boolean harwareKeysDisable = Settings.System.getInt(resolver,
                        Settings.System.HARDWARE_KEYS_DISABLE, 0) == 1;
            mDisabkeHWKeys.setChecked(harwareKeysDisable);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == mEnableNavBar) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_SHOW, checked ? 1:0);
            // remove hw button disable if we disable navbar
            if (!checked) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.HARDWARE_KEYS_DISABLE, 0);
                mDisabkeHWKeys.setChecked(false);
            }
            return true;
        } else if (preference == mDisabkeHWKeys) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.HARDWARE_KEYS_DISABLE, checked ? 1:0);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.VALIDUS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        return true;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    final Resources res = context.getResources();
                    final int deviceKeys = res.getInteger(
                            com.android.internal.R.integer.config_deviceHardwareKeys);

                    if (deviceKeys == 0) {
                        result.add(CATEGORY_KEYS);
                    }
                    return result;
                }
            };

}

