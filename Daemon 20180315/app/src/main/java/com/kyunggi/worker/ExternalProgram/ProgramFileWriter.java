package com.kyunggi.worker.ExternalProgram;
import com.kyunggi.worker.*;
import java.io.*;

public class ProgramFileWriter extends Program
{

	@Override
	protected void runSub()
	{
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		// TODO: Implement this method
		try
		{
			writeLine("Enter Full file path..").Flush(); //and mode wa");
			String filename=readLine();
			try
			{
				fos=new FileOutputStream(new File(filename));
				dos= new DataOutputStream(fos);
			}
			catch (FileNotFoundException e)
			{
				reportError(e);
			    exit();
			}
			while (true)
			{
				String s=readLine();
				try
				{
					dos.writeChars(s+"\n");
				}
				catch (IOException e)
				{
					reportError(e);
				}

			}
		}
		catch (InterruptedException e)
		{
			
		}
		finally
		{
			try
			{
				dos.close();
			}
			catch (Exception e)
			{}
			try
			{
				fos.close();
			}
			catch (Exception e)
			{}
		}
	}
	public ProgramFileWriter(WorkerSession sess)
	{
		super(sess);
	}
}
