package com.kyunggi.worker;

import android.bluetooth.*;
import android.os.*;
import java.util.*;

public class BluetoothManager
{
	private BluetoothAdapter mBluetoothAdapter = null;

	public void onCreate() {
		
		// BluetoothAdapter 인스턴스를 얻는다
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}
}
