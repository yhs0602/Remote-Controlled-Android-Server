package com.kyunggi.worker;

import android.content.*;
import android.util.*;

public class PreferenceHelper
{
	private String address;
	private Context context;

	private SharedPreferences setting;

	private SharedPreferences.Editor editor;

	private String textFileEncoding;

	private String textResultEncoding;

	private String textMsgEncoding;
	
	private TypeConverter.FILETYPE textMsgType=TypeConverter.FILETYPE.VNT;
	
	private TypeConverter.FILETYPE textResultType=TypeConverter.FILETYPE.TEXTFILE;

	private String TAG="BTOPP PreferenceHelper";
	
	public PreferenceHelper(Context c,String s)
	{
		address=s;
		context=c;
		setting = c.getSharedPreferences(address, 0);
		editor = setting.edit();
		textFileEncoding = setting.getString("TextFileEncoding", "EUC-KR");
		textMsgEncoding = setting.getString("TextMsgEncoding", "UTF-8");
		textResultEncoding = setting.getString("TextResultEncoding", "EUC-KR");
	}
	public boolean isConvertHtmToTxt()
	{
		return true;
	}

	public String getImageTypeExt()
	{
		// TODO: Implement this method
		return "jpg";
	}

	public void setTextResultType(TypeConverter.FILETYPE textResultType)
	{
		this.textResultType = textResultType;
	}

	public TypeConverter.FILETYPE getTextResultType()
	{
		return textResultType;
	}

	public void setTextMsgType(TypeConverter.FILETYPE textMsgType)
	{
		this.textMsgType = textMsgType;
	}

	public TypeConverter.FILETYPE getTextMsgType()
	{
		return textMsgType;
	}

	@Override
	protected void finalize() throws Throwable
	{
		// TODO: Implement this method
		super.finalize();
		editor.putString("TextFileEncoding",textFileEncoding);
		editor.putString("TextResultEncoding",textResultEncoding);
		editor.putString("TextMsgEncoding",textMsgEncoding);
		
		editor.commit();
	}
	

	public void setTextFileEncoding(String textFileEncoding)
	{
		this.textFileEncoding = textFileEncoding;
	}

	public String getTextFileEncoding()
	{
		return textFileEncoding;
	}

	public void setTextResultEncoding(String textResultEncoding)
	{
		this.textResultEncoding = textResultEncoding;
	}

	public String getTextResultEncoding()
	{
		Log.v(TAG,textResultEncoding);
		return textResultEncoding;
	}

	public void setTextMsgEncoding(String textMsgEncoding)
	{
		this.textMsgEncoding = textMsgEncoding;
	}

	public String getTextMsgEncoding()
	{
		return textMsgEncoding;
	}	
}
