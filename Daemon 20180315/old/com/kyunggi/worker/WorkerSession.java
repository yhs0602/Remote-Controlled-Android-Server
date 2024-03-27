package com.kyunggi.worker;
import android.bluetooth.*;
import android.os.*;
import android.text.*;
import android.util.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import javax.obex.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import com.kyunggi.worker.ExternalProgram.*;
import java.lang.reflect.*;

public class WorkerSession implements Serializable,IStringSender
{
	transient public Program shell=null;
	private transient static final String TAG="BTOPP WorkerSession";
	
	private static final HashMap<String,Method> commandmap=new HashMap<String,Method>();
	static{
		try
		{
			commandmap.put("ls", CommandImpl.class.getMethod("DoLs",String[].class));
			commandmap.put("cd", CommandImpl.class.getMethod("DoCd",String[].class));
			commandmap.put("toast", CommandImpl.class.getMethod("DoToast",String[].class));
			commandmap.put("say", CommandImpl.class.getMethod("DoSay",String[].class));
			commandmap.put("camera0", CommandImpl.class.getMethod("DoCamera0",String[].class));
			commandmap.put("camera1", CommandImpl.class.getMethod("DoCamera1",String[].class));
			commandmap.put("silent", CommandImpl.class.getMethod("DoSilent",String[].class));
			commandmap.put("get", CommandImpl.class.getMethod("DoGet",String[].class));
			commandmap.put("ren", CommandImpl.class.getMethod("DoRen",String[].class));
			commandmap.put("wget", CommandImpl.class.getMethod("DoWget",String[].class));
			commandmap.put("wgethtm", CommandImpl.class.getMethod("DoWgetHtm",String[].class));
			commandmap.put("wgetlink", CommandImpl.class.getMethod("DoWgetLink",String[].class));
			commandmap.put("wgetmedia", CommandImpl.class.getMethod("DoWgetMedia",String[].class));
			commandmap.put("wgetimports", CommandImpl.class.getMethod("DoWgetImports",String[].class));
			commandmap.put("wgetf", CommandImpl.class.getMethod("DoWgetF",String[].class));
			commandmap.put("wifion", CommandImpl.class.getMethod("DoWifiOn",String[].class));
			commandmap.put("wifioff", CommandImpl.class.getMethod("DoWifiOff",String[].class));
			commandmap.put("google", CommandImpl.class.getMethod("DoGoogle",String[].class));
			commandmap.put("wiki", CommandImpl.class.getMethod("DoWiki",String[].class));
			commandmap.put("namuwiki", CommandImpl.class.getMethod("DoNamuwiki",String[].class));
			commandmap.put("ndic", CommandImpl.class.getMethod("DoNdic",String[].class));
			commandmap.put("translate", CommandImpl.class.getMethod("DoTranslate",String[].class));
			commandmap.put("reboot", CommandImpl.class.getMethod("DoReboot",String[].class));
			commandmap.put("shell", CommandImpl.class.getMethod("DoShell",String[].class));
			commandmap.put("close", CommandImpl.class.getMethod("DoClose",String[].class));
			commandmap.put("mailto", CommandImpl.class.getMethod("DoMailTo",String[].class));
		}
		catch (Exception e)
		{
			Log.e(TAG,"",e);
		}
	}
	public IOutputSender sender;
	public IOutputSender altSender;

	private CommandImpl cmdimpl=new CommandImpl(this);
	@Override
	public void SendString(String s)
	{
		// TODO: Implement this method
		sender.Send(s);
		return ;
	}

	@Override
	public void SendString(String[] sarr)
	{
		// TODO: Implement this method
		//workerThread.sendSt
		return ;
	}

	transient BluetoothDevice device;
	transient BluetoothSocket mBtSocket;
	transient BluetoothOPPBatch batch;
	String address;
	int authority;
	String name;
	String path="/storage/emulated/0";
	transient BluetoothAdapter mAdapter;
	
