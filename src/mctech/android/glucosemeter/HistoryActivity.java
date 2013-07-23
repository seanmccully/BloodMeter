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


import mctech.android.glucosemeter.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;
import android.graphics.Typeface;



public class HistoryActivity extends Activity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.history_main);
	   
	    TableLayout table_layout = (TableLayout)findViewById(R.id.tableLayout1);
	    make_history_table(table_layout);
	    
	  
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
            case R.id.main:
            	Intent main = new Intent(this, MainActivity.class);
            	this.startActivity(main);
            	this.finish();
            	return true;
            case R.id.front:
                Intent front = new Intent(this, FrontActivity.class);
                this.startActivity(front);
                this.finish();
                return true;
            case R.id.menu_settings:
                Intent settings = new Intent(this, Settings.class);
                this.startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    
    
	public void make_history_table(TableLayout table_layout) {
    	DataHelper data_helper = new DataHelper(this.getBaseContext());
    	String[][] data = data_helper.get_data(null, null);
    	data_helper.close();
	    int size = data.length;
	    if (size > 0) {
	    	for (int x=0;x<size;x++) {
	    		TableRow table_row = new TableRow(this);
	    		
	    		TextView text_view1 = new TextView(this);
	    		TextView text_view2 = new TextView(this);
	    		
	    		text_view1.setTypeface(null, Typeface.BOLD);
	    		text_view1.setText(data[x][1] + "   ");
	    		text_view2.setText(data[x][0]);
	    		
	    		text_view1.setLayoutParams(new LayoutParams(
	    				LayoutParams.MATCH_PARENT,
	    				LayoutParams.WRAP_CONTENT));
	    		text_view2.setLayoutParams(new LayoutParams(
	    				LayoutParams.MATCH_PARENT,
	    				LayoutParams.WRAP_CONTENT));
	    		
	    		text_view1.setTextSize((float)18);
	    		text_view2.setTextSize((float)18);
	    		
	    		table_row.addView(text_view1);
	    		table_row.addView(text_view2);
	    		
	    		table_row.setPadding(0, 30, 0, 0);
	    		

	    		table_row.setLayoutParams(new LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
	    		table_row.setBackgroundResource(R.drawable.cell_shape);
	    		
	    		table_row.setOnClickListener(new View.OnClickListener() {
	        		public void onClick(View v) {
	        			Context context = v.getContext();
	        			
	        			TableRowListener listener = new TableRowListener();
	        			listener.set_table_row((TableRow)v);
	        			AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        			builder.setMessage("Delete This Measurement?").setPositiveButton("Delete", listener)
	        			    .setNegativeButton("Cancel", listener).show();
	        		}
	    		});
	    		table_layout.addView(table_row);
	    		if (data[x][2] != null) {
	    			
	    			TableRow row_note = new TableRow(this);
	    			TextView text_view3 = new TextView(this);
	    			text_view3.setTextSize((float)18);
	    			text_view3.setText(data[x][2]);
	    			
	    			LayoutParams lp = new LayoutParams(
		    				LayoutParams.MATCH_PARENT,
		    				LayoutParams.WRAP_CONTENT);
		    		lp.span = 2;
		    		text_view3.setLayoutParams(lp);
		    		row_note.addView(text_view3);
		    		row_note.setPadding(1, 0, 1, 30);
		    		row_note.setBackgroundResource(R.drawable.cell_shape);
	    			table_layout.addView(row_note);
	    		}
	    	}
	    }
    }
    
    
    public class TableRowListener extends DialogFragment
    implements DialogInterface.OnClickListener {
    	TableRow table_row;
    	public void onClick(DialogInterface arg0, int arg1) {
    		switch (arg1){
        	case DialogInterface.BUTTON_POSITIVE:
        		
        		DataHelper data_helper = new DataHelper(table_row.getContext());
        		SQLiteDatabase sql = data_helper.getWritableDatabase();
        		int children = table_row.getChildCount();
        		if (children == 2) {
        			TextView text_view1 = (TextView) table_row.getChildAt(0);
        			TextView text_view2 = (TextView) table_row.getChildAt(1);
        			if (data_helper.remove_row(sql, (String) text_view2.getText(), ((String) text_view1.getText()).trim()) > 0) {
                		TableLayout table_layout = (TableLayout)table_row.getParent();
                		table_layout.removeView(table_row);
        			}
        		}
        		data_helper.close();
        		sql.close();
    		}
    	}

		public void set_table_row(TableRow table_row) {
			this.table_row = table_row;
		}
    	
    }
}
