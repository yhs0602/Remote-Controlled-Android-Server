package com.kyunggi.worker;

import java.io.*;

public class BatchInfo
{
	public String s;
	public String filename;
	public File file;
	public boolean isfile;
	boolean isEUCKR;
	public byte[] bytes=null;
	public BatchInfo(Object o)
	{
		isEUCKR = false;
		if (o instanceof String)
		{
			isfile = false;
			s = (String) o;
			filename = "response.txt";
		}
		else if (o instanceof File)
		{
			isfile = true;
			file = (File) o;
			filename = file.getName();
		}
	}
	public BatchInfo(Object o, String n)
	{
		isEUCKR = false;
		if (o instanceof String)
		{
			isfile = false;
			s = (String) o;
			filename = n;
		}
		else if (o instanceof File)
		{
			isfile = true;
			file = (File) o;
			filename = n;
		}
	}
	public BatchInfo(Object o, String n, boolean Euc)
	{		
		if (o instanceof String)
		{
			isfile = false;
			s = (String) o;
			filename = n;
			isEUCKR = Euc;
		}
		else if (o instanceof File)
		{
			isfile = true;
			file = (File) o;
			filename = n;
		}
	}
	public BatchInfo(String as, byte[] b)
	{
		filename = as;
		bytes = b;
	}
}
