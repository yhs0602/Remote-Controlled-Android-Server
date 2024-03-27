package com.kyunggi.worker;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import javax.obex.*;
import org.apache.commons.lang3.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import android.util.*;


public class MainActivity extends Activity 
{

	private static final int REQUEST_ENABLE_BT = 123456789;
	private BluetoothAdapter mBluetoothAdapter = null;
	String devices="";
	EditText deviceTextEdit;

	private boolean mConnected;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		deviceTextEdit=(EditText) findViewById(R.id.devicesText);

		// BluetoothAdapter 인스턴스를 얻는다
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// 단말기는 Bluetooth를 지원하지 않는다
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				// Bluetooth는 지원되지만 활성화되어 있지 않다
				// Bluetooth를 활성화하는 인텐트 작성
				Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// 액티비티 실행
				startActivityForResult(intent, REQUEST_ENABLE_BT);
			}
		}
		// 페어링된 디바이스 목록을 얻는다
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		for (BluetoothDevice device : pairedDevices) {
			// 디바이스 이름과 MAC 주소
			devices+= device.getName()+ device.getAddress();
			if(device.getName().compareToIgnoreCase("DEADDEADh=4021219246")==0)
			{
				mBtDevice=device;
				break;
			}
		}
		String parcels="";
		ParcelUuid[] uuids=mBtDevice.getUuids();
		int i=0;
		for(ParcelUuid p:uuids)
		{
			parcels+="UUID UUID"+new Integer(i).toString()+"=UUID.fromString((\""+p.getUuid().toString()+"\"));\n\n";
			++i;
		}
		//deviceTextEdit.setText(parcels);
		// MAC 주소에서 BluetoothDevice 인스턴스를 얻는다 (주소변환 필요)
		//mBtDevice = mBluetoothAdapter.getRemoteDevice("00:00:00:00:00:00");
		/*
		 00001101-0000-1000-8000-00805f9b34fb

		 0000110a-0000-1000-8000-00805f9b34fb

		 00001105-0000-1000-8000-00805f9b34fb

		 00001106-0000-1000-8000-00805f9b34fb

		 0000110e-0000-1000-8000-00805f9b34fb

		 0000112f-0000-1000-8000-00805f9b34fb

		 00001112-0000-1000-8000-00805f9b34fb

		 0000111f-0000-1000-8000-00805f9b34fb

		 00000000-0000-1000-8000-00805f9b34fb

		 The OBEX connection must use a Target header set to the File Browsing UUID, (F9EC7BC4-953C-11D2-984E-525400DC9E09). This UUID is sent in binary (16 bytes) with 0xF9 sent first. OBEX authentication can optionally be used.
		 */
		UUID UUID0=UUID.fromString(("00001101-0000-1000-8000-00805f9b34fb"));//Pc연결

		UUID UUID1=UUID.fromString(("0000110a-0000-1000-8000-00805f9b34fb"));//AUDIO

		UUID UUID2=UUID.fromString(("00001105-0000-1000-8000-00805f9b34fb"));//OPP

		UUID UUID3=UUID.fromString(("00001106-0000-1000-8000-00805f9b34fb"));//OBEX FTP

		UUID UUID4=UUID.fromString(("0000110e-0000-1000-8000-00805f9b34fb"));//AdvancedAudioDistributionServiceClass_UUID

		UUID UUID5=UUID.fromString(("0000112f-0000-1000-8000-00805f9b34fb"));//PBAP

		UUID UUID6=UUID.fromString(("00001112-0000-1000-8000-00805f9b34fb"));//HeadsetAudioGatewayServiceClass_UUID

		UUID UUID7=UUID.fromString(("0000111f-0000-1000-8000-00805f9b34fb"));//HandsfreeAudioGatewayServiceClass_UUID

		UUID UUID8=UUID.fromString(("00000000-0000-1000-8000-00805f9b34fb"));//BASE UUID


		UUID uuidRFCOMM=UUID.fromString(("00000003-0000-1000-8000-00805F9B34FB"));///*OBEX*//*"00000008-0000-1000-8000-00805F9B34FB"));/*FTP*/"0000000A-0000-1000-8000-00805F9B34FB"));
		UUID uuidFTP=UUID.fromString((/*FTP*/"0000000A-0000-1000-8000-00805F9B34FB"));
		UUID uuidOBEX=UUID.fromString((/*OBEX*/"00000008-0000-1000-8000-00805F9B34FB"));///*FTP*/"0000000A-0000-1000-8000-00805F9B34FB"));

		try {
			// 연결에 사용할 프로파일을 지정하여 BluetoothSocket 인스턴스를 얻는다
			// 이 예에서는 SPP의 UUID를 사용한다
			mBtSocket = mBtDevice.createInsecureRfcommSocketToServiceRecord(UUID3);
		} catch(Exception e) {

			//e.printStackTrace();
		}
		final byte[] response=new byte[512];
	    final String resp=new String();
		new Thread(new Runnable() {
				public void run() {
					UUID uuid=UUID.fromString("F9EC7BC4-953C-11D2-984E-525400DC9E09");
					ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
					bb.putLong(uuid.getMostSignificantBits());
					bb.putLong(uuid.getLeastSignificantBits());
					byte [] bytes=bb.array();
					Operation putOperation=null;
					Operation getOperation=null;
					//ArrayUtils.reverse(bytes);
					try {
						// 소켓을 연결한다
						mBtSocket.connect();
						mSession = new ClientSession((ObexTransport)(mTransport=new BluetoothObexTransport(mBtSocket)));

						HeaderSet headerset = new HeaderSet();
						headerset.setHeader(HeaderSet.TARGET,bytes);

						headerset = mSession.connect(headerset);

						if (headerset.getResponseCode() == ResponseCodes.OBEX_HTTP_OK) {
							mConnected = true;
						} else {
							mSession.disconnect(headerset);
						}
						// Send a file with meta data to the server
						final byte filebytes[] = "[CLIENT] Hello..".getBytes();
						final HeaderSet hs = new HeaderSet();
						hs.setHeader(HeaderSet.NAME, "test.txt");
						hs.setHeader(HeaderSet.TYPE, "text/plain");
						hs.setHeader(HeaderSet.LENGTH, new Long((long)filebytes.length));

						putOperation = mSession.put(hs);
						//updateStatus("[CLIENT] Pushing file: " + "test.txt");
						//updateStatus("[CLIENT] Total file size: "
									// + filebytes.length + " bytes");

						mOutput= putOperation.openOutputStream();
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
						
						getOperation=mSession.get(geths);
						InputStreamReader din = new 
							InputStreamReader(getOperation.openInputStream(),"UTF-8"); 

						BufferedReader bufferedReader = new BufferedReader(din); 
						String tmp2=new String();
						String line = bufferedReader.readLine(); 
						while (line != null) { 
							tmp2+=line;//System.out.println(line); 
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
						     getOperation.close();
							 final byte[] tmp=bytes;*/
							 final String ftmp=tmp2;
						runOnUiThread(new Runnable(){

								@Override
								public void run()
								{
									// TODO: Implement this method
									
										//String s=new String(ftmp, "UTF-16");
										deviceTextEdit.setText(ftmp);
									
									
								}
							});
							Xml xml;
							//String s=new String();
							//resp.concat(obj.toString());
						//updateStatus("[CLIENT] File push complete");
						/*
						 // 입출력을 위한 스트림 오브젝트를 얻는다
						 mInput = mBtSocket.getInputStream();
						 mOutput = mBtSocket.getOutputStream();
						 //connect
						 mOutput.write(0x80);//connect
						 mOutput.write(0x00);//size1
						 mOutput.write(0x1E);//size2
						 mOutput.write(0x10);//OBEX ver
						 mOutput.write(0x00);//flags
						 mOutput.write(0x20);//maxsize
						 mOutput.write(0x00);//maxsize
						 /*
						 mOutput.write(0x46);//HI for target header
						 mOutput.write(0x00);
						 mOutput.write(0x13);
						 UUID uuid=UUID.fromString("F9EC7BC4-953C-11D2-984E-525400DC9E09");
						 ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
						 bb.putLong(uuid.getMostSignificantBits());
						 bb.putLong(uuid.getLeastSignificantBits());
						 byte [] bytes=bb.array();
						 ArrayUtils.reverse(bytes);
						 mOutput.write(bytes);
						 */
						//mInput.read(response,0,512);
						//disconnect
						//mOutput.write(0x81);
						//mOutput.write(0x00);
						//mOutput.write(0x03);

						//while(true) {
						// 입력 데이터를 그대로 출력한다
						//mOutput.write(mInput.read());
						//}
					} catch(Exception e) {
						//e.printStackTrace();
					}
					finally {
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
			}).start();
	/*	try
		{
			//Thread.sleep(10000);
		}
		catch (InterruptedException e)
		{}*/
		//deviceTextEdit.setText(resp);//Arrays.toString(response));

	}

	private BluetoothDevice mBtDevice;
	private BluetoothSocket mBtSocket;
	private InputStream mInput;
	private OutputStream mOutput;

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			mBtSocket.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth가 활성화되었음을 표시 
			} else {
				this.finishActivity(0);
				// Bluetooth를 활성화할 수 없음 (사용자가 취소한 경우 등)
			}
		}
	} 
	/*
	 private void connect() {
	 try {
	 mSession = new ClientSession((ObexTransport)mTransport);

	 HeaderSet headerset = new HeaderSet();
	 headerset.setHeader(HeaderSet.TARGET, MAS_TARGET);

	 headerset = mSession.connect(headerset);

	 if (headerset.getResponseCode() == ResponseCodes.OBEX_HTTP_OK) {
	 mConnected = true;
	 } else {
	 mSession.disconnect(headerset);
	 }
	 } catch (IOException e) {
	 }
	 }*/
	ClientSession mSession;
	BluetoothObexTransport mTransport;

}

