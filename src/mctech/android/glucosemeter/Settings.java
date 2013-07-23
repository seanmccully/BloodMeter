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

	
	}

