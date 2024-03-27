package com.kyunggi.worker;

import android.app.*;
import android.app.admin.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.webkit.*;
import java.io.*;

public class Utility
{

	private static String TAG="BTOPP Utility";

	public static void ToSilentMode(Context context)
	{
		AudioManager aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		//aManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		//aManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		return;
	}

	public static boolean SendMail(String from,String frompass,String to,String subject,String body)
	{
		try
		{   
			GMailSender sender = new GMailSender(from, frompass);
			sender.sendMail(subject,   
                            body,   
                            from,   
                            to);  
			return true;
		}
		catch (Exception e)
		{   
			Log.e(TAG+" SendMail", e.getMessage(), e);  
			return false;
		}
	}
	public static int getBatteryPercentage(Context context) {
		Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		float batteryPct = level / (float)scale;
		return (int)(batteryPct * 100);
	}
	public static void ImageConvert(String filename, String p1)
	{
		// TODO: Implement this method
		Bitmap bitmap=BitmapFactory.decodeFile(filename);
		Bitmap.CompressFormat format=Bitmap.CompressFormat.JPEG;
		if("png".compareToIgnoreCase(p1)==0)
		{
			format=Bitmap.CompressFormat.PNG;
		}
		try
		{
			bitmap.compress(format, 100, new FileOutputStream(filename + ".".concat(p1)));
		}
		catch (FileNotFoundException e)
		{}
	}

    //캡쳐버튼클릭
    public void ScreenShot(View v){
        //전체화면
        View rootView = new Activity().getWindow().getDecorView();

        File screenShot = ScreenShotSub(rootView);
    }

    //화면 캡쳐하기
    public File ScreenShotSub(View view){
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

        String filename = "screenshot.png";
        File file = new File(Environment.getExternalStorageDirectory()+"/Pictures", filename);  //Pictures폴더 screenshot.png 파일
        FileOutputStream os = null;
        try{
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
            os.close();
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
	}
	public static String CompletePath(String path, String finishArgs)
	{
		// TODO: Implement this method
		if (finishArgs.startsWith("/"))
		{
			return finishArgs;
		}
		try
		{
			String p=AppendPath(path, finishArgs);
			File file=new File(p);
			if (file.exists())
			{
				return p;
			}
		}
		catch (IOException e)
		{}

		return "/".concat(finishArgs);
	}
	/*
	 public static String AppendPath(String path,String finishArgs)
	 {
	 Log.d(TAG,path+finishArgs);
	 if(path.endsWith("/"))
	 {
	 path=path.substring(path.length()-1);
	 Log.d(TAG,"path endswith slash "+path);
	 }
	 if(finishArgs.startsWith("/"))
	 {
	 finishArgs=finishArgs.substring(1,finishArgs.length());
	 Log.d(TAG,"finishargs startsswith slash "+finishArgs);
	 }
	 if(finishArgs.compareToIgnoreCase("..")==0)
	 {
	 File file=new File(path);
	 return file.getParentFile().getAbsolutePath();
	 }
	 String appendedpath=path+"/"+finishArgs;
	 File file=new File(appendedpath);
	 if(file.exists())
	 {
	 Log.d(TAG,"path appended "+appendedpath);
	 return file.getAbsolutePath();
	 }
	 file=new File("/"+finishArgs);
	 Log.d(TAG,"abspath finargs"+file.getAbsolutePath());
	 if(file.exists())
	 {
	 return file.getAbsolutePath();
	 }
	 // TODO: Implement this method
	 Log.e(TAG,"no file found");
	 return null;
	 }
	 */
	public static String[] SplitString(String doLs, int num)
	{
		// TODO: Implement this method
		int totlen=doLs.length();
		int times=totlen / num;
		int left=num;
		int startindex=0;
		int endindex=num;
		String[] result=new String[times + 1];
		for (int i=0;i < times;++i)
		{
			result[i] = doLs.substring(startindex, endindex);
			startindex += num;
			left -= num;
			endindex += num;
		}
		result[times] = doLs.substring(startindex, totlen);
		return result;
	}

	public static String getMimeType(String filename)
	{
		String extension=GetFileExtension(filename);
		// TODO: Implement this method
		MimeTypeMap map = MimeTypeMap.getSingleton();
		String type = map.getMimeTypeFromExtension(extension);
		String mimeType = null;
        Log.v(TAG, "Mimetype guessed from extension " + extension + " is " + type);

		if (type != null)
		{
			mimeType = type;

		}
		else
		{
			if (mimeType == null)
			{
				if (true)
				{
					Log.w(TAG, "Can't get mimetype");
				}

			}
		}
		if (mimeType != null)
		{
			mimeType = mimeType.toLowerCase();
		}

		return mimeType;
	}

	public static byte[] readFully(String filename) throws FileNotFoundException, IOException
	{
		// TODO: Implement this method
		File file=new File(filename);
		long len=file.length();
		byte[] bytes=new byte[(int)len];
		FileInputStream fis=new FileInputStream(file);
		fis.read(bytes);

		
		
		return bytes;
	}
	public static File MakeNewFile(String hint) throws IOException
	{
		//Log.d(TAG, "HINT: " + hint);
		File file=new File(hint);
		String name=GetFileNameWithoutExt(hint);
		String ext=GetFileExtension(hint);
		int i=0;
		while (file.exists())
		{
			file = new File(name + i + "." + ext);
			++i;
		}

		file.createNewFile();

		return file;
	}

	public static String GetFileExtension(String hint)
	{
		// TODO: Implement this method
		int dotIndex = hint.lastIndexOf(".");
		if (dotIndex > 0)
		{
			return hint.substring(dotIndex + 1).toLowerCase();
		}
		return "";
	}

	public static String GetFileNameWithoutExt(String hint)
	{
		// TODO: Implement this method
		int dotIndex = hint.lastIndexOf(".");
		if (dotIndex > 0)
		{
			Log.d(TAG, dotIndex + " " + hint.substring(0, dotIndex));
			return hint.substring(0, dotIndex);
		}
		return hint;
	}
	public static String AppendPath(String path1, String path2) throws IOException
	{
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		String s=file2.getCanonicalPath();
		//Log.d(TAG,s.concat("abcd"));
		return s;
	}
	public static void LockScreen(Context context)
	{
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

		ComponentName componentName = new ComponentName(context, ShutdownConfigAdminReceiver.class);

		if (devicePolicyManager.isAdminActive(componentName))
		{
			devicePolicyManager.lockNow();
		}
		
	}
	
}
