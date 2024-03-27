package com.kyunggi.worker;

// simplified exception handling
import android.bluetooth.*;
import java.lang.reflect.*;

public class BluetoothAdapterProxy
{
    public static final int CHANNEL_OPP = 12;

    final BluetoothAdapter target;
    static final Class<?> targetClass = BluetoothAdapter.class;
    Method listenOn;

    public BluetoothAdapterProxy(BluetoothAdapter target)
    {
        this.target = target;
        Class<?>[] args = new Class[] { int.class };
        try
        {
            this.listenOn = targetClass.getDeclaredMethod(
                "listenUsingRfcommOn", args);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    public BluetoothServerSocket listenUsingRfcommOn(int channel)
    {
        try
        {
            return (BluetoothServerSocket) (listenOn.invoke(target, 
															new Object[] { channel }));
        }
        catch (Exception e)
        {
            // complain loud, complain long
            throw new RuntimeException(e);
        }
    }
}

//