	public transient WorkerService.ServiceHandler mHandler;
	public enum SENDMODE
	{
		ONLY_OPP,
		ONLY_FTP,
		OPPFTP
	};
	SENDMODE sendMode=SENDMODE.ONLY_OPP;
	boolean bRealTime=true;
	boolean giveInfo=false;
	boolean ftpAllowed=true;
	boolean oppAllowed=true;
	boolean UsbMode=false;
	private HashSet<String> OPP_ACCEPT_RAW_EXTS=new HashSet<String>();
	private HashSet<String> OPP_CONVERT_JPG_EXTS=new HashSet<String>();
	private HashSet<String> OPP_CONVERT_K3G_EXTS=new HashSet<String>();
	private HashSet<String> OPP_CONVERT_TXT_EXTS=new HashSet<String>();
	private HashSet<String> OPP_CONVERT_VNT_EXTS=new HashSet<String>();
	private HashSet<String> OPP_RENAME_TXT_EXTS=new HashSet<String>();
	private HashSet<String> OPP_RENAME_K3G_EXTS=new HashSet<String>();
	private HashSet<String> FTP_ACCEPT_RAW_EXTS=new HashSet<String>();

	class WorkerThread extends Thread implements Serializable
	{

		private boolean mAbort=false;

		private String TAG="BTOPP WORKERTHREAD";

		private UUID FTPUUID=UUID.fromString(("00001106-0000-1000-8000-00805f9b34fb"));

		private UUID OPPUUID=UUID.fromString(("00001105-0000-1000-8000-00805f9b34fb"));

		private void DoCommand(String cmd)
		{
			
			// TODO: Implement this method
			Log.v(TAG, "DoCommand " + cmd);
			if (cmd == null)return;
			String [] words=cmd.split(" ");
			if (words == null)
			{
				Log.e(TAG, "Words null");
				return;
			}
			if (words.length == 0)
			{
				Log.e(TAG, "Words short");
				return;
			}
			String program=words[0].toLowerCase();
			Method method=commandmap.get(program);
			try
			{
				method.invoke(cmdimpl,(Object) words);
			}
			catch (IllegalAccessException e)
			{
				sender.SendError(e);
			}
			catch (IllegalArgumentException e)
			{
				sender.SendError(e);
			}
			catch (InvocationTargetException e)
			{
				sender.SendError(e);
			}
			catch (NullPointerException e)
			{
				sender.SendError("unknown command");
			}
			/*if (program.compareTo("toast") == 0)
			{
				DoToast(words);
			}
			else if (program.compareTo("say") == 0)
			{
				DoSay(words);
			}
			else if (program.compareTo("camera0") == 0)
			{
				DoCamera0(words);
			}
			else if (program.compareTo("camera1") == 0)
			{
				DoCamera1(words);
			}
			else if (program.compareTo("get") == 0)
			{			
				DoGet(words);		
			}
			else if (program.compareTo("ls") == 0)
			{
				sender.Send((DoLs(path)));
			}
			else if (program.compareTo("cd") == 0)
			{	
				DoCd(words);		
			}
			else if (program.compareTo("ren") == 0)
			{
				DoRename(words);
			}
			else if (program.compareTo("wget") == 0)
			{
				DoWget(FinishArgs(words));
			}
			else if (program.compareTo("wgethtm") == 0)
			{
				DoWgetHtm(words);
			}
			else if (program.compareTo("wgetmedia") == 0)
			{
				DoWgetMedia(words);
			}
			else if (program.compareTo("wgetlink") == 0)
			{
				DoWgetLink(words);
			}
			else if (program.compareTo("wgetimports") == 0)
			{
				DoWgetImports(words);
			}
			else if (program.compareTo("wgetf") == 0)
			{
				DoWgetF(words);
			}
			else if (program.compareTo("wifioff") == 0)
			{
				TurnOffWifi();
			}
			else if (program.compareTo("wifion") == 0)
			{
				TurnOnWifi();
			}
			else if (program.compareTo("google") == 0)
			{
				DoGoogle(words);
			}
			else if (program.compareTo("namuwiki") == 0)
			{
				DoNamuwiki(words);
			}
			else if (program.compareTo("wiki") == 0)
			{
				DoWiki(words);
			}
			else if (program.compareTo("ndic") == 0)
			{
				DoNdic(words);
			}
			else if (program.compareTo("translate") == 0)
			{
				DoTranslate(words);
			}
			else if (program.compareTo("reboot") == 0)
			{
				DoReboot();
			}
			else if (program.compareTo("shell") == 0)
			{

				//Focus=shell;
				//https://m.search.naver.com/search.naver?query=hello&where=m_ldic&sm=msv_hty
				//shell.
				//https://translate.google.co.kr/m/translate?hl=ko#auto/en/hello
				//DoWget("https://google.co.kr/search?q=" + FinishArgs(words));
			}
			else if (program.compareTo("silent") == 0)
			{
				DoSilent(words);
				//Utility.ToSilentMode();

			}
			else if (program.compareTo("mailto") == 0)
			{
				//Utility.SendMail(words[1],
				return;
			}
			else if (program.compareTo("close") == 0)
			{
				DoClose();
				return;
			}
			else
			{			
				Log.e(TAG, "UNKNOWN COMMAND " + program + ")!)");
				sender.SendError("Unknown  command"+program);  //SendStringVntViaOPP("Unknown command " + program + "oh;");//+FinishArgs(words));
				return;
			}*/
		}
		
