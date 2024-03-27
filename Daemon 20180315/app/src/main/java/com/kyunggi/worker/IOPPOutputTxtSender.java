package com.kyunggi.worker;
import android.util.*;
import java.io.*;

public class IOPPOutputTxtSender extends IOPPOutputSender
{
	private String TAG="BTOPP OPPOUTPUTTXTSENDER";
	
	@Override
	public boolean Send(String s)
	{
		boolean success=SendStringViaOPP(s);
		return success;
	}
	
	public IOPPOutputTxtSender(BluetoothOPPBatch b)
	{
		super(b);
	}

	private boolean SendStringViaOPP(String s)
	{	
		boolean success=batch.AddTransfer(s,"response.txt");
		/*try
		{
			batch.flush();
		}
		catch (IOException e)
		{
			success=false;
		}*/
		return success;
	}	
}
