package com.kyunggi.worker;
import android.util.*;
import java.io.*;

public class IOPPOutputVntSender implements IOutputSender
{

	@Override
	public boolean SendError(String s)
	{
		// TODO: Implement this method
		return false;
	}
	
	public IOPPOutputVntSender(BluetoothOPPBatch b)
	{
		batch=b;
	}
	@Override
	public boolean Send(String s)
	{
		// TODO: Implement this method
		boolean success=SendStringVntViaOPP(s);
		return success;
	}

	@Override
	public boolean Send(File file)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean SendError(Throwable e)
	{
		// TODO: Implement this method
		return SendError("",e);
	}

	@Override
	public boolean SendError(String s, Throwable e)
	{
		// TODO: Implement this method
		return Send("error: "+s+Log.getStackTraceString(e));
	}
	
	private boolean SendStringVntViaOPP(String s)
	{
		// TODO: Implement this method
		String [] data=Utility.SplitString(s, 300);
		for (String str:data)
		{
			SendStringVntViaOPPSub(str);
		}
		try
		{
			batch.flush();
		}
		catch (IOException e)
		{
			return false;
		}
		return true;
	}
	private boolean SendStringVntViaOPPSub(String s)
	{
		// TODO: Implement this method
		String VntStr="BEGIN:VNOTE\nVERSION:1.1\nBODY;CHARSET=UTF-8:";
		VntStr += s;
		//VntStr+="DCREATED:20180129T175000\nLAST-MODIFIED:20180130T153100\nCLASS:PUBLIC\nEND:VNOTE";
		return batch.AddTransfer(VntStr, "LG-LV8500_MEMO_20180130153117_123.vnt");	
	}
	transient BluetoothOPPBatch batch;
	
}
