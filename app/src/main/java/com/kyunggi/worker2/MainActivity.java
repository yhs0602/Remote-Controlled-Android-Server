package com.kyunggi.worker2;

import android.app.*;
import android.app.admin.*;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.provider.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.kyunggi.worker2.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import javax.obex.*;


public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener, OnClickListener
{

	private boolean bListenNow;

	private boolean bListenOnBTEnabled;

	private boolean bListenOnBoot;

	private boolean bSneak=false;
	SharedPreferences setting;
	SharedPreferences.Editor editor;

	private String TAG;

	//출처: http://itmir.tistory.com/393 [미르의 IT 정복기]
	@Override
	public void onClick(View p1)
	{
		// TODO: Implement this method
		if (p1 instanceof Button)
		{
			Button btn=(Button)p1;
			if (btn == BtnSelFile)
			{
				Intent i=new Intent(this, FileSelectorActivity.class);
				startActivityForResult(i, REQUEST_SELECT_FILE);

			}
			else if (btn == BtnSubmit)
			{

			}
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton p1, boolean p2)
	{
		// TODO: Implement this method
		if (p1 instanceof Switch)
		{
			Switch sw=(Switch)p1;
			Switch SwitchSneak = null;
			if (sw == SwitchListen)
			{
				bListenNow = p2;
				if (p2)
				{
					if (TurnOnBluetooth() == false)
					{
						return;
					}
					startListenerService();
				}

				else
					stopListenerService();
			}
			else if (sw == SwitchListenOnBTEnable)
			{
				bListenOnBTEnabled = p2;
			}
			else if (sw == SwitchListenOnBoot)
			{
				bListenOnBoot = p2;

			}
			else if (sw == SwitchSneak)
			{
				Toast.makeText(this, "start admin", 1).show();
				GetAdmin();
				bSneak = p2;
			}
		}
	}

	private void GetAdmin()
	{
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);

		ComponentName componentName = new ComponentName(getApplicationContext(), ShutdownConfigAdminReceiver.class);

		if (!devicePolicyManager.isAdminActive(componentName))
		{
			Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
			startActivityForResult(intent, 0);
		}
	}

	//check notification access setting is enabled or not
/*	public static boolean checkNotificationEnabled() {
		try{
			if(Settings.Secure.getString(MainActivity.getContentResolver(),
										 "enabled_notification_listeners").contains(App.getContext().getPackageName())) 
			{
				return true;
			} else {
				return false;
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	*/
	private void stopListenerService()
	{
		// TODO: Implement this method
		Intent i=new Intent(this, BluetoothOPPService.class);
		stopService(i);
		i=new Intent(this, CompatibleService.class);
		stopService(i);
	}

	private void startListenerService()
	{
		// TODO: Implement this method
		Class<?> classtoIntent=BluetoothOPPService.class;
		if(isHooked())
		{
			classtoIntent=CompatibleService.class;
		}
		Intent intent=new Intent(this,classtoIntent);
		intent.putExtra("com.kyunggi.bSneak", bSneak);
		startService(intent);
	}

	private boolean isHooked()
	{
		// TODO: Implement this method
	/*	BluetoothManager bm=(BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		Class clazz=bm.getClass();
		try
		{
			Method method=clazz.getDeclaredMethod("isWhitelisted", String.class);
			method.setAccessible(true);
			try
			{
				boolean b=method.invoke(bm, "hello");
				return b;
			}
			catch (IllegalAccessException e)
			{
				Log.e(TAG,"",e);
			}
			catch (IllegalArgumentException e)
			{
				Log.e(TAG,"",e);
			}
			catch (InvocationTargetException e)
			{
				Log.e(TAG,"",e);
			}
		}
		catch (NoSuchMethodException e)
		{
			Log.e(TAG,"",e);
		}
		catch (SecurityException e)
		{
			Log.e(TAG,"",e);
		}*/
		File file=new File(Environment.getExternalStorageDirectory(),".hook");
		if(file.isFile())
		{
			return true;
		}
		return false;
	}


	private static final int REQUEST_ENABLE_BT = 123456789;
	private static final int REQUEST_SELECT_FILE = 12345678;

	private BluetoothAdapter mBluetoothAdapter = null;
	String devices="";
	EditText deviceTextEdit;
	Switch SwitchListen;
	Switch SwitchListenOnBoot;
	Switch SwitchListenOnBTEnable;
	Switch SwitchBSneak;
	Button BtnSelFile;
	Button BtnSubmit;
	EditText EditCmd;
	EditText EditSelectedFile;

	private boolean mConnected;


	private boolean fail;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		deviceTextEdit = (EditText) findViewById(R.id.devicesText);
		SwitchListen = (Switch) findViewById(R.id.SwitchListen);
		SwitchListenOnBTEnable = (Switch) findViewById(R.id.SwitchListenOnBTEnabled);
		SwitchListenOnBoot = (Switch) findViewById(R.id.SwitchListenOnBoot);
		SwitchBSneak = (Switch) findViewById(R.id.SwitchBSneak);
		EditSelectedFile = (EditText) findViewById(R.id.EditSelectedFile);
		EditCmd = (EditText) findViewById(R.id.EditCmd);
		BtnSelFile = (Button) findViewById(R.id.BtnSelFile);
		BtnSubmit = (Button) findViewById(R.id.BtnSubmit);

		setting = getSharedPreferences("setting", 0);
		editor = setting.edit();
		bListenOnBoot = setting.getBoolean("ListenOnBoot", false);
		bListenOnBTEnabled = setting.getBoolean("ListenOnBTEnabled", false);
		bListenNow = IsListenerRunning();
		bSneak = setting.getBoolean("BSneak", false);

		SwitchListen.setChecked(bListenNow);
		SwitchListenOnBTEnable.setChecked(bListenOnBTEnabled);
		SwitchListenOnBoot.setChecked(bListenOnBoot);
		SwitchBSneak.setChecked(bSneak);

		SwitchListen.setOnCheckedChangeListener(this);
		SwitchListenOnBTEnable.setOnCheckedChangeListener(this);
		SwitchListenOnBoot.setOnCheckedChangeListener(this);
		SwitchBSneak.setOnCheckedChangeListener(this);

		BtnSelFile.setOnClickListener(this);
		BtnSubmit.setOnClickListener(this);

		// BluetoothAdapter 인스턴스를 얻는다


		//OnBluetoothEnabled();
	}

	@Override
	protected void onResume()
	{
		// TODO: Implement this method
		super.onResume();
		bListenNow = IsListenerRunning();
		SwitchListen.setOnCheckedChangeListener(null);
		SwitchListen.setChecked(bListenNow);
		SwitchListen.setOnCheckedChangeListener(this);
	}

	private boolean IsListenerRunning()
	{
		ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
            if ("com.kyunggi.worker2.BluetoothOPPService".equals(service.service.getClassName()))
			{
                return true;
            }
			if ("com.kyunggi.worker2.CompatibleService".equals(service.service.getClassName()))
			{
                return true;
            }
        }
        return false;
	}

	private boolean TurnOnBluetooth()
	{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null)
		{
			// 단말기는 Bluetooth를 지원하지 않는다

			/*ActivityCompat.(this)*/finishAffinity();
			System.runFinalizersOnExit(true);
			System.exit(0);
		}
		else
		{
			if (!mBluetoothAdapter.isEnabled())
			{
				// Bluetooth는 지원되지만 활성화되어 있지 않다
				// Bluetooth를 활성화하는 인텐트 작성
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// 액티비티 실행
				startActivityForResult(intent, REQUEST_ENABLE_BT);
				return false;
			}
		}
		return true;
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		try
		{
			//mBtSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		editor.putBoolean("ListenOnBoot", bListenOnBoot);
		editor.putBoolean("ListenOnBTEnabled", bListenOnBTEnabled);
		editor.putBoolean("BSneak", bSneak);
		editor.commit();
		/*또는 editor.apply();
		 무조건 이 명령어가 처리되어야 실제로 xml에 값이 기록되게 됩니다

		 기록된 값을 지우는 방법은 아래와 같습니다
		 전체 제거 : editor.clear();
		 부분 제거 : editor.remove(key);
		 */

		//출처: http://itmir.tistory.com/393 [미르의 IT 정복기] 

		//Preferences pref;
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_ENABLE_BT)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				// Bluetooth가 활성화되었음을 표시
				try
				{
					Thread.sleep((long)5000);
				}
				catch (InterruptedException e)
				{}
				startListenerService();
				//OnBluetoothEnabled();
			}
			else
			{
				this.finishActivity(0);
				return ;
				// Bluetooth를 활성화할 수 없음 (사용자가 취소한 경우 등)
			}
		}
		else if (requestCode == REQUEST_SELECT_FILE)
		{
			if (resultCode == Activity.RESULT_OK)
			{
				String path=data.getStringExtra("com.kyunggi.worker2.path");
				File file=new File(path);
				String fileData="";
				String s;
				try
				{
					BufferedReader reader=new BufferedReader(new FileReader(file));
					try
					{
						while ((s = reader.readLine()) != null)
						{
							fileData += s;
							fileData += "\n";
						}
					}
					catch (IOException e)
					{}
				}
				catch (FileNotFoundException e)
				{}
				EditCmd.setText(fileData);
				EditSelectedFile.setText(file.getAbsolutePath());
			}
		}
	} 

	ClientSession mSession;
	BluetoothObexTransport mTransport;