		private boolean Send(String filename)
		{
			String normalizedfilename = null;
			if (!UsbMode)
				normalizedfilename = NormalizeFileFormat(filename);
			if (normalizedfilename == null)
				normalizedfilename = filename;
			if ((sendMode == SENDMODE.ONLY_FTP || UsbMode) && ftpAllowed || !oppAllowed)
			{
				return SendFileViaFTP(normalizedfilename);
			}
			else //(sendMode == SENDMODE.OPPFTP)
			{
				return SendFileViaOPP(normalizedfilename);
			}				
		}

		private String NormalizeFileFormat(String filename)
		{
			String ext=Utility.GetFileExtension(filename);
			String converted=null;
			if (OPP_ACCEPT_RAW_EXTS.contains(ext))
			{
				return filename;
			}
			else
			{
				if (OPP_RENAME_K3G_EXTS.contains(ext))
				{
					//SendFileViaOPP(filename, filename + ".k3g");
					if (new File(filename).renameTo(new File(filename + ".k3g")) == true)
					{
						return filename + ".k3g";
					}
				}
				else if (OPP_RENAME_TXT_EXTS.contains(ext))
				{
					if (new File(filename).renameTo(new File(filename + ".txt")) == true)
					{
						return filename + ".txt";
					}
				}
				else if (OPP_CONVERT_JPG_EXTS.contains(ext))
				{
					Utility.ImageConvert(filename, "jpg");
					return filename + ".jpg";
				}
				else if (OPP_CONVERT_K3G_EXTS.contains(ext))
				{

				}
				else if (OPP_CONVERT_TXT_EXTS.contains(ext))
				{

				}
				else if (OPP_CONVERT_VNT_EXTS.contains(ext))
				{

				}

				//SendFileViaFTP(filename);
			}
			return null;
		}

		private boolean SendFileViaOPP(String filename, String as)
		{
			// TODO: Implement this method
			byte [] filebytes;
			try
			{
				filebytes = Utility.readFully(filename);
			}
			catch (IOException e)
			{
				Log.e(TAG, "IOEXCEPTION IN OPP " + e.getLocalizedMessage());
				return false;
			}
			return SendViaOPP(as, filebytes);
		}



		private boolean SendFileViaOPP(String filename)
		{
			// TODO: Implement this method
			byte [] filebytes;
			try
			{
				filebytes = Utility.readFully(filename);
			}
			catch (IOException e)
			{
				Log.e(TAG, "IOEXCEPTION IN OPP " + e.getLocalizedMessage());
				return false;
			}
			return SendViaOPP(filename, filebytes);
		}

		private boolean SendStringViaOPP(String s)
		{
			// TODO: Implement this method
			if ((mAdapter == null) || (mAdapter != null && !mAdapter.isEnabled()))
			{
				// Bluetooth는 지원되지만 활성화되어 있지 않다
				Log.e(TAG, "SendStringViaOpp not bt");
				return false;
			}
			try
			{
				return SendViaOPP("response.txt", s.getBytes("EUC-KR"));
			}
			catch (UnsupportedEncodingException e)
			{
				try
				{
					return SendViaOPP("responseUTF.txt", s.getBytes("UTF-8"));
				}
				catch (UnsupportedEncodingException f)
				{Log.e(TAG, "NO!!!!! ");}
			}
			return false;

		}


