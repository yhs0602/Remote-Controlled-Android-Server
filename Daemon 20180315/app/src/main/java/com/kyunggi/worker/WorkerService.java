/*서비스 클래스 확장
 이전 섹션에서 본 것과 같이 IntentService를 사용하면 시작된 서비스 구현이 매우 단순해집니다. 하지만 서비스가 멀티스레딩을 수행해야 하는 경우(작업 큐를 통해 시작 요청을 처리하는 대신), 그때는 Service 클래스를 확장하여 각 인텐트를 처리하게 할 수 있습니다.

 비교를 위해 다음의 예시 코드를 보겠습니다. 이는 Service 클래스의 구현이며, 위의 예시에서 IntentService를 사용하여 수행한 것과 똑같은 작업을 수행합니다. 바꿔 말하면 각 시작 요청에 대해 작업자 스레드를 사용하여 작업을 수행하고 한 번에 요청을 하나씩만 처리한다는 뜻입니다.
 */
package com.kyunggi.worker;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.hardware.*;
import android.media.*;
import android.os.*;
import android.speech.tts.*;
import android.util.*;
import android.widget.*;
import java.io.*;
import java.util.*;

import android.hardware.Camera;


public class WorkerService extends Service
{
	private final String TAG="BTOPP Worker service";
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private HashMap <String,WorkerSession> sessions=null;
	private TextToSpeech tts;
	public static Context context;
	int left;

	public void setLeft(int left)
	{
		this.left = left;
	}

	public int getLeft()
	{
		return left;
	}
	public static Context getContext()
	{
		// TODO: Implement this method
		return context;
	}
	// Handler that receives messages from the thread
	public final class ServiceHandler extends Handler
	{

		private Camera mCamera;

		private MediaRecorder mediaRecorder;

		private boolean recording;

		
		public ServiceHandler(Looper looper)
		{
			super(looper);
		}
		@Override
		public void handleMessage(final Message msg)
		{
			// Normally we would do some work here, like download a file.
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			//stopSelf(msg.arg1);
			//뭐 하지??
			switch (msg.what)
			{
				case 0:
					Intent intent=null;
					if (msg.obj instanceof Intent)
					{
						intent = (Intent) msg.obj;
					}
					else
					{
						Log.e(TAG, "OBJ is not intent???");
						return;
					}
					String cmds=intent.getStringExtra("com.kyunggi.worker.command");
					String address=intent.getStringExtra("com.kyunggi.worker.address");
					left=intent.getIntExtra("com.kyunggi.worker.watchdog",15);
					Log.v(TAG, "cmd= " + cmds);
					if (cmds.compareToIgnoreCase("QAZWSXEDCAddUser") == 0)
					{
						if (sessions.containsKey(address))
							return;
						Log.v(TAG, "Adding User " + address);
						sessions.put(address, new WorkerSession(this, address));
						return;
					}
					else
					{
						if (!sessions.containsKey(address))
						{
							Log.e(TAG, "UNloggedin user came.");

							return;
						}
						if (cmds.compareToIgnoreCase("QAZWSXEDCDelUser") == 0)
						{
							Log.v(TAG, "DelUser");
							sessions.remove(address);
							return;
						}
					}
					Log.v(TAG, "ExexutingCmd");
					WorkerSession session=sessions.get(address);
					if (session == null)
					{
						Log.e(TAG, "session null");
					}
					session.ExecuteCommands(cmds);
					break;
				case 1://Toast
					if (msg.obj instanceof String)
					{
						String str=(String)msg.obj;
						Toast.makeText(WorkerService.this, "(" + str + ")", 1).show();
					}
					else
					{
						Log.e(TAG, "Toast not str");
					}
					break;
				case 2:
					if (msg.obj instanceof String)
					{
						Log.v(TAG, "tts start");
						final String str=(String)msg.obj;
						tts = new TextToSpeech(WorkerService.this, new TextToSpeech.OnInitListener()
							{

								@Override
								public void onInit(int p1)
								{
									// TODO: Implement this method
									Locale enUs = new Locale("korea");//new  Locale("en_US");
									if (tts.isLanguageAvailable(enUs) == TextToSpeech.LANG_AVAILABLE)
									{
										tts.setLanguage(enUs);
									}
									else
									{
										tts.setLanguage(Locale.KOREA);
									}
									//myTTS.setLanguage(Locale.US);   // 언어 설정 , 단말기에 언어 없는 버전에선 안되는듯
									tts.setPitch((float) 0.1);  // 높낮이 설정 1이 보통, 6.0미만 버전에선 높낮이도 설정이 안됨
									tts.setSpeechRate(1); // 빠르기 설정 1이 보통
									//myTTS.setVoice(); 
									tts.speak(str, TextToSpeech.QUEUE_FLUSH, null);  // tts 변환되어 나오는 음성					
								}
							});

					}
					else
					{
						Log.e(TAG, "TTS NOT STRING");
					}
					break;
				case 3:
					/*Intent pintent=new Intent(WorkerService.this,PictureActivity.class);
					 pintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 startActivity(pintent);

					 */
					final String str=(String)msg.obj;
					if (mCamera != null)
						mCamera.release();
					mCamera = Camera.open(0);
					mCamera.setDisplayOrientation(90);

					//출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox]
					try
					{
						mCamera.setPreviewTexture(new SurfaceTexture(10));
					}
					catch (IOException e1)
					{
						Log.e(TAG, e1.getMessage());
					}

					Camera.Parameters params = mCamera.getParameters();
					params.setPreviewSize(640, 480);
					params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					params.setPictureFormat(ImageFormat.JPEG);

					mCamera.setParameters(params);
					mCamera.startPreview();
					mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
							@Override
							public void onPictureTaken(byte[] p1, Camera p2)
							{
								// TODO: Implement this method
								//File file=new File("/storage/emulated/0/hello.jpg");
								File file;
								try
								{
									file = Utility.MakeNewFile("/storage/emulated/0/" + str + ".jpg");
								}
								catch (IOException e)
								{Log.e(TAG, "failed to create file");return;}
								try
								{
									FileOutputStream fos=new FileOutputStream(file);
									fos.write(p1);
									fos.close(); 
								}
								catch (FileNotFoundException e)
								{
									Log.e(TAG, "FILENOTFNDEXP");
								}
								catch (IOException e)
								{Log.e(TAG, "write file tail");}
							}
						});
					break;
				case 4:
					/*Intent pintent=new Intent(WorkerService.this,PictureActivity.class);
					 pintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 startActivity(pintent);

					 */
					final String str2=(String)msg.obj;
					if (mCamera != null)
						mCamera.release();
					mCamera = Camera.open(1);
					mCamera.setDisplayOrientation(90);
					//출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox]
					try
					{
						mCamera.setPreviewTexture(new SurfaceTexture(10));
					}
					catch (IOException e1)
					{
						Log.e(TAG, e1.getMessage());
					}

