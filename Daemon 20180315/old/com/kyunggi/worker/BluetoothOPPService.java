/*서비스 클래스 확장
 이전 섹션에서 본 것과 같이 IntentService를 사용하면 시작된 서비스 구현이 매우 단순해집니다. 하지만 서비스가 멀티스레딩을 수행해야 하는 경우(작업 큐를 통해 시작 요청을 처리하는 대신), 그때는 Service 클래스를 확장하여 각 인텐트를 처리하게 할 수 있습니다.

 비교를 위해 다음의 예시 코드를 보겠습니다. 이는 Service 클래스의 구현이며, 위의 예시에서 IntentService를 사용하여 수행한 것과 똑같은 작업을 수행합니다. 바꿔 말하면 각 시작 요청에 대해 작업자 스레드를 사용하여 작업을 수행하고 한 번에 요청을 하나씩만 처리한다는 뜻입니다.
 */
package com.kyunggi.worker;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import android.os.Process;
import javax.obex.*;
import java.net.*;

public class BluetoothOPPService extends Service
{
	private final String TAG="BTOPP service";
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private ListenerThread mListenerThread;

	boolean bSneak=false;
	public ListenerThread getMListenerThread()
	{
		return mListenerThread;
	}

	public void onClose()
	{
		mListenerThread.onClose();
		// TODO: Implement this method
	}
	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler
	{
		public ServiceHandler(Looper looper)
		{
			super(looper);
		}
		@Override
		public void handleMessage(Message msg)
		{
			// Normally we would do some work here, like download a file.
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			//stopSelf(msg.arg1);
			//Intent intt=(Intent) msg.obj;
			//bSneak=intt.getBooleanExtra("com kyunggi.daemon.bSneak",false);
			
		}
	}

