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


package mctech.android.glucosemeter;


import java.util.Calendar;
import mctech.android.glucosemeter.R;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;

import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;





public class MainActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
        	AlarmReceiver.cancel_notification(getBaseContext());
        }
        
    	int[] date = get_current_date();
    	int[] time = get_current_time();
    	
    	//
    	TextView res = set_textview(R.id.datetxt,  String.format("%d-%-2d-%02d", date[0], date[1], date[2]));
    	res.setOnTouchListener(new OnTouchListener() {
    		public boolean onTouch(View v, MotionEvent event) {
    			int action = event.getActionMasked();
    			if (action == MotionEvent.ACTION_UP) {
    				showDatePickerDialog(v);
    			}
				return true;
    			
    		}
    	});
    	
    	res = set_textview(R.id.hourtxt,  String.format("%02d:%02d", time[0], time[1]));
    	res.setOnTouchListener(new OnTouchListener() {
    		public boolean onTouch(View v, MotionEvent event) {
    			int action = event.getActionMasked();
    			if (action == MotionEvent.ACTION_UP) {
    				showTimePickerDialog(v);
    			}
				return true;
    			
    		}
    	});
    	
    	Button button = (Button)findViewById(R.id.savebtn);
    	button.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			Context context = v.getContext();
    			
    			TextView date = (TextView)findViewById(R.id.datetxt);
    			TextView time = (TextView)findViewById(R.id.hourtxt);
    			String full_datetime = String.format("%s %s:00", date.getText(), time.getText());
    			
    			
    			try {
    				EditText blood_level = (EditText)findViewById(R.id.glucoseLvl);
        			String glucose_str = blood_level.getText().toString();
        			EditText notes_txt = (EditText)findViewById(R.id.notesTxt);
        			String notes = notes_txt.getText().toString();
        			int glucose = 0;
    				glucose =Integer.parseInt(glucose_str);
    				if (glucose < 0) {
    					throw new NumberFormatException();
    				}
    				String message = String.format("Save Reading '%s' (%d)", full_datetime, glucose);
        			
        			DialogListener listener = new DialogListener();
        			listener.set_datetime(full_datetime);
        			listener.set_glucose(glucose);
        			listener.set_notes(notes);
        			listener.set_context(context);
        			
        			AlertDialog.Builder builder = new AlertDialog.Builder(context);
        			builder.setMessage(message).setPositiveButton("Yes", listener)
        			    .setNegativeButton("No", listener).show();
    			}
    			catch (NumberFormatException e) {
    				AlertDialog.Builder builder = new AlertDialog.Builder(context);
    				builder.setMessage("Enter a Valid Number").setCancelable(false)
    			       .setPositiveButton("OK", null).show();
  

    			}
    		
    			
    			
    		}
    	});
    }

    
    private TextView set_textview(int resource_id, CharSequence str) {
    	TextView res = (TextView)findViewById(resource_id);
    	res.setText(str);//res is null
    	return res;
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.history:
                Intent history = new Intent(this, HistoryActivity.class);
                this.startActivity(history);
                this.finish();
                return true;
            case R.id.front:
                Intent front = new Intent(this, FrontActivity.class);
        		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        		imm.hideSoftInputFromWindow(findViewById(R.id.main).getWindowToken(), 0);
                this.startActivity(front);
                this.finish();
                return true;
            case R.id.menu_settings:
                Intent settings = new Intent(this, Settings.class);
                this.startActivity(settings);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private static int[] get_current_date() {
    	final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1 ;
		int day = c.get(Calendar.DAY_OF_MONTH);
    	
		int[] date = {year, month, day};
		
		return date;
    }
 
    
    private static int[] get_current_time() {
    	final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
    	
		int[] time = {hour, minute};
		
		return time;
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
        
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

   

    
    public void showDatePickerDialog(View v) {
    	DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }
  
    
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    
   
    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment
    	implements TimePickerDialog.OnTimeSetListener {

    	
		@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		// Use the current time as the default values for the picker
    		int[] time = get_current_time();

    		// Create a new instance of TimePickerDialog and return it
    		return new TimePickerDialog(getActivity(), this, time[0], time[1],
    				DateFormat.is24HourFormat(getActivity()));
    	}

    	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    		set_textview(R.id.hourtxt,  String.format("%02d:%02d", hourOfDay, minute));
    	}
    	
    }

    public class DialogListener extends DialogFragment
    implements DialogInterface.OnClickListener {
    	String full_datetime;
    	String notes;
    	int glucose;
    	Context context;
    	
		public void onClick(DialogInterface arg0, int arg1) {
			switch (arg1){
	        	case DialogInterface.BUTTON_POSITIVE:
	        		DataHelper data_helper = new DataHelper(context);
	        		SQLiteDatabase sql = data_helper.getWritableDatabase();
	        		ContentValues values = new ContentValues();
	        		String[] columns = data_helper.get_column_list();
	        		values.put(columns[0], full_datetime);
	        		values.put(columns[1], glucose);
	        		values.put(columns[2], notes);
	        		sql.insert(DataHelper.TABLE_NAME, null, values);
	        		data_helper.close();
	        		sql.close();
	        		break;

	        	case DialogInterface.BUTTON_NEGATIVE:
	        		//No button clicked
	        		break;
	        	}
		}

		public void set_glucose(int glucose) {
			 this.glucose = glucose;
		}

		public void set_notes(String notes) {
			 this.notes = notes;
		}
		
		public void set_datetime(String full_datetime) {
			this.full_datetime = full_datetime;
		}
		
		public void set_context(Context context) {
			this.context = context;
		}
    }
    
    
    @SuppressLint("ValidFragment")
	public class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
    	
		@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		// Use the current date as the default date in the picker
    		int[] date = get_current_date();

    		// Create a new instance of DatePickerDialog and return it
    		return new DatePickerDialog(getActivity(), this, date[0], date[1]-1, date[2]);
    	}

    	public void onDateSet(DatePicker view, int year, int month, int day) {
    		 set_textview(R.id.datetxt,  String.format("%d-%02d-%02d", year, month+1, day));
        }

    }


}



