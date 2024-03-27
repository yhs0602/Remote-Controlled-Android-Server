package com.kyunggi.worker.ExternalProgram;

import android.util.*;
import java.io.*;
import com.kyunggi.worker.*;

public class ProgramShell extends Program
{

	private String TAG="BTOPP SHELL";
	public void runSub()
	{
		DataOutputStream os=null;
		DataInputStream osRes = null;
		//DataInputStream osErr = null;
		Process shProcess = null;
		// TODO: Implement this method
		try
		{		
			try
			{
				//shProcess = Runtime.getRuntime().exec("sh");
				ProcessBuilder builder = new ProcessBuilder("sh");
				builder.redirectErrorStream(true);
				shProcess = builder.start();
				os = new DataOutputStream(shProcess.getOutputStream());
				osRes = new DataInputStream(shProcess.getInputStream());
				BufferedReader reader = new BufferedReader (new InputStreamReader(osRes));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
				
				// osErr = new DataInputStream(shProcess.getErrorStream());

				if (null != os && null != osRes)
				{							
					// Getting the id of the current user to check if this is root
					while (true)
					{
						String cmd=readLine();		
						Log.v(TAG,"READLINE DONE:"+cmd);
						if (cmd.trim().equals("exit")) {
							// Putting 'exit' amongst the echo --EOF--s below doesn't work.
							writer.write("exit\n");
						} else {
							writer.write("((" + cmd + ") && echo --EOF--) || echo --EOF--\n");
						}
						writer.flush();
						
						//os.writeBytes(cmd + "\n");
						//os.flush();
						String answer="";
						//String error="";
						String tmp;
						Log.d(TAG,"DOING");
						int i=0;
						tmp = reader.readLine();
						while (tmp != null && ! tmp.trim().equals("--EOF--")) {
							answer+=tmp;//System.out.println ("Stdout: " + tmp);
							Log.d(TAG,""+tmp);
							tmp = reader.readLine();
						}
						if (tmp == null) {
							break;
						}
					/*	while ((tmp=osRes.readLine())!=null&&tmp.compareToIgnoreCase("")!=0&&i<50)
						{
							Log.d(TAG,tmp);
							answer += tmp;
							++i;
							break;
						}*/
						/*while ((tmp=osErr.readLine())!=null)
						{
							Log.d(TAG,tmp);
							error += tmp;
						}*/
					//	Log.d(TAG,"=);");
						writeLine("Response:");
						writeLine(answer).Flush();
						//writeLine("Error:");
						//writeLine(error).Flush();
					}			
				}
			}
			catch (IOException e)
			{
				reportError(e);
			}
			finally{
				try
				{
					osRes.close();
				}
				catch (IOException e)
				{}
				/*try
				{
					osErr.close();
				}
				catch (IOException e)
				{}*/

			}
		}
		catch (InterruptedException e)
		{

		}
		finally
		{
			try
			{
				os.writeBytes("exit\n");
				os.flush();
				os.close();
				shProcess.waitFor();
			}
			catch (Exception e)
			{
				writeLine(Log.getStackTraceString(e)).Flush();
			}
		}

	}
	public ProgramShell(WorkerSession sender)
	{
		super(sender);
	}
}
