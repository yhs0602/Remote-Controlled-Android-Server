package com.kyunggi.worker;
import android.bluetooth.*;
import android.util.*;
import java.io.*;
import java.util.*;
import javax.obex.*;

public class BluetoothOPPHelper
{
	String address;
	BluetoothAdapter mBtadapter;
	BluetoothDevice device;
	ClientSession session;
	BluetoothSocket mBtSocket;
	protected final UUID OPPUUID=UUID.fromString(("00001105-0000-1000-8000-00805f9b34fb"));

	private String TAG="BluetoothOPPHelper";

	public BluetoothOPPHelper(String address)
	{
		mBtadapter=BluetoothAdapter.getDefaultAdapter();
		device=mBtadapter.getRemoteDevice(address);
		try
		{
			mBtSocket = device.createRfcommSocketToServiceRecord(OPPUUID);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
			
		}
	}
	public ClientSession StartBatch(int n)
	{
		ClientSession mSession = null;

		// TODO: Implement this method
		boolean retry=true;
		int times=0;
		while (retry && times < 4)
		{
			//BluetoothConnector.BluetoothSocketWrapper bttmp=null;
			try
			{
				mBtSocket.connect();
				//bttmp =  (new BluetoothConnector(device,false,BluetoothAdapter.getDefaultAdapter(),Arrays.asList(new UUID[]{OPPUUID,OPPUUID, OPPUUID}))).connect();//*/ device.createInsecureRfcommSocketToServiceRecord(OPPUUID);
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
				//mBtSocket=bttmp.getUnderlyingSocket();
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
	
	protected boolean Put(ClientSession s, OPPBatchInfo info)
	{
		return Put(s,info.data,info.as,info.type);
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
	Queue<OPPBatchInfo> sendQueue;
	public boolean AddTransfer(String as,String mimetype,byte[] data)
	{
		return sendQueue.add(new OPPBatchInfo(as,mimetype,data));
	}
	class OPPBatchInfo
	{
		String as;
		String type;
		byte[] data;
		public OPPBatchInfo(String as,String type,byte[] data)
		{
			this.as=as;
			this.data=data;
			this.type=type;
		}
	}
	
}
