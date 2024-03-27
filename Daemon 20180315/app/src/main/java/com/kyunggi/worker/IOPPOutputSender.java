package com.kyunggi.worker;
import android.util.*;
import java.io.*;

public abstract class  IOPPOutputSender extends IOutputSender
{
	public IOPPOutputSender(BluetoothOPPBatch b)
	{
		super(b);
	}
	//public abstract boolean Send(String s);
}
