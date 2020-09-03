package com.kyunggi.worker2;

import android.app.*;
import android.app.admin.*;
import android.bluetooth.*;
import android.content.*;
import android.graphics.*;
import android.media.*;
import android.net.*;
import android.net.wifi.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.webkit.*;

import com.argo.hwp.*;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.*;
//import org.apache.pdfbox.pdmodel.*;
//import org.apache.pdfbox.text.*;
//import org.apache.pdfbox.pdmodel.*;
//import org.apache.pdfbox.text.*;

public class Utility {
    private static String TAG = "BTOPP Utility";
    private static String log = "";

    static MediaRecorder recorder = new MediaRecorder();
    static OCRUtil ocrUtil = new OCRUtil();

    public static String getStringFromQRCode(String path) {
        return QRCode.decodeQRImage(path);
    }

    public static String getStringFromImageFile(String path) {
        Log.d(TAG, "GetStrfromImg");
        ocrUtil.Init();
        Log.d(TAG, "Init done");
        return ocrUtil.processImage(path);
    }

    public static String getStringFromHwpFile(String path) throws IOException {
        Log.v(TAG, "GetStringFromHwp" + path);
        File hwp = new File(path); // 텍스트를 추출할 HWP 파일
        Writer writer = new StringWriter(); // 추출된 텍스트를 출력할 버퍼
        HwpTextExtractor.extract(hwp, writer); // 파일로부터 텍스트 추출
        String text = writer.toString(); // 추출된 텍스트
        return text;
    }

    public static String getStringFromPdfFile(String path) throws IOException {
        Log.v(TAG, "StringFromPdf path " + path);
        String text = "";
        PDDocument document = PDDocument.load(new File(path));
        if (!document.isEncrypted()) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
            //System.out.println("Text:" + text);
        }
        document.close();
        Log.v(TAG, "GETSTRingfromPdf end");
        return text;
    }

    public static float getCpuTemperature() {
        java.lang.Process process;

        try {
            process = Runtime.getRuntime().exec("cat sys/class/thermal/thermal_zone0/temp");
            process.waitFor();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();

            float temperature = Float.parseFloat(line); // 10.0f;
            return temperature;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    public static boolean IsConnectedToInternet(Context c) {
        //String CONNECTIVITY_SERVICE = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(c.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected() == false) {
                //Toast.makeText(this, "네트워크 연결을 확인하세요.", 1).show();
                return false;
            } else {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    //Toast.makeText(this, "데이터 요금이 발생할 수 있습니다.", 1).show();
                }
                return true;
            }
            //Toast.makeText(this, "네트워크 연결을 확인하세요.", 1).show();
        } else {
            //Toast.makeText(this, "네트워크 연결을 확인하세요.", 1).show();
            return false;
            //Toast.makeText(this, "테스트.", 1).show();
            //btest = true;
        }
    }

    public static void TurnOffTethering(Context c) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException, NoSuchMethodException, SecurityException {
        class BTPanServiceListener implements BluetoothProfile.ServiceListener {

            private final Context context;

            public BTPanServiceListener(final Context context) {
                this.context = context;
            }

            @Override
            public void onServiceConnected(final int profile,
                                           final BluetoothProfile proxy) {
                //Some code must be here or the compiler will optimize away this callback.
                Log.e(TAG, "BTPan proxy connected");

            }

            @Override
            public void onServiceDisconnected(final int profile) {
            }
        }
        String sClassName = "android.bluetooth.BluetoothPan";

        Class<?> classBluetoothPan = Class.forName(sClassName);

        Constructor<?> ctor = classBluetoothPan.getDeclaredConstructor(Context.class, BluetoothProfile.ServiceListener.class);
        ctor.setAccessible(true);
        Object instance = ctor.newInstance(c, new BTPanServiceListener(c));
        //  Set Tethering ON
        Class[] paramSet = new Class[1];
        paramSet[0] = boolean.class;

        Method setTetheringOn = classBluetoothPan.getDeclaredMethod("setBluetoothTethering", paramSet);

        setTetheringOn.invoke(instance, false);
    }

    public static void TurnOnTethering(Context c) throws Exception {
        class BTPanServiceListener implements BluetoothProfile.ServiceListener {

            private final Context context;

            public BTPanServiceListener(final Context context) {
                this.context = context;
            }

            @Override
            public void onServiceConnected(final int profile,
                                           final BluetoothProfile proxy) {
                //Some code must be here or the compiler will optimize away this callback.
                Log.e("MyApp", "BTPan proxy connected");

            }

            @Override
            public void onServiceDisconnected(final int profile) {
            }
        }
        String sClassName = "android.bluetooth.BluetoothPan";

        Class<?> classBluetoothPan = Class.forName(sClassName);

        Constructor<?> ctor = classBluetoothPan.getDeclaredConstructor(Context.class, BluetoothProfile.ServiceListener.class);
        ctor.setAccessible(true);
        Object instance = ctor.newInstance(c, new BTPanServiceListener(c));
        //  Set Tethering ON
        Class[] paramSet = new Class[1];
        paramSet[0] = boolean.class;

        Method setTetheringOn = classBluetoothPan.getDeclaredMethod("setBluetoothTethering", paramSet);

        setTetheringOn.invoke(instance, true);
    }

    public static String[] SplitStringByByteLength(String src, String encoding, int maxsize) {
        Charset cs = Charset.forName(encoding);
        CharsetEncoder coder = cs.newEncoder();
        ByteBuffer out = ByteBuffer.allocate(maxsize);  // output buffer of required size
        CharBuffer in = CharBuffer.wrap(src);
        List<String> ss = new ArrayList<>();            // a list to store the elements
        int pos = 0;
        while (true) {
            CoderResult cr = coder.encode(in, out, true); // try to encode as much as possible
            int newpos = src.length() - in.length();
            String s = src.substring(pos, newpos);
            ss.add(s);                                  // add what has been encoded to the list
            pos = newpos;                               // store new input position
            out.rewind();                               // and rewind output buffer
            if (!cr.isOverflow()) {
                break;                                  // everything has been encoded
            }
        }
        return ss.toArray(new String[0]);
    }

    public static String[] SplitStringByByteLength(String src, int maxsize) {
        ArrayList<String> splitted = new ArrayList<String>();
        int pos = 0;
        try {
            while (true) {
                int index = src.offsetByCodePoints(pos, maxsize / 4 - 1);
                splitted.add(src.substring(pos, index));
                pos = index + 1;
            }
        } catch (IndexOutOfBoundsException e) {

        }
        return splitted.toArray(new String[splitted.size()]);
    }
