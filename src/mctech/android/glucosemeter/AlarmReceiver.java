package mctech.android.glucosemeter;

import mctech.android.glucosemeter.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AlarmReceiver extends BroadcastReceiver {
	private static final int NOTIFICATION_ID = 1;
	
	public static void cancel_notification(Context context) {
		NotificationManager notifications = (NotificationManager)
		        context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifications.cancel(NOTIFICATION_ID);
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		long[] vibrate = { 100, 100, 200, 300 };
		
		RemoteViews notification_view = new RemoteViews(context.getPackageName(), R.layout.notification);
		notification_view.setImageViewResource(R.id.image, R.drawable.ic_launcher);
		notification_view.setTextViewText(R.id.title, "Blood Meter");
		notification_view.setTextViewText(R.id.text, "Click to record your glucose!!");
		Intent main = new Intent(context, MainActivity.class);
		main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		main.setAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, main, 0);

		NotificationManager notifications = (NotificationManager)
		        context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = new Notification();
		notification.contentIntent = contentIntent;
		notification.contentView = notification_view;
		notification.tickerText = (CharSequence)"Blood Meter";
		notification.vibrate = vibrate;
		notification.defaults =Notification.DEFAULT_ALL;
		notification.icon = R.drawable.ic_launcher;
		notifications.notify(NOTIFICATION_ID, notification);
		//context.startActivity(main);
		
	}

}
