package com.kyunggi.worker;
import android.util.*;
import java.io.*;

public class IOPPOutputVntSender extends IOPPOutputSender
{
	public IOPPOutputVntSender(BluetoothOPPBatch b)
	{
		super(b);
	}
	@Override
	public boolean Send(String s)
	{
		// TODO: Implement this method
		boolean success=SendStringVntViaOPP(s);
		return success;
	}
	
	private boolean SendStringVntViaOPP(String s)
	{
		// TODO: Implement this method
		String [] data=Utility.SplitStringByByteLength(s, "UTF-8",400);
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
	
	
}