//	public static String[] SplitStringByByteLength(String src,String encoding, int maxsize) throws UnsupportedEncodingException
//	{
//		ArrayList<String> splitted=new ArrayList<String>();
//		StringBuilder builder=new StringBuilder();
//		//int l=0;
//		int i=0;
//		while(true)
//		{
//			String tmp=builder.toString();
//			char c=src.charAt(i);
//			if(c=='\0')
//				break;
//			builder.append(c);
//			if(builder.toString().getBytes(encoding).length>maxsize)
//			{
//				splitted.add(new String(tmp));
//				builder=new StringBuilder();
//			}
//			++i;
//		}
//		return splitted.toArray(new String[splitted.size()]);
//	}

    public static void addError(String tAG, String p1) {
        // TODO: Implement this method
        Log.e(tAG, p1);
        log += tAG + p1;
    }

    public static String readTextFile(String path) {
        try {
            return new String(Utility.readFully(path));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * wifi 상태인지 확인
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnabled(Context context) {

        WifiManager wifiMgr = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            return true;
        } else {
            return false;
        }
    }

    public static void startRec(String name) {
        recorder.reset();
        try {
            File file = Environment.getExternalStorageDirectory();
//갤럭시 S4기준으로 /storage/emulated/0/의 경로를 갖고 시작한다. 
            String path = file.getAbsolutePath() + name;
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//첫번째로 어떤 것으로 녹음할것인가를 설정한다. 마이크로 녹음을 할것이기에 MIC로 설정한다.
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//이것은 파일타입을 설정한다. 녹음파일의경우 3gp로해야 용량도 작고 효율적인 녹음기를 개발할 수있다. 
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//이것은 코덱을 설정하는 것이라고 생각하면된다. 
            recorder.setOutputFile(path);
//저장될 파일을 저장한뒤 
            recorder.prepare();
            recorder.start();
//시작하면된다. 
        } catch (IllegalStateException | IOException e) {
            Log.e(TAG, "StartRec", e);
        }
    }

    public static void stopRec() {
        recorder.stop();
//멈추는 것이다. 
        recorder.release();
    }

    public static void ToVibrateMode(Context context) {
        AudioManager aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //aManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        aManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        //aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return;
    }

    public static void ToNormalMode(Context context) {
        AudioManager aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        aManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        //aManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        //aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return;
    }

    public static void ToSilentMode(Context context) {
        AudioManager aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //aManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        //aManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return;
    }

    public static boolean SendMail(String from, String frompass, String to, String subject, String body) {
        try {
            GMailSender sender = new GMailSender(from, frompass);
            sender.sendMail(subject,
                    body,
                    from,
                    to);
            return true;
        } catch (Exception e) {
            Log.e(TAG + " SendMail", e.getMessage(), e);
            return false;
        }
    }

    public static int getBatteryPercentage(Context context) {
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        return (int) (batteryPct * 100);
    }

    public static String ImageConvert(String filename, String p1) {
        String finalfilename = filename + ".".concat(p1);
        Bitmap bitmap = BitmapFactory.decodeFile(filename);
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        if ("png".compareToIgnoreCase(p1) == 0) {
            format = Bitmap.CompressFormat.PNG;
        }
        try {
            bitmap.compress(format, 100, new FileOutputStream(finalfilename));
        } catch (FileNotFoundException e) {
        }
        return finalfilename;
    }

    //캡쳐버튼클릭
    public void ScreenShot(View v) {
        //전체화면
        View rootView = new Activity().getWindow().getDecorView();

        File screenShot = ScreenShotSub(rootView);
    }

    //화면 캡쳐하기
    public File ScreenShotSub(View view) {
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다

        Bitmap screenBitmap = view.getDrawingCache();   //캐시를 비트맵으로 변환

        String filename = "screenshot.png";
        File file = new File(Environment.getExternalStorageDirectory() + "/Pictures", filename);  //Pictures폴더 screenshot.png 파일
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 90, os);   //비트맵을 PNG파일로 변환
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        view.setDrawingCacheEnabled(false);
        return file;
    }

    public static String CompletePath(String path, String finishArgs) {
        if (finishArgs.startsWith("/")) {
            return finishArgs;
        }
        try {
            String p = AppendPath(path, finishArgs);
            File file = new File(p);
            if (file.exists()) {
                return p;
            }
        } catch (IOException e) {
        }

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
    public static String[] SplitString(String doLs, int num) {
        // TODO: Implement this method
        int totlen = doLs.length();
        int times = totlen / num;
        int left = num;
        int startindex = 0;
        int endindex = num;
        String[] result = new String[times + 1];
        for (int i = 0; i < times; ++i) {
            result[i] = doLs.substring(startindex, endindex);
            startindex += num;
            left -= num;
            endindex += num;
        }
        result[times] = doLs.substring(startindex, totlen);
        return result;
    }

    public static String getMimeType(String filename) {
        String extension = GetFileExtension(filename);
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String type = map.getMimeTypeFromExtension(extension);
        String mimeType = null;
        Log.v(TAG, "Mimetype guessed from extension " + extension + " is " + type);

        if (type != null) {
            mimeType = type;

        } else {
            if (mimeType == null) {
                if (true) {
                    Log.w(TAG, "Can't get mimetype");
                }

            }
        }
        if (mimeType != null) {
            mimeType = mimeType.toLowerCase();
        }

        return mimeType;
    }

    public static byte[] readFully(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        long len = file.length();
        byte[] bytes = new byte[(int) len];
        FileInputStream fis = new FileInputStream(file);
        fis.read(bytes);


        return bytes;
    }

    public static File MakeNewFile(String hint) throws IOException {
        //Log.d(TAG, "HINT: " + hint);
        File file = new File(hint);
        String name = GetFileNameWithoutExt(hint);
        String ext = GetFileExtension(hint);
        int i = 0;
        while (file.exists()) {
            file = new File(name + i + "." + ext);
            ++i;
        }

        file.createNewFile();

        return file;
    }

    public static String GetFileExtension(String hint) {
        // TODO: Implement this method
        int dotIndex = hint.lastIndexOf(".");
        if (dotIndex > 0) {
            return hint.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }

    public static String GetFileNameWithoutExt(String hint) {
        int dotIndex = hint.lastIndexOf(".");
        if (dotIndex > 0) {
            Log.d(TAG, dotIndex + " " + hint.substring(0, dotIndex));
            return hint.substring(0, dotIndex);
        }
        return hint;
    }

    public static String AppendPath(String path1, String path2) throws IOException {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        String s = file2.getCanonicalPath();
        //Log.d(TAG,s.concat("abcd"));
        return s;
    }

    public static void LockScreen(Context context) {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName componentName = new ComponentName(context, ShutdownConfigAdminReceiver.class);

        if (devicePolicyManager.isAdminActive(componentName)) {
            devicePolicyManager.lockNow();
        }

    }

}
