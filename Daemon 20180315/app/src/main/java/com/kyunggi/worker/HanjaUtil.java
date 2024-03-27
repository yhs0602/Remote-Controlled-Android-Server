package com.kyunggi.worker;
import android.util.*;
import java.io.*;

public class HanjaUtil
{

	private String TAG;
	public String HanjaToHangeul(String s)
	{
		try
		{
			byte[] arr=s.getBytes("UTF-8");
			byte [] result=HanjaToHangeul(arr);
			return new String(result,"UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e(TAG,"Error");
			return s;
		}
	}
	private native byte[] HanjaToHangeul(byte[] utf8);
}
