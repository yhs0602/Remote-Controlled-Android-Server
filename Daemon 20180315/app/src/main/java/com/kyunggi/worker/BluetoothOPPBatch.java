package com.kyunggi.worker;

import android.bluetooth.*;
import android.util.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import javax.obex.*;

public class BluetoothOPPBatch extends BluetoothBatch
{

	private String TAG="BTOPP OPPBATCH";
	//private BluetoothDevice device;
	//private BluetoothSocket mBtSocket;
	//private UUID OPPUUID=UUID.fromString(("00001105-0000-1000-8000-00805f9b34fb"));

	public BluetoothOPPBatch(BluetoothDevice dev)
	{
		super(dev);
		//device = dev;
		//sendQueue = new LinkedBlockingQueue<OPPBatchInfo>();
	}
	
	
	public ClientSession StartBatch(int n)
	{
		ClientSession mSession = null;

		// TODO: Implement this method
		boolean retry=true;
		int times=0;
		while (retry && times < 4)
		{
			BluetoothConnector.BluetoothSocketWrapper bttmp=null;
			try
			{
				
				bttmp =  (new BluetoothConnector(device,false,BluetoothAdapter.getDefaultAdapter(),Arrays.asList(new UUID[]{OPPUUID,OPPUUID, OPPUUID}))).connect();//*/ device.createInsecureRfcommSocketToServiceRecord(OPPUUID);
				/*if(mBtSocket.isConnected())
				{
					mBtSocket.close();
				}*/
			}
			catch (Exception e)
			{
				Log.e(TAG, "opp fail sock " + e.getMessage());
				retry = true;
				times++;
				continue;
			}		

			try
			{
				// 소켓을 연결한다
				mBtSocket=bttmp.getUnderlyingSocket();
			//	mBtSocket.connect();
				BluetoothObexTransport mTransport = null;
				mSession = new ClientSession((ObexTransport)(mTransport = new BluetoothObexTransport(mBtSocket)));

				HeaderSet headerset = new HeaderSet();
				//	headerset.setHeader(HeaderSet.COUNT,n);

				headerset = mSession.connect(null);

				if (headerset.getResponseCode() == ResponseCodes.OBEX_HTTP_OK)
				{
					boolean mConnected = true;
				}
				else
				{
					Log.e(TAG, "SEnd by OPP denied;");
					mSession.disconnect(headerset);
					times++;
					continue;
				}

			}
			catch (Exception e)
			{
				Log.e(TAG, "opp failed;" , e);
				retry = true;
				times++;
				continue;
				//e.rintStackTrace();
			}
			retry=false;
		}
		return mSession;

	}
	/*private boolean Put(ClientSession session, OPPBatchInfo info)
	{
		// TODO: Implement this method
		byte [] bytes;
		String filename=info.filename;
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
				bytes = info.s.getBytes("UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				return false;
			}
		}
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
				Log.e(TAG, "opp failed;" + e.getLocalizedMessage());
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
					Log.e(TAG, "opp" + e.getLocalizedMessage());
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
*//*
	private void FinishBatch(ClientSession mSession) throws IOException
	{
		mSession.disconnect(null);
		mBtSocket.close();
	}*/
/*	public boolean flush() throws IOException
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
	}*/
	//Queue<OPPBatchInfo> sendQueue;
/*	public class OPPBatchInfo
	{
		public String s;
		public String filename;
		public File file;
		public boolean isfile;
		public OPPBatchInfo(Object o)
		{
			if (o instanceof String)
			{
				isfile = false;
				s = (String) o;
				filename = "response.txt";
			}
			else if (o instanceof File)
			{
				isfile = true;
				file = (File) o;
				filename = file.getName();
			}
		}
		public OPPBatchInfo(Object o, String n)
		{
			if (o instanceof String)
			{
				isfile = false;
				s = (String) o;
				filename = n;
			}
			else if (o instanceof File)
			{
				isfile = true;
				file = (File) o;
				filename = n;
			}
		}
	}*/
	/*public boolean AddTransfer(File f)
	{
		if (!f.exists())
		{
			return false;
		}
		sendQueue.add(new OPPBatchInfo(f));
		return true;
	}
	public boolean AddTransfer(File f, String name)
	{
		if (!f.exists())
		{
			return false;
		}
		sendQueue.add(new OPPBatchInfo(f, name));
		return true;
	}
	public boolean AddTransfer(File[] f)
	{
		boolean success=true;
		for (File files:f)
		{
			if (files.exists())
			{
				sendQueue.add(new OPPBatchInfo(files));
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
		sendQueue.add(new OPPBatchInfo(s));
		return true;
	}
	public boolean AddTransfer(String s, String name)
	{
		if (s == null)
		{
			return false;
		}
		sendQueue.add(new OPPBatchInfo(s, name));
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
				sendQueue.add(new OPPBatchInfo(str));
			}
		}
		return success;
	}*/
}

