package mctech.android.glucosemeter;


import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;




public class GraphView extends View {
	private double parentWidth = 0;
	private double parentHeight = 0;
	private double startWidth = 0;
	private double startHeight = 0;
	private double ymin = 0;
	private double ymax = 0;
	private double xmin = 0;
	private double xmax = 0;
	private DataPoint[] dataPoints = null;	
	private ShapeDrawable mDrawable = null;
	private static Paint paint = new Paint();
	private static int label_offset = 2;
	private DisplayMetrics metrics;
	private String prev_label = "00-00-0000";


	public GraphView(Context context, DisplayMetrics metrics) {
		super(context);
		this.metrics = metrics;
        mDrawable = new ShapeDrawable(new RectShape());
    }

	private double determine_xcord(double x_value) {
		double x_point = 0;
		if (xmax == xmin) {
			x_point = ((parentWidth - startWidth) / 2) + startWidth;
		}
		else {
			x_point = ((x_value - xmin) * ((parentWidth - startWidth) / (xmax - xmin))) + startWidth;
		}
		return x_point;
	}

	private double determine_ycord(double y_value) {
		double y_point = (y_value - ymin) * (startHeight / (ymax - ymin));
		return y_point;// + (y_point * 0.10);
	}
	
	
	private float sizeof_string(int size, String str) {
	
		float[] widths = new float[size];
		float total = 0;
		paint.getTextWidths(str, widths);
		for (int x=0;x<size;x++) {
			total += widths[x];
		}
		return total;
	}
	
	private void resize_font_y_labels() {
		String y_label = Integer.toString((int)ymax);
		float y_offset = sizeof_string(y_label.length(), y_label);
		float x_cord = (float)(startWidth * 0.90) - y_offset;
		if (x_cord < (y_offset / y_label.length())) {
			paint.setTextSize(paint.getTextSize() - 1);
			resize_font_y_labels();
		}
		else if (x_cord > y_offset) {
			paint.setTextSize(paint.getTextSize() + 1);
			resize_font_y_labels();
		}
	}
	
	private void resize_font_x_labels() {
		Rect rect = new Rect();
		
		determine_label_height(prev_label, rect);
		
		if ((rect.right + rect.left + startHeight) > parentHeight) {
			paint.setTextSize(paint.getTextSize() - 1);
			resize_font_x_labels();
		}
		
	}
	
	
	private void draw_y_labels(Canvas canvas) {
		resize_font_y_labels();
		double curr_y_value = ymin;
		String y_label = Integer.toString((int)curr_y_value);
		Rect rect = new Rect();
		int label_width = determine_label_height(y_label, rect);
		float y_offset = sizeof_string(y_label.length(), y_label);
		int range = (int)(startHeight / (label_width * label_offset));
		int y_increment = (int)((ymax - ymin) / range);
		if (y_increment == 0) {
			y_increment = 1;
		}
		while (curr_y_value < ymax) {
			y_label = Integer.toString((int)curr_y_value);
			y_offset = sizeof_string(y_label.length(), y_label);
			float y_cord = (float)determine_ycord(curr_y_value);
			float x_cord = (float)(startWidth * 0.90) - y_offset;
			canvas.drawText(y_label, x_cord, y_cord, paint);
			canvas.drawLine(x_cord + (y_offset / 2), y_cord + 1, x_cord + (float)(y_offset * 1.5), y_cord + 1, paint);
			
			curr_y_value += y_increment;
		}
	}
	
	
	private int determine_label_height(String label, Rect rect) {
		paint.getTextBounds(label, 0, label.length() , rect);
		return rect.height();
	}
	
	 
	@SuppressWarnings("deprecation")
	private void draw_x_labels(Canvas canvas) {
		resize_font_x_labels();
		Rect rect = new Rect();
		int label_width = determine_label_height(prev_label, rect);
		int range = (int)((parentWidth - startWidth) / (label_width * label_offset));
		long x_interval = ((long)xmax - (long)xmin) / range;
		if (x_interval == 0) {
			range = 1;
		}
		for (int x=1;x<=range;x++) {
			canvas.save();
			long x_value = (long)xmin + (x_interval * x);
			Date date = new Date(x_value);
			String label = (date.getMonth() + 1) + "-" + date.getDate() + "-" + (date.getYear() + 1900);
			if (!label.startsWith("1")) {
				label = "0" + label;
			}
			float x_cord = (float)determine_xcord(x_value);
			int[] colors = determine_color_point(x_value);
        	paint.setARGB(colors[0], colors[1], colors[2], colors[3]);
			if (prev_label != null && label.compareTo(prev_label) == 0) {
				label = date.getHours() + ":" + date.getMinutes();
				String[] labels = label.split(":");
				if (labels[0].length() == 1) {
					labels[0] = "0" + labels[0];
				}
				if (labels[1].length() == 1) {
					labels[1] = "0" + labels[1];
				}
				label = labels[0] + ":" + labels[1];
				paint.getTextBounds(label, 0, label.length() , rect);
				rect.height();
				canvas.rotate(90, x_cord, (float)(startHeight + paint.getTextSize()  + rect.exactCenterY()));
				canvas.drawText(label, x_cord, (float)startHeight + paint.getTextSize(), paint);
			}
			else { 
				paint.getTextBounds(label, 0, label.length() , rect);
				canvas.rotate(90, x_cord, (float)(startHeight + paint.getTextSize() + rect.exactCenterY()));
				canvas.drawText(label, x_cord, (float)startHeight + paint.getTextSize(), paint);
				prev_label = label;
			}
			canvas.restore();
		}
		paint.reset();
	}
	
