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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DataHelper extends SQLiteOpenHelper {
	private SQLiteDatabase db = null;
	
	
	private static final int DATABASE_VERSION = 3;
	
    static final String TABLE_NAME = "blood_glucose";
    private static final String TMP_TABLE_NAME = "blood_glucose_tmp";
	private static final String COLUMN1 = "date";
	private static final String COLUMN2 = "blood_glucose_level";
	private static final String COLUMN3 = "notes";
    private static final String TABLE_CREATE = " (" +  COLUMN1 +
                		" TEXT, " + COLUMN2 +" INTEGER, " + COLUMN3 + " TEXT);";
	private static final String DATABASE_NAME = "blood_glucose_monitor";
	
	
	
    DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        set_writeable_db();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            List<String> columns = get_columns(db, TABLE_NAME);
            db.execSQL("ALTER table " + TABLE_NAME + " RENAME TO '" + TMP_TABLE_NAME + "'");
            onCreate(db);
            columns.retainAll(get_columns(db, TABLE_NAME));
            String cols = join(columns, ","); 
            db.execSQL(String.format( "INSERT INTO %s (%s) SELECT %s from %s", TABLE_NAME, cols, cols, TMP_TABLE_NAME));
            db.execSQL("DROP table '" + TMP_TABLE_NAME + "'");
	}
	
	public List<String> get_columns(SQLiteDatabase db, String table_name) {
		
		List<String> columns = null;
		Cursor row = null;
		try {
	        row = db.rawQuery("select * from " + table_name + " limit 1", null);
	        if (row != null) {
	           columns  = new ArrayList<String>(Arrays.asList(row.getColumnNames()));
	        }
		} finally {
	        if (row != null)
	            row.close();
	    }
	    return columns;
	}
	
	public static String join(List<String> list, String delim) {
	    StringBuilder buf = new StringBuilder();
	    int num = list.size();
	    for (int i = 0; i < num; i++) {
	        if (i != 0)
	            buf.append(delim);
	        buf.append((String) list.get(i));
	    }
	    return buf.toString();
	}
	
	public String[] get_column_list() {
		return new String[] {COLUMN1, COLUMN2, COLUMN3 };
	}
	
	
	public void set_writeable_db() {
		db = getReadableDatabase();
		
	}
	
	protected Cursor get_glucose_history(String start_date, String end_date) {
		String selection = null;
		String[] selection_columns = null;
		if (start_date != null && end_date != null) { 
			selection = "strftime('%s', " + COLUMN1 + " ) >= strftime('%s','?s') " + 
    				" AND strftime('%s', " + COLUMN1 + ") <= strftime('%s', '?s')";
			selection_columns = new String[] {start_date, end_date};
		}
		else if (start_date != null ) {
			selection = "strftime('%s', "+ COLUMN1 + ") >= strftime('%s', '?s')";
			selection_columns = new String[] {start_date};
		}
		else if (end_date != null) {
			selection = "strftime('%s', " + COLUMN1 + ") <= strftime('%s', '?s')";
			selection_columns = new String[] {end_date};
		}

        String order = COLUMN1 + " DESC";
		return db.query(TABLE_NAME, new String[] {COLUMN1, COLUMN2, COLUMN3}, selection, selection_columns, 
																					null, null, order, null);
	}

	public int remove_row(SQLiteDatabase sql, String date, String blood_glucose) {
		String where_clause = COLUMN1 + " = ? AND " + COLUMN2 + " = ?";
		return sql.delete(TABLE_NAME, where_clause, new String[] {date, blood_glucose});
	}
	
	public String[][] get_data(String start_date, String end_date) {
		Cursor results = get_glucose_history(start_date, end_date);
		int num_rows = results.getCount();
		String[][] data = new String[num_rows][3];
		
		for (int x=0;x<num_rows;x++) {
			results.moveToNext();
			data[x][0] = results.getString(0);
			data[x][1] = results.getString(1);
			data[x][2] = results.getString(2);
		}
		results.close();
		
		return data;
    }
	
}
