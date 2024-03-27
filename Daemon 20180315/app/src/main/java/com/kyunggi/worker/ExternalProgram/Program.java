package com.kyunggi.worker.ExternalProgram;
import android.text.*;
import android.util.*;
import com.kyunggi.worker.*;
import java.util.concurrent.*;

public abstract class Program implements Runnable
{
	WorkerSession session;

	private String TAG="BTOPP PROGRAM";
	public Program(WorkerSession sess)
	{
		session = sess;
		thread = new Thread(this);
	}
	private ArrayBlockingQueue<String> stdin=new ArrayBlockingQueue<String>(100),stdout=new ArrayBlockingQueue<String>(100);
	private boolean isRunning=false;
	private Thread thread;
	protected String readLine() throws InterruptedException
	{
		Log.v(TAG, "READLINE");
		checkrun();
		return stdin.take();
	}
	protected Program writeLine(String s)
	{
		stdout.add(s);
		return this;
	}
	protected void Flush()
	{
		String s=TextUtils.join("\n", stdout);
		stdout.clear();
		session.SendString(s);
	}
	public void TypeLine(String s)
	{
		Log.v(TAG, "TYPELINE " + s);
		if (isRunning && PreprocessCmd(s) == false)
		{
			stdin.add(s);
			Log.v(TAG, "TYPELINE DOBE");
			/*synchronized(thread)
			 {
			 //thread.notifyAll();
			 }*/		
		}
	}

	private boolean PreprocessCmd(String s)
	{
		// TODO: Implement this method
		s = s.toLowerCase();
		if (s.trim().compareTo("exit") == 0)
		{
			OnDestroy();
			return true;
		}
		return false;
	}

	public void OnDestroy()
	{
		if (isRunning)
		{
			isRunning = false;
			thread.interrupt();
			try
			{
				thread.join();
			}
			catch (InterruptedException e)
			{}
		}
		session.requestDestroy(this);
	}
	public void Start()
	{
		isRunning = true;
		thread.start();
		Log.v(TAG, "STARTING PROGRAM");
	}

	public void checkrun() throws InterruptedException
	{
		// TODO: Implement this method
		if (!isRunning)
		{
			throw new InterruptedException();
		}
	}
	public void SigKill()
	{
		OnDestroy();
	}
	protected void reportError(Throwable e)
	{
		writeLine(Log.getStackTraceString(e)).Flush();
		Log.e(TAG, "error ", e);
	}
	public String getStatus()
	{
		StringBuilder b=new StringBuilder();
		b.append(isRunning);
		Thread.State state= thread.getState();
		b.append(state.name());
		return b.toString();
	}

	protected abstract void runSub();
	@Override
	public final void run()
	{
		// TODO: Implement this method
		try
		{
			runSub();
			Flush();
			OnDestroy();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			OnDestroy();
		}
	}
    protected void exit()
	{
		OnDestroy();
	}
}
