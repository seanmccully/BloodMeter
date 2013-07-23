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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import mctech.android.glucosemeter.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;





public class FrontActivity extends Activity {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private GraphView graph = null;
	private Date minDate = null;
	private Date maxDate = null;
	private int minLevel = 0;
	private int maxLevel = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.front_activity);
        
	    DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		graph = new GraphView(this, metrics);
        
        DataHelper data_helper = new DataHelper(getBaseContext());
        String[][] data = data_helper.get_data(null, null);
        data_helper.close();
	    int size = data.length;
	    if (size > 0) { 
	    	graph.init_data_point(size);
	        setMinMax(data);
	        graph.setOffsets(minDate.getTime(), maxDate.getTime(), minLevel, maxLevel);
	    }
	    else {
	    	
	    }
        
	    setContentView(graph);
    }
    
    
    
    private void setMinMax(String[][] data) {
    	int size = data.length;
        
    	try {
			minDate = dateFormat.parse(data[0][0].replace(" -", "-"));
			maxDate = dateFormat.parse(data[0][0].replace(" -", "-"));
		} catch (ParseException e) {
			minDate = new Date();
			maxDate = new Date();

		}
        
    	minLevel = Integer.parseInt(data[0][1]);
        maxLevel = Integer.parseInt(data[0][1]);
        
    	for (int x=0;x<size;x++) {
    		Date date = new Date();
    		boolean parseError = false;
    		try {
    			date = dateFormat.parse(data[x][0].replace(" -", "-"));
    			graph.add_data_point(date, Integer.parseInt(data[x][1]), x);
    		} catch (ParseException e) {
    			parseError = true;

    		}
    		if (Integer.parseInt(data[x][1]) > maxLevel) {
    			maxLevel = Integer.parseInt(data[x][1]);
    		}
    		if (minLevel > Integer.parseInt(data[x][1])) {
    			minLevel = Integer.parseInt(data[x][1]);
    		}
    		if (!parseError) {
    			if (maxDate.compareTo(date) < 0) {
	    			maxDate = date;
	    		}
    			if (minDate.compareTo(date) > 0) {
    				minDate = date;
    			}
    		}
    		
    	}
    	
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {   
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
            case R.id.main:
            	Intent main = new Intent(this, MainActivity.class);
            	this.startActivity(main);
            	this.finish();
            	return true;
            case R.id.menu_settings:
                Intent settings = new Intent(this, Settings.class);
                this.startActivity(settings);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    

    
}
