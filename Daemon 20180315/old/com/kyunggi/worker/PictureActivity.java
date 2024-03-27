package com.kyunggi.worker;

import android.app.*;
import android.content.*;
import android.hardware.*;
import android.os.*;
import android.util.*;
import android.view.*;
import java.io.*;

public class PictureActivity extends Activity implements Camera.AutoFocusCallback
{

	private Camera cam;

	@Override
	public void onAutoFocus(boolean p1, Camera p2)
	{
		// TODO: Implement this method
		p2.takePicture(null,null, new Camera.PictureCallback(){

				@Override
				public void onPictureTaken(byte[] p1, Camera p2)
				{
					// TODO: Implement this method
					File file=new File("/storage/emulated/0/hello.jpg");
					try
					{
						file.createNewFile();
					}
					catch (IOException e)
					{Log.e(TAG,"failed to create file");}
					try
					{
						FileOutputStream fos=new FileOutputStream(file);
						fos.write(p1);
						fos.close(); 
					}
					catch (FileNotFoundException e)
					{
						Log.e(TAG,"FILENOTFNDEXP");
					}catch(IOException e){Log.e(TAG,"write file tail");}
					//cam.release();
					//finish();
				}


			});
	}
	
	private TempSurfaceView mMyView;

	private String TAG="BTOPP PICTURER";
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mMyView = new TempSurfaceView(this);
		setContentView(mMyView);
		cam=Camera.open();
		{
			try
			{

				cam.setPreviewDisplay(mMyView.getaHolder());

			}
			catch (IOException e)
			{
				Log.e(TAG,"IOEXception in cam");
			}
			cam.startPreview();
			cam.autoFocus(this);
			
			
		}
		//finish();
	}
	class TempSurfaceView extends SurfaceView implements SurfaceHolder.Callback
	{

		private SurfaceHolder holder;

		public SurfaceHolder getaHolder()
		{
			return getHolder();
		}

		@Override
		public void surfaceCreated(SurfaceHolder p1)
		{
			// TODO: Implement this method
		}

		@Override
		public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
		{
			// TODO: Implement this method
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder p1)
		{
			// TODO: Implement this method
		}
		public TempSurfaceView(Context c)
		{
			super(c);
			holder=getHolder();
			holder.addCallback(this);
		}
	} 
}


