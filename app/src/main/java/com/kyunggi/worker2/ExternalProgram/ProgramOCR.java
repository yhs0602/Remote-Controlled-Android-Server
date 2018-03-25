package com.kyunggi.worker2.ExternalProgram;
import com.kyunggi.worker2.*;

public class ProgramOCR extends Program
{

	@Override
	protected void runSub()
	{
		// TODO: Implement this method
		try
		{
			writeLine("Enter Full file path..").Flush(); //and mode wa");
			String filename=readLine();
			String str=Utility.getStringFromImageFile(filename).trim();
			writeLine(str+"QAZWSXEDCconfirm/edit/QAZWSXEDCEnd").Flush();
			setExitSequence(null);
			String endmark="QAZWSXEDCEnd";
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
			str=b.toString();
			SetResult(new WorkerOperation(null,WorkerOperation.DATATYPE.STRING,str));
		}
		catch(InterruptedException e)
		{
			
		}
		return ;
	}
	public ProgramOCR(WorkerSession sess)
	{
		super(sess);
	}
	
}
