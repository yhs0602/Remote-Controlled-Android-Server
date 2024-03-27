package com.kyunggi.worker.ExternalProgram;

import android.util.*;
import java.io.*;

public class ProgramShell extends Program
{

	private String TAG;
	@Override
	public void run()
	{
		DataOutputStream os=null;
		// TODO: Implement this method
		try
		{
			Process shProcess;
			try
			{
				shProcess = Runtime.getRuntime().exec("sh");

				os = new DataOutputStream(shProcess.getOutputStream());
				DataInputStream osRes = new DataInputStream(shProcess.getInputStream());

				if (null != os && null != osRes)
				{
					// Getting the id of the current user to check if this is root
					while (true)
					{
						String cmd=readLine();
						os.writeBytes(cmd + "\n");
						os.flush();
						String answer="";
						while (osRes.available() > 0)
						{
							answer += osRes.readLine();
						}
						writeLine(answer).Flush();
					}			
				}
			}
			catch (IOException e)
			{
				
			}		
		}
		catch (InterruptedException e)
		{

		}
		try
		{
			os.writeBytes("exit\n");
			os.flush();
			os.close();
		}
		catch (Exception e)
		{}

	}
	public ProgramShell(IStringSender sender)
	{
		super(sender);
	}

}