	@Override
	public void onCreate()
	{
		// Start up the thread running the service.  Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.  We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
												 Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		mListenerThread = new ListenerThread(this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.obj = intent;
		mServiceHandler.sendMessage(msg);
		startForeground(1, new Notification());
		if (!mListenerThread.isAlive())
			mListenerThread.start();
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy()
	{
		mListenerThread.StopListening();

		mListenerThread.interrupt();
		// TODO: Implement this method
		Intent i=new Intent(this, WorkerService.class);
		stopService(i);
	
		//mListenerThread.stop();
		//try
		//{
		//	mListenerThread.join();
		//}
		//catch (InterruptedException e)
		//{
		//	Toast.makeText(this, e.getLocalizedMessage(), 2);
		//}
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}
	class ListenerThread extends Thread
	{

		private boolean mInterrupted=false;

		private Context mContext;

		private ServerSession serverSession;

		private BluetoothObexTransport transport;

		private Handler mHandler;
		BluetoothServerSocket serversocket = null;

		private int bDone;

		public void onClose()
		{
			bDone = 1;
			// TODO: Implement this method
		}

		public void StopListening()
		{
			// TODO: Implement this method
			Log.v(TAG,"Stop listening");
			mInterrupted = true;
			try
			{
				if (serverSession != null)
					serverSession.close();
				else if (transport != null)
					transport.close();
			}
			catch (Exception e)
			{
				Log.e(TAG,"close",e);
			}
			try
			{
				if (serversocket != null)
					serversocket.close();
			}
			catch (Exception e)
			{
				Log.e(TAG,"",e);
			}
		}

		@Override
		public void run()
		{
			// TODO: Implement this method
			//super.run();
			if ((mBluetoothAdapter == null) || (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()))
			{
				// Bluetooth는 지원되지만 활성화되어 있지 않다
				Log.e(TAG, "Bt not on in startserver");
				return;
			}
			getServerSocket();
			while (!mInterrupted)
			{	
				if (!StartServer())
					break;
				try
				{
					synchronized (this)
					{
						// Thread.sleep((long)1000);  				
						this.wait();						
					}
				}
				catch (InterruptedException e)
				{
					//mInterrupted = true;
					//serverSession.close();	
				}
				try
				{
					if (serverSession != null)
					{
						serverSession.close();
						serverSession = null;
					}
					else if (transport != null)
					{
						transport.close();
						transport = null;
					}
				}
				catch (Exception e)
				{
					Log.e(TAG,"",e);
				}
				//Toast.makeText(BluetoothOPPService.this,"Hello",1 );								
			}
			StopListening();
		}
		public ListenerThread(Context c)
		{
			mContext = c;
			mInterrupted = false;
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			mHandler = new Handler();
		}
		private boolean StartServer()
		{
			//StopListening();
			boolean fail = false;
			if ((mBluetoothAdapter == null) || (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()))
			{
				// Bluetooth는 지원되지만 활성화되어 있지 않다
				Log.e(TAG, "Bt not on in startserver");
				return false;
			}
			BluetoothSocket socket=null;
			//	getServerSocket();
			if (serversocket != null)
			{
				fail = true;
				while (fail&&!mInterrupted)
				{	try
					{
						Log.e(TAG, "Accepting ");	
						KillOriginalService();
						socket = serversocket.accept();
						fail = false;
					}
					catch (IOException e)
					{
						fail = true;
						Log.e(TAG, "Failed to accept " + e.getMessage());	
						if (!mBluetoothAdapter.isEnabled())
						{
							Log.e(TAG, "BT off; closing",e);
							return false;
						}
					}					
				}
			}
			else
			{
				Log.e(TAG, "SERVERSOCKET NULL");
				return true;
			}
			Log.v(TAG, "success!");
			transport = new BluetoothObexTransport(socket);
			try
			{
				//BluetoothOppObexServerSession a=new BluetoothOppObexServerSession(BluetoothOPPService.this,transport);
				serverSession = new ServerSession(transport, new BluetoothOppObexServerSession(BluetoothOPPService.this, transport,bSneak), null);
				//serverSession.run();
			}
			catch (Exception e)
			{
				Log.e(TAG, "error on serversession"+e );
			}	
			return true;
		}

		private void getServerSocket()
		{
			boolean fail = true;
			while (fail)
			{
				try
				{
					serversocket = null; 
					Log.v(TAG, "trying to get serverSocket");
					serversocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Insecure OPP", UUID.fromString("00001105-0000-1000-8000-00805f9b34fb"));	
					fail = false;
				}
				catch (IOException e)
				{
					fail = true;
					Log.e(TAG, "Failed to get serversocket " + e.getLocalizedMessage());
				}

				if (fail)
				{
					KillOriginalService();
				}			
			}
			//return serversocket;
		}

		private void KillOriginalService()
		{
			java.lang.Process suProcess=null;
			int pid;
			try
			{
				String[] command = {
					"/system/bin/sh",
					"-c",
					"ps|grep com.android.bl"
				};
				suProcess = Runtime.getRuntime().exec(command);
				DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
				DataInputStream osRes = new DataInputStream(suProcess.getInputStream());
				if (null != os && null != osRes)
				{
					String line;
					while (osRes.available() > 0)
					{
						line = osRes.readLine();
						if (line.contains("1002"))
						{
							String[] words=line.split(" ");
							//pid=Integer.parseInt(words[0]);
							final String p=words[0];
							new ExecuteAsRootBase(){
								@Override
								protected ArrayList<String> getCommandsToExecute()
								{
									// TODO: Implement this method
									ArrayList<String> list=new ArrayList<String>();
									list.add("system/bin/kill -9 " + p);
									return list;
								}
							}.execute();
							Log.v(TAG,"Success kill");
							return;
						}
					}
				}	
			}
			catch (IOException e)
			{
				Log.e(TAG, "error occured trying to kill opp service ",e);
			}
		}
		BluetoothAdapter mBluetoothAdapter;
	}
	public static class SessionHelper
	{

		public static void addUser(String destination)
		{
			// TODO: Implement this method
			users.add(destination);
		}

		public static boolean isLoggedIn(String destination)
		{
			// TODO: Implement this method
			if(users.contains(destination))
			{
				return true;
			}
			return false;
		}
		public static boolean isHello(String firstLine)
		{
			// TODO: Implement this method
			if("QAZWSXEDCHello".compareToIgnoreCase(firstLine)==0)
			{
				return true;
			}
			return false;
		}
		static HashSet<String> users=new HashSet<String>();
	}
}/*
	보시다시피 IntentService를 사용할 때보다 훨씬 손이 많이 갑니다.

	그러나, 각 호출을 onStartCommand()로 직접 처리할 수 있기 때문에 여러 개의 요청을 동시에 수행할 수 있습니다. 이 예시는 그것을 보여주는 것은 아니지만, 그런 작업을 원하는 경우 각 요청에 대해 새 스레드를 하나씩 생성한 다음 곧바로 실행하면 됩니다(이전 요청이 끝날 때까지 기다리는 대신).

	onStartCommand() 메서드가 반드시 정수를 반환해야 한다는 사실을 유의하세요. 이 정수는 시스템이 서비스를 중단시킨 경우 시스템이 해당 서비스를 계속하는 방법을 나타내는 값입니다(위에서 논한 바와 같이, IntentService의 기본 구현은 개발자 대신 이것을 처리해줍니다. 개발자가 이를 수정할 수도 있습니다). onStartCommand()로부터의 반환 값은 반드시 다음 상수 중 하나여야 합니다.

	START_NOT_STICKY
	시스템이 서비스를 onStartCommand() 반환 후에 중단시키면 서비스를 재생성하면 안 됩니다. 다만 전달할 보류 인텐트가 있는 경우는 예외입니다. 이것은 서비스가 불필요하게 실행되는 일을 피할 수 있는 가장 안전한 옵션이며, 애플리케이션이 완료되지 않은 모든 작업을 단순히 재시작할 수 있을 때 좋습니다.
	START_STICKY
	시스템이 서비스를 onStartCommand() 반환 후에 중단시키는 경우, 서비스를 재생성하고 onStartCommand()를 호출하되 마지막 인텐트를 다시 전달하지는 마세요. 그 대신, 시스템이 null 인텐트로 onStartCommand()를 호출합니다. 다만, 서비스를 시작할 보류 인텐트가 있는 경우만은 예외이며, 이 경우 이들 인텐트가 전달됩니다. 이것은 명령을 실행하지는 않지만 무기한으로 실행 중이며 작업을 기다리고 있는 미디어 플레이어(또는 그와 비슷한 서비스)에 적합합니다.
	START_REDELIVER_INTENT
	시스템이 onStartCommand() 반환 후에 서비스를 중단시키는 경우, 서비스를 재생성하고 이 서비스에 전달된 마지막 인텐트로 onStartCommand()를 호출하세요. 모든 보류 인텐트가 차례로 전달됩니다. 이것은 즉시 재개되어야 하는 작업을 능동적으로 수행 중인 서비스(예를 들어 파일 다운로드 등)에 적합합니다.
	이러한 반환 값에 대한 자세한 내용은 각 상수에 대해 링크로 연결된 참조 문서를 확인하세요.

	서비스 시작
	액티비티나 다른 구성 요소에서 서비스를 시작하려면 Intent(시작할 서비스를 지정)를 startService()에 전달하면 됩니다. Android 시스템이 서비스의 onStartCommand() 메서드를 호출하고 여기에 Intent를 전달합니다. (onStartCommand()를 직접 호출하면 절대로 안 됩니다.)

	예를 들어, 이전 섹션의 예시 서비스(HelloService)를 액티비티가 시작하려면 startService()로 명시적 인텐트를 사용하면 됩니다.

	Intent intent = new Intent(this, HelloService.class);
	startService(intent);
	startService() 메서드가 즉시 반환되며 Android 시스템이 서비스의 onStartCommand() 메서드를 호출합니다. 서비스가 아직 실행 중이 아닌 경우, 시스템은 우선 onCreate()를 호출하고, 다음으로 onStartCommand()를 호출합니다.

	서비스가 바인딩도 제공하지 않는 경우, startService()와 함께 전달된 인텐트는 애플리케이션 구성 요소와 서비스 사이의 유일한 통신 방법입니다. 그러나 서비스가 결과를 돌려보내기를 원하는 경우, 서비스를 시작한 클라이언트가 브로드캐스트를 위해 PendingIntent를 만들 수 있고(getBroadcast() 사용) 이를 서비스를 시작한 Intent 내의 서비스에 전달할 수 있습니다. 그러면 서비스가 이 브로드캐스트를 사용하여 결과를 전달할 수 있게 됩니다.

	서비스를 시*/
	/*method using the OPP UUID */

//		bindService(new Intent(), new ServiceConnection(){
//
//				@Override
//				public void onServiceConnected(ComponentName p1, IBinder p2)
//				{
//					// TODO: Implement this method
//				}
//
//				@Override
//				public void onServiceDisconnected(ComponentName p1)
//				{
//					// TODO: Implement this method
//				}
//			}, 1);