					Camera.Parameters param = mCamera.getParameters();
					param.setPreviewSize(640, 480);
					param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					param.setPictureFormat(ImageFormat.JPEG);

					mCamera.setParameters(param);
					mCamera.startPreview();
					mCamera.takePicture(null, null, null, new Camera.PictureCallback() {
							@Override
							public void onPictureTaken(byte[] p1, Camera p2)
							{
								// TODO: Implement this method
								File file=null;
								try
								{
									file = Utility.MakeNewFile("/storage/emulated/0/" + str2 + ".jpg");
								}
								catch (IOException e)
								{Log.e(TAG, "failed to create file");return;}
								try
								{
									FileOutputStream fos=new FileOutputStream(file);
									fos.write(p1);
									fos.close(); 
								}
								catch (FileNotFoundException e)
								{
									Log.e(TAG, "FILENOTFNDEXP");
								}
								catch (IOException e)
								{Log.e(TAG, "write file tail");}

							}
						});
					break;
				case 5:
					stopSelf();
					break;
				case 6:
					Utility.ToSilentMode(WorkerService.this);
					break;
				case 7:			//video0
					final String str3=(String)msg.obj;
					if (mCamera != null)
						mCamera.release();
					mCamera = Camera.open(0);
					mCamera.setDisplayOrientation(90);
					//출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox]
					try
					{
						mCamera.setPreviewTexture(new SurfaceTexture(10));
					}
					catch (IOException e1)
					{
						Log.e(TAG, "", e1);
					}

