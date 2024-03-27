package com.kyunggi.worker;
import android.bluetooth.*;
import android.content.*;

public class BTOnOffreceiver extends BroadcastReceiver
{
	SharedPreferences setting;
	SharedPreferences.Editor editor;
	
    @Override
	public void onReceive(Context context, Intent intent)
	{
		setting = context.getSharedPreferences("setting", 0);
		//setting.getFloat(key, defValue);
		//setting.getInt(key, defValue);
		//setting.getLong(key, defValue);
		//setting.getString(key, defValue);
		editor = setting.edit();
		boolean bListenOnBTOn = setting.getBoolean("ListenOnBTEnabled", false);
		//boolean bListenNow = IsListenerRunning(context);
		
		final String action = intent.getAction();

		if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
		{
			final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
			switch (state)
			{
				case BluetoothAdapter.STATE_OFF:
					;
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					stopListenerService(context);
					break;
				case BluetoothAdapter.STATE_ON:
					if (bListenOnBTOn)
					{

						Intent i = new Intent(context, BluetoothOPPService.class);
						context.startService(i);
					} 
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					;
					break;
			}

		}
	}
	private void stopListenerService(Context c)
	{
		// TODO: Implement this method
		Intent i=new Intent(c, BluetoothOPPService.class);
		c.stopService(i);
	}
}