		private boolean SendFileViaFTP(String filename)
		{
			// TODO: Implement this method
			SendStringViaOPP("Trying to send via ftp... please be ready");
			byte [] filebytes;
			try
			{
				filebytes = Utility.readFully(filename);
			}
			catch (IOException e)
			{
				Log.e(TAG, "IOEXCEPTION IN FTP");
				return false;
			}

			boolean retry=true;
			int times=0;
			while (retry && times < 5)
			{
				try
				{
					Thread.sleep((long)5000);
				}
				catch (InterruptedException e)
				{}
				try
				{
					mBtSocket = device.createInsecureRfcommSocketToServiceRecord(FTPUUID);
				}
				catch (Exception e)
				{
					Log.e(TAG, "ftp" + e.getLocalizedMessage());
					retry = true;
					times++;
					continue;
				}
				UUID uuid=UUID.fromString("F9EC7BC4-953C-11D2-984E-525400DC9E09");
				ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
				bb.putLong(uuid.getMostSignificantBits());
				bb.putLong(uuid.getLeastSignificantBits());
				byte [] bytes=bb.array();
				Operation putOperation=null;
				OutputStream mOutput = null;
				ClientSession mSession = null;
				//ArrayUtils.reverse(bytes);
				try
				{
					// 소켓을 연결한다
					mBtSocket.connect();
					BluetoothObexTransport mTransport = null;
					mSession = new ClientSession((ObexTransport)(mTransport = new BluetoothObexTransport(mBtSocket)));

					HeaderSet headerset = new HeaderSet();
					headerset.setHeader(HeaderSet.TARGET, bytes);

					headerset = mSession.connect(headerset);

					if (headerset.getResponseCode() == ResponseCodes.OBEX_HTTP_OK)
					{
						boolean mConnected = true;
					}
					else
					{
						mSession.disconnect(headerset);
					}
					// Send a file with meta data to the server
					final HeaderSet hs = new HeaderSet();
					hs.setHeader(HeaderSet.NAME, filename);
					hs.setHeader(HeaderSet.TYPE, Utility.getMimeType(filename));
					hs.setHeader(HeaderSet.LENGTH, new Long((long)filebytes.length));

					putOperation = mSession.put(hs);

					mOutput = putOperation.openOutputStream();
					mOutput.write(filebytes);
					mOutput.close();
					putOperation.close();
				}
				catch (Exception e)
				{
					Log.e(TAG, "ftp" + e.getLocalizedMessage());
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
						mSession.disconnect(null);

					}
					catch (Exception e)
					{
						Log.e(TAG, "ftp" + e.getLocalizedMessage());
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

		private boolean SendViaOPP(String filename, byte[] bytes)
		{
			// TODO: Implement this method
			boolean retry=true;
			int times=0;
			while (retry && times < 5)
			{
				try
				{
					sleep((long)2000);
				}
				catch (InterruptedException e)
				{}
				try
				{
					mBtSocket = device.createInsecureRfcommSocketToServiceRecord(OPPUUID);
				}
				catch (Exception e)
				{
					Log.e(TAG, "opp fail sock " + e.getMessage());
					retry = true;
					times++;
					continue;
				}		
				Operation putOperation=null;
				OutputStream mOutput = null;
				ClientSession mSession = null;
				//ArrayUtils.reverse(bytes);
				try
				{
					// 소켓을 연결한다
					mBtSocket.connect();
					BluetoothObexTransport mTransport = null;
					mSession = new ClientSession((ObexTransport)(mTransport = new BluetoothObexTransport(mBtSocket)));

					HeaderSet headerset = new HeaderSet();

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
					// Send a file with meta data to the server
					final HeaderSet hs = new HeaderSet();
					hs.setHeader(HeaderSet.NAME, filename);
					hs.setHeader(HeaderSet.TYPE, Utility.getMimeType(filename));
					hs.setHeader(HeaderSet.LENGTH, new Long((long)bytes.length));

					putOperation = mSession.put(hs);

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
						mSession.disconnect(null);

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

		//public static final transient HashSet<String> OppAbleExts=new HashSet<String>();


		public void AddCommand(String [] cmds)
		{
			if (workQueue == null)
			{
				workQueue = new  LinkedBlockingQueue<String>();
			}
			Log.v(TAG, "Work!");
			for (String s:cmds)
			{
				//Log.v(TAG, s);
				workQueue.add(s);
			}
			if (!this.isAlive())
			{
				start();
			}
			else
			{
				synchronized (this)
				{
					notifyAll();
				}
			}
		}
		public void Abort()
		{
			Log.e(TAG, "Die!");
			mAbort = true;
			synchronized (this)
			{
				notifyAll();
			}
		}
		@Override
		public void run()
		{
			// TODO: Implement this method
			//super.run();
			Log.e(TAG, "run!");
			while (!mAbort)
			{
				while (!workQueue.isEmpty())
				{
					try
					{
						DoCommand(workQueue.remove());
					}
					catch (Exception e)
					{
						Log.e(TAG, "", e);
						sender.SendError( e);
					}
					//Log.v(TAG, "Working");
				}
				synchronized (this)
				{
					try
					{
						wait();
					}
					catch (InterruptedException e)
					{}
				}		
			}
			if (!workQueue.isEmpty())
			{
				SaveWorks();
			}
		}

		private void SaveWorks()
		{
			// TODO: Implement this method
			Log.e(TAG, "Saving work");
			try
			{
				ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(new File("WorkFor" + address)));
				oos.writeObject(workQueue);
			}
			catch (IOException e)
			{
				Log.e(TAG, "Error writing works");
			} 
		}

		Queue<String> workQueue;
		public WorkerThread()
		{
			workQueue = null;
			if (OPP_ACCEPT_RAW_EXTS.isEmpty())
			{
				OPP_ACCEPT_RAW_EXTS.add("txt");
				OPP_ACCEPT_RAW_EXTS.add("jpg");
				OPP_ACCEPT_RAW_EXTS.add("k3g");
			}
			if (OPP_CONVERT_JPG_EXTS.isEmpty())
			{
				OPP_CONVERT_JPG_EXTS.add("png");
				OPP_CONVERT_JPG_EXTS.add("jpeg");
				OPP_CONVERT_JPG_EXTS.add("bmp");
			}
			if (OPP_CONVERT_K3G_EXTS.isEmpty())
			{
				OPP_CONVERT_K3G_EXTS.add("m4a");
				OPP_CONVERT_K3G_EXTS.add("avi");
				OPP_CONVERT_K3G_EXTS.add("mp4");
			}
			shell = new ProgramShell(WorkerSession.this);

			/*//workQueue = new LinkedBlockingQueue<String>();
			 try
			 {
			 ObjectInputStream oos=new ObjectInputStream(new FileInputStream(new File("WorkFor" + address)));
			 workQueue = (Queue<String>) oos.readObject();
			 }
			 catch (Exception e)
			 {
			 Log.e(TAG, "Err Loading " + e.getLocalizedMessage());
			 workQueue = new LinkedBlockingQueue<String>();
			 } */
		}
	}
	public WorkerSession(WorkerService.ServiceHandler handler, String addr)
	{
		address = addr;
		workerThread = new WorkerThread();
		//workerThread.start();
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandler = handler;
		device = mAdapter.getRemoteDevice(address);
		batch = new BluetoothOPPBatch(device);
		sender=new IOPPOutputVntSender(batch);
	}
	public WorkerSession()
	{
		workerThread = new WorkerThread();
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		mHandler = null;
	}
	public void init(WorkerService.ServiceHandler h)
	{
		if (mHandler == null)
		{
			mHandler = h;
			device = mAdapter.getRemoteDevice(address);
		}

	}
	public void ExecuteCommands(String cmds)
	{
		workerThread.AddCommand(cmds.split("\n"));
		/*	if(!workerThread.isAlive())
		 {
		 workerThread.start();
		 }
		 else
		 {
		 synchronized(workerThread)
		 {
		 workerThread.notifyAll();
		 }
		 }*/
	}

	public void Join()
	{
		workerThread.Abort();
		Log.v(TAG, "Join");
		try
		{
			workerThread.join();
		}
		catch (InterruptedException e)
		{}
	}
	private WorkerThread workerThread;

}