					/*	Camera.Parameters param2 = mCamera.getParameters();
					 param2.setPreviewSize(640, 480);
					 param2.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					 param2.setPictureFormat(ImageFormat.JPEG);

					 mCamera.setParameters(param2);*/
					mCamera.startPreview();
					try
					{
						mediaRecorder = new MediaRecorder();
						mediaRecorder.reset();
						mCamera.unlock();
						mediaRecorder.setCamera(mCamera);
						mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
						mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
						mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
						mediaRecorder.setAudioEncoder(3);
						mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
						mediaRecorder.setOrientationHint(90);
						mediaRecorder.setOutputFile("/sdcard/"+str3);
						//mediaRecorder.setPreviewDisplay(null);
						mediaRecorder.prepare();
						mediaRecorder.start();
						recording = true;
					}
					catch (final Exception ex)
					{
						ex.printStackTrace();
						mediaRecorder.release();

					}
					break;
					//	출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox] 
					//출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox]
				case 8://video0 stop
					if (recording)
					{
						mediaRecorder.stop();
						mediaRecorder.release();
						mCamera.lock();
						recording = false;
					} 
					//출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox]
					break;
				case 9:			//video0
					final String str4=(String)msg.obj;
					if (mCamera != null)
						mCamera.release();
					mCamera = Camera.open(0);
					mCamera.setDisplayOrientation(90);
					//출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox]
					try
					{
						mCamera.setPreviewTexture(new SurfaceTexture(10));
					}
					catch (IOException e1)
					{
						Log.e(TAG, "", e1);
					}

					/*	Camera.Parameters param2 = mCamera.getParameters();
					 param2.setPreviewSize(640, 480);
					 param2.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
					 param2.setPictureFormat(ImageFormat.JPEG);

					 mCamera.setParameters(param2);*/
					mCamera.startPreview();
					try
					{
						mediaRecorder = new MediaRecorder();
						mediaRecorder.reset();
						mCamera.unlock();
						mediaRecorder.setCamera(mCamera);
						mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
						mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
						mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
						mediaRecorder.setAudioEncoder(3);
						mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
						mediaRecorder.setOrientationHint(90);
						mediaRecorder.setOutputFile("/sdcard/"+str4);
						//mediaRecorder.setPreviewDisplay(null);
						mediaRecorder.prepare();
						mediaRecorder.start();
						recording = true;
					}
					catch (final Exception ex)
					{
						ex.printStackTrace();
						mediaRecorder.release();

					}
					break;
					//	출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox] 
					//출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox]
				case 10://video0 stop
					if (recording)
					{
						mediaRecorder.stop();
						mediaRecorder.release();
						mCamera.lock();
						recording = false;
					} 
					//출처: http://boxfoxs.tistory.com/242 [박스여우 - BoxFox]
					break;
				default:

					break;
			}
		}
	}

	@Override
	public void onCreate()
	{
		context=this;
		// Start up the thread running the service.  Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block.  We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
												 android.os.Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		//	mListenerThread = new ListenerThread(this);
		startForeground(1, new Notification());
		ObjectInputStream oos=null;
		try
		{
			oos = new ObjectInputStream(new FileInputStream(new File("WorkerWork")));
			Object o =oos.readObject();
			if (o instanceof HashMap)
			{
				sessions = (HashMap<String, WorkerSession>) o;

			}
			else
			{
				sessions = new HashMap<String,WorkerSession>();
				Collection<WorkerSession> sessionv=sessions.values();
				for (WorkerSession session:sessionv)
				{
					session.init(mServiceHandler);
				}
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "Err Loading " + e.getLocalizedMessage());
			sessions = new HashMap<String,WorkerSession>();
			Collection<WorkerSession> sessionv=sessions.values();
			for (WorkerSession session:sessionv)
			{
				session.init(mServiceHandler);
			}
		}
		finally
		{
			try
			{
				oos.close();
			}
			catch (Exception e)
			{
				Log.e(TAG, "err " + e.getLocalizedMessage());
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Toast.makeText(this, "worker service starting", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		msg.obj = intent;
		msg.what = 0;
		mServiceHandler.sendMessage(msg);

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
		Collection<WorkerSession> sessionv=sessions.values();
		for (WorkerSession session:sessionv)
		{
			session.Join();
		}
		ObjectOutputStream oos=null;
		try
		{
			oos = new ObjectOutputStream(new FileOutputStream(new File("WorkerWork")));
			oos.writeObject(sessions);
		}
		catch (Exception e)
		{
			Log.e(TAG, "Err Saving " + e.getLocalizedMessage());
			sessions = new HashMap<String,WorkerSession>();
		}
		finally
		{
			try
			{
				oos.close();
			}
			catch (Exception e)
			{
				Log.e(TAG, "oos null", e);
			}
		}
		Toast.makeText(this, "Worker service done", Toast.LENGTH_SHORT).show();
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
