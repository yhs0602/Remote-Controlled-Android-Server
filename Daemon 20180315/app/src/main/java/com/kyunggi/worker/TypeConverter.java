package com.kyunggi.worker;
import android.util.*;
import java.io.*;
import java.util.*;
import org.jsoup.nodes.*;

public class TypeConverter
{

	private String TAG="BTOPP TYPEconverter";
	public TypeConverter(WorkerSession sess)
	{
		session = sess;
		prefHelper = sess.getPreferenceHelper();
	}
	private WorkerSession session=null;
	private PreferenceHelper prefHelper=null;
	/*public  Object ConvertTo(FILETYPE from, FILETYPE to, Object data)
	 {
	 if (from == FILETYPE.STRING)
	 {
	 return StringConvertTo("",to, (String)data);
	 }
	 else if (from==FILETYPE.IMG||from == FILETYPE.BMP || from == FILETYPE.JPG || from == FILETYPE.PNG)
	 {
	 return ImgConvertTo(from, to, (File)data);
	 }
	 else if (from == FILETYPE.GIF)
	 {
	 return GifConvertTo(to, (File)data);
	 }
	 else if (from == FILETYPE.HTM)
	 {
	 return HtmConvertTo(to, (File)data);
	 }
	 else if (from==FILETYPE.VIDEO||from == FILETYPE.K3G || from == FILETYPE.MP4 || from == FILETYPE.THREEGP)
	 {
	 return VideoConvertTo(to, (File) data);
	 }
	 else if (from==FILETYPE.AUDIO||from == FILETYPE.MP3 || from == FILETYPE.QCP || from == FILETYPE.QCQ)
	 {
	 return AudioConvertTo(to, (File) data);
	 }


	 return null;
	 }
	 */
	public BatchInfo[] GetBatchInfo(String as, FILETYPE hint, Object data) throws IOException
	{
		if (hint == FILETYPE.STRING || hint == FILETYPE.TEXTFILE || hint == FILETYPE.TEXTMSG || hint == FILETYPE.TEXTRESULT || hint == FILETYPE.VNT)
		{
			return StringConvertTo(as, hint, (String)data);		
		}
		else if (hint == FILETYPE.IMG || hint == FILETYPE.BMP || hint == FILETYPE.JPG || hint == FILETYPE.PNG)
		{
			//data :path
			try
			{
				return new BatchInfo[]{new BatchInfo(as, Utility.readFully(Utility.ImageConvert((String)data, prefHelper.getImageTypeExt())))};
			}
			catch (IOException e)
			{
				Utility.addError(TAG, "");
				return null;
			}
		}
		else if (hint == FILETYPE.GIF)
		{
			try
			{
				return new BatchInfo[]{new BatchInfo(as, Utility.readFully(Utility.ImageConvert((String)data, prefHelper.getImageTypeExt())))};
			}
			catch (IOException e)
			{}
		}
		else if (hint == FILETYPE.HTM)
		{
			return prefHelper.isConvertHtmToTxt() ? HtmConvertToTxt(as, (String)data): new BatchInfo[]{new BatchInfo(as, (String)data)};
		}
		else if (hint == FILETYPE.HTMFILE)
		{
			return prefHelper.isConvertHtmToTxt() ? HtmConvertToTxt(as, (String)data): new BatchInfo[]{new BatchInfo(as, (String)data)};
		}
		else if (hint == FILETYPE.VIDEO || hint == FILETYPE.K3G || hint == FILETYPE.MP4 || hint == FILETYPE.THREEGP)
		{
			return null;
		}
		else if (hint == FILETYPE.AUDIO || hint == FILETYPE.MP3 || hint == FILETYPE.QCP || hint == FILETYPE.QCQ)
		{
			return null;
		}
		else if (hint == FILETYPE.PDF)
		{
				return StringConvertTo(as/*+".txt"*/, FILETYPE.TEXTRESULT, Utility.getStringFromPdfFile((String)data));		
		}
		else if (hint == FILETYPE.HWP)
		{		
				return StringConvertTo(as/*+".txt"*/, FILETYPE.TEXTRESULT, Utility.getStringFromHwpFile((String)data));		
		}
		
		try
		{
			return new BatchInfo[]{new BatchInfo(as, Utility.readFully((String)data))};
		}
		catch (IOException e)
		{}
		return null;
	}

	private BatchInfo[] HtmFileConvertToTxt(String as, String filename)
	{
		Document doc=new Document(filename);
		return StringConvertTo(as, FILETYPE.TEXTFILE, doc.text());
	}
	private BatchInfo[] HtmConvertToTxt(String as, String data)
	{
		try
		{
			File f=Utility.MakeNewFile(session.path + as);
			FileOutputStream fos=new FileOutputStream(f);
			fos.write(data.getBytes());
			fos.close();
			return HtmFileConvertToTxt(as, f.getCanonicalPath());
		}
		catch (IOException e)
		{}
		return null;
	}
	private BatchInfo AudioConvertTo(TypeConverter.FILETYPE to, File data)
	{
		// TODO: Implement this method
		//BatchInfo info=new BatchInfo();

		return null;
	}

