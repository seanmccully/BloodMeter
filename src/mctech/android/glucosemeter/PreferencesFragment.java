//(c) 2012 Sean McCully
//#   Licensed under the Apache License, Version 2.0 (the "License"); you may
//#   #   not use this file except in compliance with the License. You may obtain
//#   #   a copy of the License at
//#   #
//#   #       http://www.apache.org/licenses/LICENSE-2.0
//#   #
//#   #   Unless required by applicable law or agreed to in writing, software
//#   #   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
//#   #   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
//#   #   License for the specific language governing permissions and limitations
//#   #   under the License.
//# 
//
//
//
package mctech.android.glucosemeter;

/**
 * Created by smccully on 7/22/13.
 */

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * This fragment shows the preferences for the second header.
 */
public class PreferencesFragment extends PreferenceFragment {
    private Context context;
    private static String am_key = "am_time";
    private static String pm_key = "pm_time";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        if (this.context != null) {
            set_preference_listener("checkboxam_preference");
            set_preference_listener("checkboxpm_preference");
        }
    }


    public void set_preference_listener(String pref_str) {
        PreferenceListener listener = new PreferenceListener();
        listener.set_context(this.context);
        //listener.set_bundle(savedInstanceState);
        Preference pref = findPreference(pref_str);
        pref.setOnPreferenceClickListener(listener);
    }

    public void onAttach (Activity activity) {
        super.onAttach(activity);
        this.context = activity.getBaseContext();
    }


    public class PreferenceListener extends DialogFragment
            implements Preference.OnPreferenceClickListener {

        private Context context;
        private Bundle saved_bundle;

        public boolean onPreferenceClick(Preference preference) {

            PreferenceManager pref = preference.getPreferenceManager();
            TimePreference dp = (TimePreference)pref.findPreference("checkbox_dialog");

            if (((CheckBoxPreference) preference).isChecked()) {
                if (preference.getKey().equalsIgnoreCase("checkboxam_preference")) {
                    dp.set_am(true);
                    dp.setKey(am_key);
                }
                else {
                    dp.set_am(false);
                    dp.setKey(pm_key);
                }
                dp.show();
                return true;
            }
            else {
                dp.cancelAlarm();
                return false;
            }
        }

        public void set_bundle(Bundle savedInstanceState) {
            this.saved_bundle = savedInstanceState;

        }

        public void set_context(Context context) {
            this.context = context;
        }

    }
}
