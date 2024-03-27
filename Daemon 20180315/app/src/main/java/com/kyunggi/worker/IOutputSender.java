package com.kyunggi.worker;

import android.util.*;
import java.io.*;

public abstract class IOutputSender
{
	private String TAG="BTOPP IOPSENDER";

	public void Send(BatchInfo[] getBatchInfo)
	{
		// TODO: Implement this method
		for(BatchInfo b:getBatchInfo)
		{
			Send(b);
		}
	}
//	public boolean Send(String as,TypeConverter.FILETYPE type,Object data)
//	{
//		// TODO: Implement this method
//		
//		return true;
//	}
	public boolean SendError(String s)
	{
		// TODO: Implement this method
		return Send("Error: "+s);
	}

	public IOutputSender(BluetoothBatch b)
	{
		batch=b;
	}
	public abstract boolean Send(String s);
	public boolean Send(String as,byte[] b)
	{
		return batch.AddTransfer(as,b);
	}
	public boolean Send(BatchInfo info)
	{
		return batch.AddTransfer(info);
	}
	public boolean Send(File file)
	{
		// TODO: Implement this method
		boolean s= batch.AddTransfer(file);
		/*try
		{
			batch.flush();
		}
		catch (IOException e)
		{
			s=false;
		}*/
		return s;
	}


	public boolean SendError(Throwable e)
	{
		// TODO: Implement this method
		return SendError("",e);
	}


	public boolean SendError(String s, Throwable e)
	{
		// TODO: Implement this method
		Log.e(TAG,s,e);
		return Send("error: ".concat(s).concat(Log.getStackTraceString(e)));
	}
	
	public boolean Send(TypeConverter.FILETYPE type)
	{
		return true;
		
	}

	transient BluetoothBatch batch;
}
