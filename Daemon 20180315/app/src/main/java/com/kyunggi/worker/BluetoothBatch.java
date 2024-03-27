package com.kyunggi.worker;

import android.bluetooth.*;
import android.util.*;
import com.kyunggi.worker.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.obex.*;



public abstract class BluetoothBatch
{

	private String TAG="BTOPP BATCH";

	protected BluetoothDevice device;
	protected /*BluetoothConnector.*/BluetoothSocket/*Wrapper*/ mBtSocket;
	protected final UUID OPPUUID=UUID.fromString(("00001105-0000-1000-8000-00805f9b34fb"));

	public BluetoothBatch(BluetoothDevice dev)
	{
		device = dev;
		sendQueue = new LinkedBlockingQueue<BatchInfo>();
	}

	public boolean AddTransfer(BatchInfo info)
	{
		// TODO: Implement this method
		return sendQueue.add(info);
	}
	public abstract ClientSession StartBatch(int n);

	protected boolean Put(ClientSession session, BatchInfo info)
	{
		// TODO: Implement this method
		byte [] bytes;
		String filename=info.filename;
		if (info.bytes == null)
		{
			Log.v(TAG,"Info.bytes null");
			if (info.isfile)
			{
				try
				{
					bytes = Utility.readFully(info.file.getCanonicalPath());
				}
				catch (IOException e)
				{
					return false;
				}
			}
			else
			{
				try
				{
					bytes = info.s.getBytes(new Random().nextBoolean() ?"EUC-KR": "UTF-8");
				}
				catch (UnsupportedEncodingException e)
				{
					Log.e(TAG, "", e);
					return false;
				}
			}
		}
		else
		{
			bytes = info.bytes;
		}


		return Put(session, bytes, filename);
		/*	boolean retry=true;
		 int times=0;
		 while (retry && times < 4)
		 {		
		 Operation putOperation=null;
		 OutputStream mOutput = null;
		 //ClientSession mSession = null;
		 //ArrayUtils.reverse(bytes);
		 try
		 {	
		 // Send a file with meta data to the server
		 final HeaderSet hs = new HeaderSet();
		 hs.setHeader(HeaderSet.NAME, filename);
		 hs.setHeader(HeaderSet.TYPE, Utility.getMimeType(filename));
		 hs.setHeader(HeaderSet.LENGTH, new Long((long)bytes.length));

		 putOperation = session.put(hs);

		 mOutput = putOperation.openOutputStream();
		 mOutput.write(bytes);
		 mOutput.close();
		 putOperation.close();
		 }
		 catch (Exception e)
		 {
		 Log.e(TAG, "put failed",e);
		 retry = true;
		 times++;
		 continue;
		 //e.printStackTrace();
		 }
		 finally
		 {
		 try
		 {
		 mOutput.close();
		 putOperation.close();
		 }
		 catch (Exception e)
		 {
		 Log.e(TAG, "put " ,e);
		 retry = true;
		 times++;
		 continue;
		 }
		 //updateStatus("[CLIENT] Connection Closed");
		 }
		 retry = false;
		 return true;
		 }
		 return false;*/
	}
	protected boolean Put(ClientSession session, byte [] bytes, String as)
	{
		return Put(session, bytes, as, Utility.getMimeType(as));
	}
	protected boolean Put(ClientSession session, byte[] bytes, String as, String type)
	{
		// TODO: Implement this method
		//byte [] bytes;
		String filename=as;
		boolean retry=true;
		int times=0;
		while (retry && times < 4)
		{		
			Operation putOperation=null;
			OutputStream mOutput = null;
			//ClientSession mSession = null;
			//ArrayUtils.reverse(bytes);
			try
			{	
				// Send a file with meta data to the server
				final HeaderSet hs = new HeaderSet();
				hs.setHeader(HeaderSet.NAME, filename);
				hs.setHeader(HeaderSet.TYPE, type);
				hs.setHeader(HeaderSet.LENGTH, new Long((long)bytes.length));
				Log.v(TAG,filename);
				//Log.v(TAG,type);
				Log.v(TAG,bytes.toString());
				putOperation = session.put(hs);

				mOutput = putOperation.openOutputStream();
				mOutput.write(bytes);
				mOutput.close();
				putOperation.close();
			}
			catch (Exception e)
			{
				Log.e(TAG, "put failed", e);
				retry = true;
				times++;
				continue;
				//e.printStackTrace();
			}
			finally
			{
				try
				{
					
					if(mOutput!=null)
						mOutput.close();
					if(putOperation!=null)
						putOperation.close();
				}
				catch (Exception e)
				{
					Log.e(TAG, "put finally" , e);
					retry = true;
					times++;
					continue;
				}
				//updateStatus("[CLIENT] Connection Closed");
			}
			retry = false;
			return true;
		}
		return false;
	}

	private void FinishBatch(ClientSession mSession) throws IOException
	{
		mSession.disconnect(null);
		try
		{
			Thread.sleep((long)500);
		}
		catch (InterruptedException e)
		{}
		mBtSocket.close();
	}
	public boolean flush() throws IOException
	{
		if (sendQueue.isEmpty())
		{
			return true;
		}
		try
		{
			Thread.sleep((long)2000);
		}
		catch (InterruptedException e)
		{}
		ClientSession session=StartBatch(sendQueue.size());
		if (session == null)
		{
			return false;
		}
		while (!sendQueue.isEmpty())
		{
			if (Put(session, sendQueue.remove()) == false)
			{
				Log.e(TAG, "Put failed");
			}
		}
		FinishBatch(session);
		return true;
	}
	Queue<BatchInfo> sendQueue;
	
	public boolean AddTransfer(File f)
	{
		if (!f.exists())
		{
			return false;
		}
		sendQueue.add(new BatchInfo(f));
		return true;
	}
	public boolean AddTransfer(File f, String name)
	{
		if (!f.exists())
		{
			return false;
		}
		sendQueue.add(new BatchInfo(f, name));
		return true;
	}
	public boolean AddTransfer(File[] f)
	{
		boolean success=true;
		for (File files:f)
		{
			if (files.exists())
			{
				sendQueue.add(new BatchInfo(files));
			}
			else
			{
				Log.e(TAG, "File not exist" + files.getAbsolutePath());
				success = false;
			}
		}
		return success;
	}
	public boolean AddTransfer(String s)
	{
		if (s == null)
		{
			return false;
		}
		sendQueue.add(new BatchInfo(s));
		return true;
	}
	public boolean AddTransfer(String s, String name)
	{
		if (s == null)
		{
			return false;
		}
		sendQueue.add(new BatchInfo(s, name));
		return true;
	}
	public boolean AddTransfer(String s, String name, boolean Euc)
	{
		if (s == null)
		{
			return false;
		}
		sendQueue.add(new BatchInfo(s, name, Euc));
		return true;
	}
	public boolean AddTransfer(String[] s)
	{
		boolean success=true;
		for (String str:s)
		{
			if (str == null)
			{
				Log.e(TAG, "STR NULL");
				success = false;
			}
			else
			{
				sendQueue.add(new BatchInfo(str));
			}
		}
		return success;
	}
	public boolean AddTransfer(String as, byte[] bytes)
	{
		if (as != null && bytes != null)
		{
			sendQueue.add(new BatchInfo(as, bytes));
		}
		else
		{
			Utility.addError(TAG, "as or byted null");
			return false;
		}
		return true;
	}
}