//	private void StartServer()
//	{/*
//		 new ExecuteAsRootBase(){
//
//		 @Override
//		 protected ArrayList<String> getCommandsToExecute()
//		 {
//		 // TODO: Implement this method
//		 ArrayList<String> list=new ArrayList<String>();
//		 list.add("pwd");
//		 return list;
//		 }
//		 }.execute();*/
//		BluetoothServerSocket serversocket;
//		try
//		{
//			Log.e("WORKER", "trying");
//
//			serversocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Insecure OPP", UUID.fromString("00001105-0000-1000-8000-00805f9b34fb"));
//			serversocket.close();
//			serversocket = null;
//		}
//		catch (IOException e)
//		{
//			fail = true;
//			Log.e("WORKER", "Failed to get socket" + e.getMessage());
//		}
//		/*method using the OPP UUID */
//
////		bindService(new Intent(), new ServiceConnection(){
////
////				@Override
////				public void onServiceConnected(ComponentName p1, IBinder p2)
////				{
////					// TODO: Implement this method
////				}
////
////				@Override
////				public void onServiceDisconnected(ComponentName p1)
////				{
////					// TODO: Implement this method
////				}
////			}, 1);
//	}
//
//	private void OnBluetoothEnabled()
//	{
//		// 페어링된 디바이스 목록을 얻는다 
//		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//		for (BluetoothDevice device : pairedDevices)
//		{
//			// 디바이스 이름과 MAC 주소
//			devices += device.getName() + device.getAddress();
//			if (device.getName().compareToIgnoreCase("DEADDEADh=4021219246") == 0)
//			{
//				mBtDevice = device;
//				break;
//			}
//		}
////		String parcels="";
////		ParcelUuid[] uuids=mBtDevice.getUuids();
////		int i=0;
////		for (ParcelUuid p:uuids)
////		{
////			parcels += "UUID UUID" + new Integer(i).toString() + "=UUID.fromString((\"" + p.getUuid().toString() + "\"));\n\n";
////			++i;
////		}
//		//deviceTextEdit.setText(parcels);
//		// MAC 주소에서 BluetoothDevice 인스턴스를 얻는다 (주소변환 필요)
//		//mBtDevice = mBluetoothAdapter.getRemoteDevice("00:00:00:00:00:00");
//		/*
//		 00001101-0000-1000-8000-00805f9b34fb
//
//		 0000110a-0000-1000-8000-00805f9b34fb
//
//		 00001105-0000-1000-8000-00805f9b34fb
//
//		 00001106-0000-1000-8000-00805f9b34fb
//
//		 0000110e-0000-1000-8000-00805f9b34fb
//
//		 0000112f-0000-1000-8000-00805f9b34fb
//
//		 00001112-0000-1000-8000-00805f9b34fb
//
//		 0000111f-0000-1000-8000-00805f9b34fb
//
//		 00000000-0000-1000-8000-00805f9b34fb
//
//		 The OBEX connection must use a Target header set to the File Browsing UUID, (F9EC7BC4-953C-11D2-984E-525400DC9E09). This UUID is sent in binary (16 bytes) with 0xF9 sent first. OBEX authentication can optionally be used.
//		 *//*
//		 UUID UUID0=UUID.fromString(("00001101-0000-1000-8000-00805f9b34fb"));//Pc연결
//
//		 UUID UUID1=UUID.fromString(("0000110a-0000-1000-8000-00805f9b34fb"));//AUDIO
//
//		 UUID UUID2=UUID.fromString(("00001105-0000-1000-8000-00805f9b34fb"));//OPP
//		 */
//		UUID UUID3=UUID.fromString(("00001106-0000-1000-8000-00805f9b34fb"));//OBEX FTP
//		/*
//		 UUID UUID4=UUID.fromString(("0000110e-0000-1000-8000-00805f9b34fb"));//AdvancedAudioDistributionServiceClass_UUID
//
//		 UUID UUID5=UUID.fromString(("0000112f-0000-1000-8000-00805f9b34fb"));//PBAP
//
//		 UUID UUID6=UUID.fromString(("00001112-0000-1000-8000-00805f9b34fb"));//HeadsetAudioGatewayServiceClass_UUID
//
//		 UUID UUID7=UUID.fromString(("0000111f-0000-1000-8000-00805f9b34fb"));//HandsfreeAudioGatewayServiceClass_UUID
//
//		 UUID UUID8=UUID.fromString(("00000000-0000-1000-8000-00805f9b34fb"));//BASE UUID
//
//
//		 UUID uuidRFCOMM=UUID.fromString(("00000003-0000-1000-8000-00805F9B34FB"));///*OBEX*//*"00000008-0000-1000-8000-00805F9B34FB"));/*FTP"0000000A-0000-1000-8000-00805F9B34FB"));
//		 UUID uuidFTP=UUID.fromString(("0000000A-0000-1000-8000-00805F9B34FB"));
//		 UUID uuidOBEX=UUID.fromString(("00000008-0000-1000-8000-00805F9B34FB"));///*FTP"0000000A-0000-1000-8000-00805F9B34FB"));
//		 */
//		//StartServer();
//		//GetFileNamesViaBTFTP(UUID3);
//	}
//
	private void GetFileNamesViaBTFTP(UUID UUID3)
	{
		try
		{
			// 연결에 사용할 프로파일을 지정하여 BluetoothSocket 인스턴스를 얻는다
			// 이 예에서는 SPP의 UUID를 사용한다
			mBtSocket = mBtDevice.createInsecureRfcommSocketToServiceRecord(UUID3);
		}
		catch (Exception e)
		{

			//e.printStackTrace();
		}

		Thread thread=new Thread(new Runnable() {
				public void run()
				{
					UUID uuid=UUID.fromString("F9EC7BC4-953C-11D2-984E-525400DC9E09");
					ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
					bb.putLong(uuid.getMostSignificantBits());
					bb.putLong(uuid.getLeastSignificantBits());
					byte [] bytes=bb.array();
					Operation putOperation=null;
					Operation getOperation=null;
					//ArrayUtils.reverse(bytes);
					try
					{
						// 소켓을 연결한다
						mBtSocket.connect();
						mSession = new ClientSession((ObexTransport)(mTransport = new BluetoothObexTransport(mBtSocket)));

						HeaderSet headerset = new HeaderSet();
						headerset.setHeader(HeaderSet.TARGET, bytes);

						headerset = mSession.connect(headerset);

						if (headerset.getResponseCode() == ResponseCodes.OBEX_HTTP_OK)
						{
							mConnected = true;
						}
						else
						{
							mSession.disconnect(headerset);
						}
						// Send a file with meta data to the server
						final byte filebytes[] = "[CLIENT] Hello..".getBytes();
						final HeaderSet hs = new HeaderSet();
						hs.setHeader(HeaderSet.NAME, "test.txt");
						hs.setHeader(HeaderSet.TYPE, "text/plain");
						hs.setHeader(HeaderSet.LENGTH, new Long((long)filebytes.length));

						putOperation = mSession.put(hs);

						mOutput = putOperation.openOutputStream();
						mOutput.write(filebytes);
						mOutput.close();
						putOperation.close();
						//In order to go the desired folder the OBEX SETPATH command is 
						//being used 
						//Prepare the header for the SETPATH command
						HeaderSet header = new HeaderSet(); 
						//folder_name is set to the name of the desired folder 
						//if left blank the root folder will be used 
						//header.setHeader(HeaderSet.NAME, ""); 
						//Send the SETPATH command 
						/*result =*/ mSession.setPath(header, false, false); 
						final HeaderSet geths = new HeaderSet();
						//geths.setHeader(HeaderSet.NAME, null);
						geths.setHeader(HeaderSet.TYPE, "x-obex/folder-listing");
						//hs.setHeader(HeaderSet.LENGTH, new Long((long)filebytes.length));

						getOperation = mSession.get(geths);
						InputStreamReader din = new 
							InputStreamReader(getOperation.openInputStream(), "UTF-8"); 

						BufferedReader bufferedReader = new BufferedReader(din); 
						String tmp2=new String();
						String line = bufferedReader.readLine(); 
						while (line != null)
						{ 
							tmp2 += line;//System.out.println(line); 
							line = bufferedReader.readLine(); 
						} 
						bufferedReader.close(); 

						getOperation.close();  
						/*
						 mInput=getOperation.openInputStream();
						 // Retrieve the length of the object being sent back
						 int length = (int) getOperation.getLength();
					     // Create space for the object
						 byte[] obj = new byte[length];
					     // Get the object from the input stream
						 DataInputStream in = getOperation.openDataInputStream();
						 in.read(obj);

						 // End the transaction
						 in.close();
						 */
						final String ftmp=tmp2;
						runOnUiThread(new Runnable(){
								@Override
								public void run()
								{
									//String s=new String(ftmp, "UTF-16");
									deviceTextEdit.setText(ftmp);
								}
							});
						Xml xml;
					}
					catch (Exception e)
					{
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
						catch (IOException e)
						{}
						//updateStatus("[CLIENT] Connection Closed");
					}

				}
			});
		thread.start();
	}

	private BluetoothDevice mBtDevice;
	private BluetoothSocket mBtSocket;
	private InputStream mInput;
	private OutputStream mOutput;


	/*   Class c;
	 Instrumentation i;

	 public static void agentmain(String arg, Instrumentation inst)
	 throws Exception {
	 // only if header utility is on the class path; otherwise,
	 // a class can be found within any class loader by iterating
	 // over the return value of Instrumentation::getAllLoadedClasses
	 Class<?> headerUtility = Class.forName("HeaderUtility");

	 // copy the contents of typo.fix into a byte array
	 ByteArrayOutputStream output = new ByteArrayOutputStream();
	 try (InputStream input =
	 BugFixAgent.class.getResourceAsStream("/typo.fix")) {
	 byte[] buffer = new byte[1024];
	 int length;
	 while ((length = input.read(buffer)) != -1) {
	 output.write(buffer, 0, length);
	 }
	 }

	 // Apply the redefinition
	 instrumentation.redefineClasses(
	 new ClassDefinition(headerUtility, output.toByteArray()));
	 }*/

}
	//BluetoothDevice.listenUsingRfcommWithServiceRecord(UUID.fromString("00001105-0000-1000-8000-00805f9b34fb"));
	/*method using the OPP UUID */


