package mctech.android.glucosemeter;


import java.util.List;
import mctech.android.glucosemeter.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;



public class Settings extends PreferenceActivity  {
	private static String am_key = "am_time";
	private static String pm_key = "pm_time";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

	}
	
	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_headers, target);
	
	}

	
	
	
    /**
     * This fragment shows the preferences for the second header.
     */
    public static class PreferencesFragment extends PreferenceFragment {
        private Context context;
        
        
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
   
    

}

