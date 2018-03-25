package com.kyunggi.worker2;
import android.app.*;
import android.content.*;
import android.widget.*;
import android.bluetooth.*;

public class BootReceiver extends BroadcastReceiver
{
	SharedPreferences setting;
	SharedPreferences.Editor editor;

	@Override
	public void onReceive(Context context, Intent intent)
	{

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
		{
			setting = context.getSharedPreferences("setting", 0);
			//setting.getFloat(key, defValue);
			//setting.getInt(key, defValue);
			//setting.getLong(key, defValue);
			//setting.getString(key, defValue);
			editor = setting.edit();
			boolean bListenOnBoot = setting.getBoolean("ListenOnBoot", false);
			boolean bListenNow = IsListenerRunning(context);
			boolean bton=BluetoothAdapter.getDefaultAdapter().isEnabled();
			Toast.makeText(context, "Boot finish!!!", 1).show();
			if (bListenOnBoot&&!bListenNow&&bton)
			{
				Intent i = new Intent(context, BluetoothOPPService.class);
				context.startService(i);
			} 
		}

	}	
	private boolean IsListenerRunning(Context context)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
            if ("com.kyunggi.worker2.BluetoothOPPService".equals(service.service.getClassName()))
			{
                return true;
            }
        }
        return false;
	}
}
