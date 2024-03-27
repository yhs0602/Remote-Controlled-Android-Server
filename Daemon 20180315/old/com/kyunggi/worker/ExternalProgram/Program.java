package com.kyunggi.worker.ExternalProgram;
import java.util.concurrent.*;

import android.text.*;

public abstract class Program implements Runnable
{
	IStringSender output;
	public Program(IStringSender out)
	{
		output=out;
		thread=new Thread(this);
	}
	private ArrayBlockingQueue<String> stdin,stdout;
	private boolean isRunning=false;
	private Thread thread;
	protected String readLine() throws InterruptedException
	{
		checkrun();
		return stdin.remove();
	}
	protected Program writeLine(String s)
	{
		stdout.add(s);
		return this;
	}
	protected void Flush()
	{
		String s=TextUtils.join("\n",stdin);
		stdin.clear();
		output.SendString(s);
	}
	public void TypeLine(String s)
	{
		if(isRunning&&PreprocessCmd(s)==false)
		{
			stdin.add(s);
			synchronized(thread)
			{
				thread.notifyAll();
			}		
		}
	}

	private boolean PreprocessCmd(String s)
	{
		// TODO: Implement this method
		s=s.toLowerCase();
		if(s.compareTo("exit")==0)
		{
			OnDestroy();
			return true;
		}
		return false;
	}
	
	public void OnDestroy()
	{
		isRunning=false;
		thread.stop(); 	
	}
	public void Start()
	{
		isRunning=true;
		thread.start();
	}

	public void checkrun() throws InterruptedException
	{
		// TODO: Implement this method
		if(!isRunning)
		{
			throw new InterruptedException();
		}
	}
}
