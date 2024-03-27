package com.kyunggi.worker.ExternalProgram;
import com.kyunggi.worker.*;

public class ProgramMail extends Program
{
	public void runSub()
	{
		// TODO: Implement this method
		try
		{
			writeLine("Email addr pass to sub endmark").Flush(); //and mode wa");
			String account=readLine();
			String pass=readLine();
			String to=readLine();
			String subject=readLine();
			String endmark=readLine();
			String body="";
			StringBuilder b=new StringBuilder();
			while (true)
			{
				String s=readLine();
				if(s==null)
					break;
				if(s.trim().compareTo(endmark.trim())==0)
				{
					break;
				}
				b.append(s+"\n");
			}
			body=b.toString();
			try{
				Utility.SendMail(account,pass,to,subject,body);
			}catch(RuntimeException e)
			{
				reportError(e);
			}
			writeLine("Finish").Flush();
		}
		catch (InterruptedException e)
		{

		}
		finally
		{
		}
		return ;
	}
	public ProgramMail(WorkerSession s)
	{
		super(s);
	}
}