	private  File VideoConvertTo(TypeConverter.FILETYPE to, File data)
	{
		// TODO: Implement this method
		return null;
	}

	private byte[] HtmConvertTo(TypeConverter.FILETYPE to, File data)
	{
		// TODO: Implement this method
		return null;
	}

	private  File GifConvertTo(TypeConverter.FILETYPE to, File data)
	{
		// TODO: Implement this method
		return null;
	}

	private  File ImgConvertTo(TypeConverter.FILETYPE from, TypeConverter.FILETYPE to, File data)
	{
		// TODO: Implement this method
		return null;
	}

	private BatchInfo[] StringConvertTo(String as, TypeConverter.FILETYPE to, String data)
	{
		if (as.compareTo("") == 0)
			as = null;
		if (to == FILETYPE.STRING)
			to = FILETYPE.TEXTMSG;
		if(to==FILETYPE.TEXTFILE||to==FILETYPE.TEXTRESULT)
		{
			if(Utility.GetFileExtension(as).compareToIgnoreCase("txt")!=0)
			{
				as=as.concat(".txt");
				Log.d(TAG,"as  =" +as);
			}
		}
		try
		{
			if (to == FILETYPE.TEXTFILE)
			{
				return new BatchInfo[]{new BatchInfo(as, Utility.readTextFile(data).getBytes(session.getPreferenceHelper().getTextFileEncoding()))};
			}
			if (to == FILETYPE.TEXTMSG)
			{
				if (session.getPreferenceHelper().getTextMsgType() == FILETYPE.VNT)
				{
					return CreateVnt(data);
				}
				if (as == null)
				{
					as = "message.txt";
				}
				return new BatchInfo[]{new BatchInfo(as, data.getBytes(session.getPreferenceHelper().getTextMsgEncoding()))};
			}
			if (to == FILETYPE.TEXTRESULT)
			{
				if (session.getPreferenceHelper().getTextResultType() == FILETYPE.VNT)
				{
					return CreateVnt(data);
				}
				if (as == null)
				{
					as = "result.txt";
				}
				return new BatchInfo[]{new BatchInfo(as, data.getBytes(session.getPreferenceHelper().getTextResultEncoding()))};
			}
			if (to == FILETYPE.VNT)
			{
				return CreateVnt(data);			
			}		
		}
		catch (UnsupportedEncodingException e)
		{
			try
			{
				return new BatchInfo[]{new BatchInfo("result.txt", data.getBytes("EUC-KR"))};
			}
			catch (UnsupportedEncodingException f)
			{}
		}
		// TODO: Implement this method
		return null;
	}

	private BatchInfo[]  CreateVnt(String s)
	{
		// TODO: Implement this method
		String [] data=Utility.SplitStringByByteLength(s, "UTF-8", 400);
		ArrayList<BatchInfo> arr=new ArrayList<BatchInfo>();

		for (String str:data)
		{
			arr.add(CreateVntSub(str));
		}
		/*try
		 {
		 session.batch.flush();
		 }
		 catch (IOException e)
		 {
		 return false;
		 }*/
		return arr.toArray(new BatchInfo[arr.size()]);
	}
	private BatchInfo CreateVntSub(String s)
	{
		// TODO: Implement this method
		String VntStr="BEGIN:VNOTE\nVERSION:1.1\nBODY;CHARSET=UTF-8:";
		VntStr += s;
		//VntStr+="DCREATED:20180129T175000\nLAST-MODIFIED:20180130T153100\nCLASS:PUBLIC\nEND:VNOTE";
		try
		{
			return new BatchInfo("LG-LV8500_MEMO_20180130153117_123.vnt", VntStr.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			return null;
		}	
	}
	private static HashMap<String,FILETYPE> fileTypemap=new HashMap<String,FILETYPE>();
	{
		fileTypemap.put("txt", FILETYPE.TEXTFILE);
		fileTypemap.put("text", FILETYPE.TEXTFILE);
		fileTypemap.put("jpg", FILETYPE.JPG);
		fileTypemap.put("gif", FILETYPE.GIF);
		fileTypemap.put("png", FILETYPE.PNG);
		fileTypemap.put("bmp", FILETYPE.BMP);
		fileTypemap.put("pdf", FILETYPE.PDF);
		fileTypemap.put("hwp", FILETYPE.HWP);



	}
	public FILETYPE getFileType(String path)
	{
		String ext=Utility.GetFileExtension(path);
		return fileTypemap.get(ext);
	}
	public enum FILETYPE
	{
		STRING,
		TEXTMSG,
		TEXTFILE,
		TEXTRESULT,
		VNT,
		HTM,
		HTMFILE,
		IMG,
		JPG,
		BMP,
		PNG,
		VIDEO,
		K3G,
		MP4,
		THREEGP,
		GIF,
		AUDIO,
		MP3,
		QCP,
		QCQ,
		PDF,
		HWP
		}
}