	private int[] determine_color_point(double x_cord) {
		int a = 255;
        int r = 255;
        int g = 255;
        int b = 255;
        double offset = (x_cord - xmin) / (xmax - xmin);
        if (Double.isNaN(offset) || Double.isInfinite(offset)) {
        	offset = 0;
        	r = 0;
        }
        else if (offset < 0.50) {
        	r = 255;
        } 
        else {
        	r = 0;
        }
		g = (int)( g * offset );
		b = (int)( b * offset );
        return new int[]{a, r, g, b};
	}
	
    protected void onDraw(Canvas canvas) {
        mDrawable.getPaint().setColor(0x000000);
        mDrawable.setBounds(0, 0, (int)parentWidth, (int)parentHeight); 
        canvas.drawLine((float)startWidth, (float)startHeight, (float)startWidth, 0, paint);
        canvas.drawLine((float)startWidth, (float)startHeight, (float)parentWidth,  (float)startHeight, paint);
        if (dataPoints != null) {
        	draw_data(canvas);
        }
        
    	mDrawable.draw(canvas);
    }
	
    private void draw_data(Canvas canvas) {
    	paint.setTextScaleX((float) 1.30);
    	draw_y_labels(canvas);
    	paint.setTextScaleX((float) 1.0);
        draw_x_labels(canvas);
        draw_data_points(canvas);

    }
    
    private void draw_data_points(Canvas canvas) {
        paint.setStrokeWidth(12);
        for (int x=0;x<dataPoints.length;x++) {
        	int[] colors = determine_color_point(dataPoints[x].x_cord);
        	paint.setARGB(colors[0], colors[1], colors[2], colors[3]);
        	canvas.drawPoint((float)determine_xcord(dataPoints[x].x_cord), (float)determine_ycord(dataPoints[x].y_cord), paint);
        }
        paint.reset();
    }
    
    public void init_data_point(int size) {
    	dataPoints = new DataPoint[size];
    }
    
    public void add_data_point(Date date, int level, int x) {
    	dataPoints[x] = new DataPoint(date.getTime(), level);
    }
    
    public void setOffsets(double min_x, double max_x, double min_y, double max_y) {
    	xmin = min_x;//d - (min_x * 0.10);
    	xmax = max_x;// + (max_x * 0.10);
    	ymin = min_y - (min_y * 0.10);
    	ymax = max_y + (max_y * 0.10);
    }
    
 
	@SuppressLint("DrawAllocation")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		parentWidth = metrics.widthPixels;
		startWidth = (int)(parentWidth * 0.10);
		parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		startHeight = (int)(parentHeight * 0.85);
		

		this.setMeasuredDimension((int)parentWidth, (int)parentHeight);
		this.setLayoutParams(new FrameLayout.LayoutParams((int)parentWidth, (int)parentHeight));
		
		parentWidth = (int)(parentWidth * 0.95);
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}


	private class DataPoint {
		public double x_cord;
		public double y_cord;
	
		
		public DataPoint(float x_cord, float y_cord) {
			this.x_cord = x_cord;
			this.y_cord = y_cord;
		}
		
	}
}
