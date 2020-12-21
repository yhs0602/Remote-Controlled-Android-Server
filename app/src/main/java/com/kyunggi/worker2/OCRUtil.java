package com.kyunggi.worker2;

import android.content.res.*;
import android.graphics.*;
import android.util.*;
import com.googlecode.tesseract.android.*;
import java.io.*;
import java.util.*;

public class OCRUtil
{

	//Bitmap image; //사용되는 이미지
	private TessBaseAPI mTess; //Tess API reference
	String datapath = "" ; //언어데이터가 있는 경로
	private boolean isInitiated=false;

	private String TAG="BTOPP OCRHELPER";
	
	public void Init()
	{
		Log.v(TAG,"OCR INIT");
		if(isInitiated)
			return;
		//이미지 디코딩을 위한 초기화
		//image = BitmapFactory.decodeFile(""); //샘플이미지파일
		//언어파일 경로
		datapath = WorkerService.getContext().getFilesDir()+"/tesseract/";

		//트레이닝데이터가 카피되어 있는지 체크
		checkFile(new File(datapath + "tessdata/"));

		Log.d(TAG,"CheckFile done");
		//Tesseract API
		String lang = "eng";

		mTess = new TessBaseAPI();
		Log.d(TAG,"NEW tessApi done");
		mTess.setDebug(true);
		System.gc();
		try
		{
			mTess.init(datapath, lang);
			isInitiated=true;
		}catch(Exception e)
		{
			Log.e(TAG,"init error",e);
		}
		Log.d(TAG,"TESSINIT DONE");
		
		return;
	}

	//Process an Image
	public String processImage(Bitmap img)
	{
		String OCRresult;
		mTess.setImage(img);
		OCRresult = mTess.getUTF8Text();
		//TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
		//OCRTextView.setText(OCRresult);
		return OCRresult;
	}

	public String processImage(String path)
	{
		Log.d(TAG,path);
		Log.d(TAG,Arrays.toString(path.getBytes()));
		
		File file=new File(path);
		if(!file.exists())
		{
			throw new RuntimeException("no such="+file.getAbsolutePath().concat(" does not exist"));
		}
		return processImage(BitmapFactory.decodeFile(path));
	}

	//copy file to device
	private void copyFiles()
	{
		try
		{
			String filepath = datapath + "/tessdata/eng.traineddata";
			AssetManager assetManager = WorkerService.getContext().getAssets();
			InputStream instream = assetManager.open("tessdata/eng.traineddata");
			OutputStream outstream = new FileOutputStream(filepath);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = instream.read(buffer)) != -1)
			{
				outstream.write(buffer, 0, read);
			}
			outstream.flush();
			outstream.close();
			instream.close();

		}
		catch (FileNotFoundException e)
		{
			Log.e(TAG,"Copy Files",e);
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Log.e(TAG,"Copy Files",e);
			e.printStackTrace();
		}
		
		return ;
	}

	//check file on the device
	private void checkFile(File dir)
	{
		//디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
		if (!dir.exists() && dir.mkdirs())
		{
			copyFiles();
		}
		//디렉토리가 있지만 파일이 없으면 파일카피 진행
		if (dir.exists())
		{
			//String datafilepath = datapath + "/tessdata/eng.traineddata";
			//File datafile = new File(datafilepath);
			//if (!datafile.exists())
			{
				copyFiles();
			}
		}
		return ;
	}

}
