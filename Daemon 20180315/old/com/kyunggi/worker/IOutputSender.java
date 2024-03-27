package com.kyunggi.worker;
import java.io.*;

public interface IOutputSender
{
	public boolean Send(String s);
	public boolean Send(File file);
	public boolean SendError(Throwable e);
	public boolean SendError(String s,Throwable e);
	public boolean SendError(String s);
}
