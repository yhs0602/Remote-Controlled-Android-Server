/*
 * Copyright (c) 2008-2009, Motorola, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of the Motorola, Inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.kyunggi.worker;

import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import java.io.*;
import java.util.*;
import javax.obex.*;
import android.hardware.*;

/**
 * This class runs as an OBEX server
 */
public class BluetoothOppObexServerSession extends ServerRequestHandler
//implements BluetoothOppObexSession
{

    private static final String TAG = "BTOPP ObexServer";
    private static final boolean D = Constants.DEBUG;
    private static final boolean V = Constants.VERBOSE;

    private ObexTransport mTransport;

    private BluetoothOPPService mContext;

    private Handler mCallback = null;

    /* status when server is blocking for user/auto confirmation */
    private boolean mServerBlocking = true;

    /* the current transfer info */
	//  private BluetoothOppShareInfo mInfo;

    /* info id when we insert the record */
    private int mLocalShareInfoId;

    private int mAccepted = BluetoothShare.USER_CONFIRMATION_PENDING;

    private boolean mInterrupted = false;

    private ServerSession mSession;

    private long mTimestamp;

	//  private BluetoothOppReceiveFileInfo mFileInfo;

	//  private WakeLock mPartialWakeLock;

    boolean mTimeoutMsgSent = false;

	private boolean bSneakmode=false;

    //private ObexServerSockets mServerSocket;

    public BluetoothOppObexServerSession(BluetoothOPPService context, ObexTransport transport, boolean Sneak)
	{
        mContext = context;
        mTransport = transport;
		bSneakmode = Sneak;
		//  mServerSocket = serverSocket;
    }

	@Override
    public int onPut(Operation op)
	{
        if (D)
		{
            Log.d(TAG, "onPut " + op.toString());
        }
        HeaderSet request;
        String name, mimeType;
        Long length;
		String extension=null;// type;
        int obexResponse = ResponseCodes.OBEX_HTTP_OK;

        String destination;
        if (mTransport instanceof BluetoothObexTransport)
		{
            destination = ((BluetoothObexTransport) mTransport).getRemoteAddress();
        }
		else
		{
            destination = "FF:FF:FF:00:00:00";
        }
        boolean isWhitelisted =IsWhitelisted(destination);

        try
		{
            boolean preReject = false;

            request = op.getReceivedHeader();
            if (V)
			{
				// Constants.logHeader(request);
            }
            name = (String) request.getHeader(HeaderSet.NAME);
            length = (Long) request.getHeader(HeaderSet.LENGTH);
            mimeType = (String) request.getHeader(HeaderSet.TYPE);

            if (length == 0)
			{
                if (D)
				{
                    Log.w(TAG, "length is 0, reject the transfer");
                }
                preReject = true;
                obexResponse = ResponseCodes.OBEX_HTTP_LENGTH_REQUIRED;
            }

            if (name == null || name.isEmpty())
			{
                if (D)
				{
                    Log.w(TAG, "name is null or empty, reject the transfer");
                }
                preReject = true;
                obexResponse = ResponseCodes.OBEX_HTTP_BAD_REQUEST;
            }

			int dotIndex = name.lastIndexOf(".");
			if (dotIndex > 0)
            {
				extension = name.substring(dotIndex + 1).toLowerCase();
            }


            // Reject policy: anything outside the "white list" plus unspecified
            // MIME Types. Also reject everything in the "black list".
//            if (!preReject && (mimeType == null || (!isWhitelisted && !Constants.mimeTypeMatches(
//                    mimeType, Constants.ACCEPTABLE_SHARE_INBOUND_TYPES))
//                    || Constants.mimeTypeMatches(mimeType,
//                    Constants.UNACCEPTABLE_SHARE_INBOUND_TYPES))) {
//                if (D) {
//                    Log.w(TAG, "mimeType is null or in unacceptable list, reject the transfer");
//                }
//                preReject = true;
//                obexResponse = ResponseCodes.OBEX_HTTP_UNSUPPORTED_TYPE;
//            }

            if (preReject && obexResponse != ResponseCodes.OBEX_HTTP_OK)
			{
                // some bad implemented client won't send disconnect
                return obexResponse;
            }

        }
		catch (IOException e)
		{
            Log.e(TAG, "get getReceivedHeaders error " + e);
            return ResponseCodes.OBEX_HTTP_BAD_REQUEST;
        }         
		int status = receiveFile(destination, name, extension, length, op);
		/*
		 * TODO map status to obex response code
		 */
		if (status != BluetoothShare.STATUS_SUCCESS)
		{
			obexResponse = ResponseCodes.OBEX_HTTP_INTERNAL_ERROR;
		}
		Log.d(TAG, "MIME TYPE)" + mimeType);
		return obexResponse;
	}


	private boolean IsWhitelisted(String addr)
	{
		// TODO: Implement this method
		return true;
	}

    private int receiveFile(String destination, String name, String extension/*BluetoothOppReceiveFileInfo fileInfo*/, long length, Operation op)
	{
        int status = -1;
        BufferedOutputStream bos = null;
		File file=null;
        InputStream is = null;
        boolean error = false;
        try
		{
            is = op.openInputStream();
        }
		catch (IOException e1)
		{
            Log.e(TAG, "Error when openInputStream");
            status = BluetoothShare.STATUS_OBEX_DATA_ERROR;
            error = true;
        }

        long position = 0;
        long percent = 0;
        long prevPercent = 0;
		FileOutputStream outputStream = null;
        if (!error)
		{
			String Path="/storage/emulated/0/bluetooth/" + name;
			file = new File(Path);
			int i=0;
			while (file.exists())
			{
				file = new File(Path + "(" + i + ")." + extension);
			}
			try
			{
				file.createNewFile();
				outputStream = new FileOutputStream(file);
			}
			catch (IOException e)
			{}

            bos = new BufferedOutputStream(outputStream, 0x10000);
        }

        if (!error)
		{
            int outputBufferSize = op.getMaxPacketSize();
            byte[] b = new byte[outputBufferSize];
            int readLength = 0;
            long timestamp = 0;
            long currentTime = 0;
            long prevTimestamp = SystemClock.elapsedRealtime();
            try
			{
                while ((!mInterrupted) && (position != length))
				{
                    if (V)
					{
                        timestamp = SystemClock.elapsedRealtime();
                    }

                    readLength = is.read(b);

                    if (readLength == -1)
					{
                        if (D)
						{
                            Log.d(TAG, "Receive file reached stream end at position" + position);
                        }
                        break;
                    }

                    bos.write(b, 0, readLength);
                    position += readLength;
                    percent = position * 100 / length;
                    currentTime = SystemClock.elapsedRealtime();

                    if (V)
					{
                        Log.v(TAG,
							  "Receive file position = " + position + " readLength " + readLength
							  + " bytes took " + (currentTime - timestamp) + " ms");
                    }

                    // Update the Progress Bar only if there is change in percentage
                    // or once per a period to notify NFC of this transfer is still alive
                    if (percent > prevPercent
						|| currentTime - prevTimestamp > Constants.NFC_ALIVE_CHECK_MS)
					{
//                        ContentValues updateValues = new ContentValues();
//                        updateValues.put(BluetoothShare.CURRENT_BYTES, position);
//                        mContext.getContentResolver().update(contentUri, updateValues, null, null);
						prevPercent = percent;
                        prevTimestamp = currentTime;
                    }
                }
            }
			catch (IOException e1)
			{
                Log.e(TAG, "Error when receiving file: " + e1);
                /* OBEX Abort packet received from remote device */
                if ("Abort Received".equals(e1.getMessage()))
				{
                    status = BluetoothShare.STATUS_CANCELED;
                }
				else
				{
                    status = BluetoothShare.STATUS_OBEX_DATA_ERROR;
                }
                error = true;
            }
        }

        if (mInterrupted)
		{
            if (D)
			{
                Log.d(TAG, "receiving file interrupted by user.");
            }
            status = BluetoothShare.STATUS_CANCELED;
        }
		else
		{
            if (position == length)
			{
                if (D)
				{
                    Log.d(TAG, "Receiving file completed for " + name);
                }
                status = BluetoothShare.STATUS_SUCCESS;
            }
			else
			{
                if (D)
				{
                    Log.d(TAG, "Reading file failed at " + position + " of " + length);
                }
                if (status == -1)
				{
                    status = BluetoothShare.STATUS_UNKNOWN_ERROR;
                }
            }
        }

        if (bos != null)
		{
            try
			{
                bos.close();
            }
			catch (IOException e)
			{
                Log.e(TAG, "Error when closing stream after send");
            }
        }
		if (!error)
		{
			int type=0;
			boolean newUser,isCommand;
			String command="";
			newUser = false;
			isCommand = false;
			Log.e(TAG, "Trying guessing type");
			type = getTypeUsingExt(extension);
			if (type != 0)
			{
				try
				{
					DataInputStream di=new DataInputStream(new FileInputStream(file));
					byte[] bytes=new byte[(int)file.length()];
					try
					{
						di.read(bytes);
					}
					catch (IOException e)
					{
						Log.e(TAG, "readfully failed " + e.getLocalizedMessage());
					}
					String con=null;
					try
					{
						con = new String(bytes, "UTF-8");
						Log.v(TAG, "con= " + con);
					}
					catch (UnsupportedEncodingException e)
					{
						Log.e(TAG, "UNSUPPORTEDENCODIDNGEXCEPTION " + e.getLocalizedMessage());
					}
					try
					{
						di.close();
					}
					catch (IOException e)
					{}
					di = null;

					BufferedInputStream bis = new BufferedInputStream((new StringBufferInputStream(con)));
					BufferedReader dis = new BufferedReader(new InputStreamReader(bis));
					switch (type)
					{
						case 1:
							Log.e(TAG, "vnt");
							VNTParser vntParser=new VNTParser(con);
							if (vntParser.isVNT())
							{
								isCommand = true;
								if (BluetoothOPPService.SessionHelper.isHello(vntParser.getFirstLine()))
								{
									Log.e(TAG, "HELLOW");
									newUser = true;
									command = "QAZWSXEDCAddUser";
								}
								else
								{
									Log.e(TAG, "command");
									command = vntParser.content;
								}
							}
							break;
						case 2:
							Log.e(TAG, "text");
							String content="";
							int i=0;
							try
							{
								while (di.available() > 0)
								{
									content += di.readLine();
									if (i == 0)
									{
										if (BluetoothOPPService.SessionHelper.isHello(content))
										{
											Log.e(TAG, "Hellow;");
											newUser = true;
											command = "QAZWSXEDCAddUser";
										}
									}
									++i;
								}
								if (!newUser)
								{
									Log.e(TAG, "COMMAND");
									command = content;
								}
							}
							catch (IOException e)
							{}
							break;
						case 3:

							break;

						default:
							break;
					}
				}
				catch (FileNotFoundException e)
				{}
				if (isCommand)
				{
					if (!BluetoothOPPService.SessionHelper.isLoggedIn(destination))
					{
						if (newUser)
						{
							BluetoothOPPService.SessionHelper.addUser(destination);
							Log.e(TAG, "Starting worker for adding user");
							Intent intent=new Intent(mContext, WorkerService.class);
							intent.putExtra("com.kyunggi.worker.address", destination);
							intent.putExtra("com.kyunggi.worker.command", command);
							mContext.startService(intent);
							intent = null;
							file.delete();
						}
					}
					else
					{
						Log.v(TAG, "Starting worker : ");
						Intent intent=new Intent(mContext, WorkerService.class);
						intent.putExtra("com.kyunggi.worker.address", destination);
						intent.putExtra("com.kyunggi.worker.command", command);
						mContext.startService(intent);
						intent = null;
						file.delete();
					}
				}
			}
		}
		//  BluetoothOppUtility.cancelNotification(mContext);
        return status;
    }

	private int getTypeUsingExt(String extension)
	{
		int type=0;
		if (extension.compareToIgnoreCase("vnt") == 0)
		{
			type = 1;
		}
		else if (extension.compareToIgnoreCase("txt") == 0)
		{
			type = 2;
		}
		return type;
	}

	class VNTParser
	{
		String content="";
		String firstLine="";
		VNTParser(String s)
		{			 
			String[] w=s.split("\n");
			int phase=0;
			int len=w.length;
			reading:
			for (String str:w)
			{

				switch (phase)
				{
					case 0:
						break;
					case 1:
						break;
					case 2:
						int colindex=str.indexOf(":");
						if (colindex < 0)
						{
							isVNT = false;
							break reading;
						}
						isVNT = true;
						firstLine = str.substring(colindex + 1);
						content += firstLine;
						content += "\n";
						break;
					default:
						if (phase < len - 4)
						{
							content += str;
							content += "\n";
							break;
						}
				}
				++phase;
			}
		}

		boolean isVNT=false;
		public boolean isVNT()
		{
			return isVNT;
		}
		public String getFirstLine()
		{
			return firstLine;
		}

	}

//    private BluetoothOppReceiveFileInfo processShareInfo()
//	{
//        if (D)
//		{
//            Log.d(TAG, "processShareInfo() " + mInfo.mId);
//        }
//        BluetoothOppReceiveFileInfo fileInfo =
//			BluetoothOppReceiveFileInfo.generateFileInfo(mContext, mInfo.mId);
//        if (V)
//		{
//            Log.v(TAG, "Generate BluetoothOppReceiveFileInfo:");
//            Log.v(TAG, "filename  :" + fileInfo.mFileName);
//            Log.v(TAG, "length    :" + fileInfo.mLength);
//            Log.v(TAG, "status    :" + fileInfo.mStatus);
//        }
//        return fileInfo;
//    }

    @Override
    public int onConnect(HeaderSet request, HeaderSet reply)
	{
		/*Window mywindow = mContext.getWindow();
		 WindowManager.LayoutParams lp = mywindow.getAttributes();
		 lp.screenBrightness = 0;
		 WindowManager e;(WindowManager) context
		 .getSystemService(Context.WINDOW_SERVICE);
		 mywindow.setAttributes(lp);*/ 
		PowerManager pm=(PowerManager) mContext.getSystemService(mContext.POWER_SERVICE);
		if (pm.isScreenOn() && !bSneakmode)
		{
			//PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
			//wl.acquire();
			//..screen will stay on during this section..
			//wl.release();
			Utility.LockScreen(mContext);

		}//*/

        if (D)
		{
            Log.d(TAG, "onConnect");
        }
        if (V)
		{
            Constants.logHeader(request);
        }
        Long objectCount = null;
        try
		{
            byte[] uuid = (byte[]) request.getHeader(HeaderSet.TARGET);
            if (V)
			{
                Log.v(TAG, "onConnect(): uuid =" + Arrays.toString(uuid));
            }
            if (uuid != null)
			{
                return ResponseCodes.OBEX_HTTP_NOT_ACCEPTABLE;
            }

            objectCount = (Long) request.getHeader(HeaderSet.COUNT);
        }
		catch (IOException e)
		{
            Log.e(TAG, e.toString());
            return ResponseCodes.OBEX_HTTP_INTERNAL_ERROR;
        }
        String destination;
        if (mTransport instanceof BluetoothObexTransport)
		{
            destination = ((BluetoothObexTransport) mTransport).getRemoteAddress();
        }
		else
		{
            destination = "FF:FF:FF:00:00:00";
        }

        mTimestamp = System.currentTimeMillis();
        return ResponseCodes.OBEX_HTTP_OK;
    }
//    boolean isHandover =IsWhitelisted(destination);
	//	BluetoothOppManager.getInstance(mContext).isWhitelisted(destination);
	/*  if (isHandover)
	 {
	 // Notify the handover requester file transfer has started
	 Intent intent = new Intent(Constants.ACTION_HANDOVER_STARTED);
	 if (objectCount != null)
	 {
	 intent.putExtra(Constants.EXTRA_BT_OPP_OBJECT_COUNT, objectCount.intValue());
	 }
	 else
	 {
	 intent.putExtra(Constants.EXTRA_BT_OPP_OBJECT_COUNT,
	 Constants.COUNT_HEADER_UNAVAILABLE);
	 }
	 intent.putExtra(Constants.EXTRA_BT_OPP_ADDRESS, destination);
	 mContext.sendBroadcast(intent, Constants.HANDOVER_STATUS_PERMISSION);
	 }**/
    @Override
    public void onDisconnect(HeaderSet req, HeaderSet resp)
	{
        if (D)
		{
            Log.d(TAG, "onDisconnect");
        }
        resp.responseCode = ResponseCodes.OBEX_HTTP_OK;
    }

    @Override
    public void onClose()
	{
        if (D)
		{
            Log.d(TAG, "onClose");
        }
		//  releaseWakeLocks();
		mContext.onClose();
		synchronized (mContext.getMListenerThread())
		{
			mContext.getMListenerThread().notifyAll();
		} 
	}

// if (mServerSocket != null)
//		{
//            if (D)
//			{
//                Log.d(TAG, "prepareForNewConnect");
//            }
//            mServerSocket.prepareForNewConnect();
//        }
	//      BluetoothOppUtility.cancelNotification(mContext);
	/* onClose could happen even before start() where mCallback is set */
//        if (mCallback != null)
//		{
//            Message msg = Message.obtain(mCallback);
//            msg.what = BluetoothOppObexSession.MSG_SESSION_COMPLETE;
//            msg.obj = mInfo;
//            msg.sendToTarget();
//        }
	//}
	/*
	 @Override
	 public void unblock() {
	 mServerBlocking = false;
	 }
	 */
    /**
     * Called when connection is accepted from remote, to retrieve the first
     * Header then wait for user confirmation

	 public void preStart() {
	 try {
	 if (D) {
	 Log.d(TAG, "Create ServerSession with transport " + mTransport.toString());
	 }
	 mSession = new ServerSession(mTransport, this, null);
	 } catch (IOException e) {
	 Log.e(TAG, "Create server session error" + e);
	 }
	 }
	 */
    /**
     * Called from BluetoothOppTransfer to start the "Transfer"

	 @Override
	 public void start(Handler handler, int numShares) {
	 if (D) {
	 Log.d(TAG, "Start!");
	 }
	 mCallback = handler;

	 }
	 */
    /**
     * Called from BluetoothOppTransfer to cancel the "Transfer" Otherwise,
     * server should end by itself.

	 @Override
	 public void stop() {
	 /*
	 * TODO now we implement in a tough way, just close the socket.
	 * maybe need nice way

	 if (D) {
	 Log.d(TAG, "Stop!");
	 }
	 mInterrupted = true;
	 if (mSession != null) {
	 try {
	 mSession.close();
	 mTransport.close();
	 } catch (IOException e) {
	 Log.e(TAG, "close mTransport error" + e);
	 }
	 }
	 mCallback = null;
	 mSession = null;
	 }
	 *//*
	 @Override
	 public void addShare(BluetoothOppShareInfo info) {
	 if (D) {
	 Log.d(TAG, "addShare for id " + info.mId);
	 }
	 mInfo = info;
	 mFileInfo = processShareInfo();
	 }
	 */
	/*
	 private synchronized void releaseWakeLocks()
	 {
	 if (mPartialWakeLock.isHeld())
	 {
	 mPartialWakeLock.release();
	 }
	 }
	 */// MimeTypeMap map = MimeTypeMap.getSingleton();
//                    type = map.getMimeTypeFromExtension(extension);
//                    if (V)
//					{
//                        Log.v(TAG, "Mimetype guessed from extension " + extension + " is " + type);
//                    }
//                    if (type != null)
//					{
//                        mimeType = type;
//
//                    }
//					else
//					{
//                        if (mimeType == null)
//						{
//                            if (D)
//							{
//                                Log.w(TAG, "Can't get mimetype, reject the transfer");
//                            }
//                            preReject = true;
//                            obexResponse = ResponseCodes.OBEX_HTTP_UNSUPPORTED_TYPE;
//                        }
//                    }
//                    if (mimeType != null)
//					{
//                        mimeType = mimeType.toLowerCase();
//                    }
//                }
	// }
}
