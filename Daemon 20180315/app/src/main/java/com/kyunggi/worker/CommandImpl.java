package com.kyunggi.worker;

import android.os.*;
import android.text.*;
import android.util.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import android.app.*;
import java.lang.reflect.*;
import com.kyunggi.worker.ExternalProgram.*;
import android.bluetooth.*;

public class CommandImpl
{
	WorkerSession session;
	BluetoothBatch batch;
	TypeConverter typeConverter;

	public String TAG="BTOPP CMDIMPL";
	public CommandImpl(WorkerSession s)
	{
		session=s;
		batch=s.batch;
		typeConverter=s.typeConverter;
	}
	
	public void DoSendTo(String []args)
	{
		BluetoothDevice device=BluetoothAdapter.getDefaultAdapter().getRemoteDevice(args[0]);
		Object o=DoEval(FinishArgs(args,2," "));
	}
	public void DoNormalMode(String []args)
	{
		Utility.ToNormalMode(WorkerService.getContext());
	}
	
	public void DoVibrateMode(String []args)
	{
		Utility.ToVibrateMode(WorkerService.getContext());
	}
	
	public void DoPs(String [] args)
	{
		ArrayList<Program> progs= session.getRunningPrograms();
		StringBuilder b=new StringBuilder();
		b.append("PID NAME STATUS");
		for(int i=0;i<progs.size();++i)
		{
			Program p=progs.get(i);
			b.append(i+"   "+p.getClass().getName()+"   "+p.getStatus()+"\n");
		}
	}
	public void DoKillAll(String [] args)
	{
		session.KillAll();
	}
	
	public boolean PreprocessCmd(String cmd)
	{
		Log.v(TAG,"PREPROCESSCMD "+cmd);
		Program p=session.getForegroundProgram();
		if(p==null)
			return false;
		Log.d(TAG,"P IS NOT NULL TYPE");
		p.TypeLine(cmd);
		return true;
	}

	public void DoExec(String [] args) throws NullPointerException,ArrayIndexOutOfBoundsException, IOException
	{
		//1단계 구현: single task
		Program program=session.getForegroundProgram();
		if(program==null)
		{
			if(args[1].compareToIgnoreCase("shell")==0)
			{
				session.addProgram(new ProgramShell(session));
			}
			else if(args[1].compareToIgnoreCase("filewriter")==0)
			{
				session.addProgram(new ProgramFileWriter(session));
			}
			else if(args[1].compareToIgnoreCase("mail")==0)
			{
				session.addProgram(new ProgramMail(session));
			}
			else
			{
				Log.e(TAG,"Shell no:"+args[1].concat("aa"));
			}
			program=session.getForegroundProgram();
			program.Start();
		}
		else
		{
			session.sender.Send(typeConverter.GetBatchInfo("",TypeConverter.FILETYPE.TEXTMSG,"error : already running"));
		}
	}
	
	private HashMap<String,Object> variables=new HashMap<String,Object>();
	
	public void DoSet(String []args)
	{
		String varname=args[1];
		variables.put(varname,DoEval(FinishArgs(args)));	
	}

	private Object DoEval(String args)
	{
		
		// TODO: Implement this method
		return null;
	}
	
	public void DoNews(String [] args)
	{	
		DoWget("http://news.google.co.kr/","news.txt");
	}
	public void DoBTTetheringOn(String[] a) throws Exception
	{
		Utility.TurnOnTethering(WorkerService.getContext());
	}
	public void DoBTTetheringOff(String[] a) throws NoSuchMethodException, InstantiationException, InvocationTargetException, SecurityException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException
	{
		Utility.TurnOffTethering(WorkerService.getContext());
	}
	public void DoGetInfo(String[] args) throws IOException
	{
		float temperature=Utility.getCpuTemperature();
		StringBuilder b=new StringBuilder();
		b.append("battery: "+Utility.getBatteryPercentage(WorkerService.getContext())+"%\n");
		b.append("wifi state: "+Utility.isWifiEnabled(WorkerService.getContext())+"\n");
		b.append("network connected: "+Utility.IsConnectedToInternet(WorkerService.getContext())+"\n");
		b.append("CPU temperature: "+temperature+"°C\n");
		b.append("Ambient temperature calculted: "+(2*temperature-36.5f)+"°C\n");
		
	/*	byte[] byt=(byte[]) session.typeConverter.ConvertTo(TypeConverter.FILETYPE.STRING,TypeConverter.FILETYPE.TEXTRESULT,b.toString());
		if(byt!=null)
			
			session.sender.Send("result.txt",byt);*/
		session.sender.Send(typeConverter.GetBatchInfo("",TypeConverter.FILETYPE.TEXTMSG,b.toString()));
	}
	
	public void DoHelp(String []args) throws IOException
	{
		session.sender.Send(typeConverter.GetBatchInfo("",TypeConverter.FILETYPE.TEXTMSG, WorkerSession.commandmap.keySet().toString()));
	}
	public void DoRecVideo0(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		msg.obj = FinishArgs(words);
		msg.what = 7;
		session.mHandler.sendMessage(msg);
	}

	public void DoStopRecVideo0(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		//Log.d(TAG, "Words FINARGS " + words + " " + FinishArgs(words));
		msg.obj = FinishArgs(words);
		msg.what = 8;
		session.mHandler.sendMessage(msg);
	}
	
	public void DoRecVideo1(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		msg.obj = FinishArgs(words);
		msg.what = 9;
		session.mHandler.sendMessage(msg);
	}

	public void DoStopRecVideo1(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		//Log.d(TAG, "Words FINARGS " + words + " " + FinishArgs(words));
		msg.obj = FinishArgs(words);
		msg.what = 10;
		session.mHandler.sendMessage(msg);
	}
	
	public void DoRecSound(String[] words)
	{
		Utility.startRec(FinishArgs(words));
	}
	public void DoStopRecSound(String[] words)
	{
		Utility.stopRec();
	}
	
	
	public void DoCamera1(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		msg.obj = FinishArgs(words);
		msg.what = 4;
		session.mHandler.sendMessage(msg);
	}

	public void DoCamera0(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		//Log.d(TAG, "Words FINARGS " + words + " " + FinishArgs(words));
		msg.obj = FinishArgs(words);
		msg.what = 3;
		session.mHandler.sendMessage(msg);
	}

	public void DoSay(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		msg.obj = FinishArgs(words);
		msg.what = 2;
		session.mHandler.sendMessage(msg);
	}

	public void DoToast(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		msg.obj = FinishArgs(words);
		msg.what = 1;
		session.mHandler.sendMessage(msg);
	}
	
	public void DoCd(String[] words)
	{
		String p="";
		String fin=FinishArgs(words);
		if(fin.compareToIgnoreCase("$download")==0)
		{
			p="/storage/emulated/0/Download";
		}
		
		try
		{				
			p = Utility.AppendPath(session.path, FinishArgs(words));
			Log.d(TAG, "AppendPath :"+p);
		}
		catch (IOException e)
		{
			p = null;
		}		
		if (p == null)
		{
			session.sender.SendError("No such file or directory");
			return;
		}
		session.path = p;
		session.sender.Send((DoLsSub(session.path)));
	}

	public void TurnOnWifi()
	{
		if(Utility.isWifiEnabled(WorkerService.getContext()))return;
		try
		{
			new ExecuteAsRootBase(){

				@Override
				protected ArrayList<String> getCommandsToExecute()
				{
					// TODO: Implement this method
					ArrayList<String> a=new ArrayList<String>();
					a.add("svc wifi enable");
					return a;
				}
			}.execute();
			session.sender.Send("Success");
		}
		catch (Exception e)
		{
			session.sender.SendError("error turning on");
		}
	}

	 public void DoWifiOn(String []args)
	 {
		 TurnOnWifi();
	 }
	public void DoWifiOff(String []args)
	{
		TurnOffWifi();
	}
	public void DoMailTo(String []args) throws NullPointerException, IOException, ArrayIndexOutOfBoundsException
	{
		//TurnOnWifi();
		DoExec(new String[]{"exec","mail","mail"});
	}
	public void TurnOffWifi()
	{
		try
		{
			new ExecuteAsRootBase(){

				@Override
				protected ArrayList<String> getCommandsToExecute()
				{
					// TODO: Implement this method
					ArrayList<String> a=new ArrayList<String>();
					a.add("svc wifi disable");
					return a;
				}
			}.execute();
		}
		catch (Exception e)
		{
			session.sender.SendError(e);
		}
	}

	public void DoRen(String[] words)
	{
		try
		{

			File file=new File(Utility.CompletePath(session.path, words[1]));
			File file2=new File(Utility.CompletePath(session.path, words[2]));
			if (file.renameTo(file2))
			{
				session.sender.Send("success");
			}
			else
			{
				session.sender.SendError("fail");
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			session.sender.SendError("Array out",e);
		}
	}

	public void DoWget(String [] q)
	{
		DoWget(FinishArgs(q));
	}
	public void DoWget(String query)
	{
		DoWget(query,"");
	}
	public void DoWget(String query, String as)
	{
		TurnOnWifi();
		try
		{
			Document doc=Jsoup.connect(query).get();
			session.sender.Send(typeConverter.GetBatchInfo(as,TypeConverter.FILETYPE.TEXTRESULT,doc.text()));
			//SendStringViaOPP(doc.html());
		}
		catch (Exception e)
		{
			session.sender.SendError( e);
		}
	}
	public void DoLs(String []words)
	{
		session.sender.Send(DoLsSub(FinishArgs(words)));
	}
	public String DoLsSub(String dirPath)
	{
		// TODO: Implement this method
		File f = new File(dirPath);
		if (!f.exists())
		{
			Log.e(TAG, "f no" + dirPath);
			return "no such file or dir " + dirPath;
		}
		File[] files = f.listFiles();
		StringBuilder result=new StringBuilder();
		try
		{
			result .append("Canonical=" + f.getCanonicalPath() + "\n");
		}
		catch (IOException e)
		{
			result.append("Canonical= fail\n");
			result.append("Path=" + f.getPath() + "\n");
		}

		for (File fil:files)
		{
			if (fil == null)
			{result.append("error\n");continue;}
			result .append(fil.getName());
			if (fil.isDirectory())
			{
				result.append("/");
			}
			result.append("\n");
		}
		return result.toString();
	}
	public  String trim(String s, int width)
	{
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}
	public void DoClose(String []s)
	{
		session.sender.Send("bye");
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		//msg.obj = FinishArgs(words);
		msg.what = 5;
		session.mHandler.sendMessage(msg);
	}

	public void DoSilent(String[] words)
	{
		Message msg = session.mHandler.obtainMessage();
		//msg.arg1 = startId;
		msg.obj = FinishArgs(words);
		msg.what = 6;
		session.mHandler.sendMessage(msg);
	}

	public void DoReboot(String []s)
	{
		new ExecuteAsRootBase(){

			@Override
			protected ArrayList<String> getCommandsToExecute()
			{
				// TODO: Implement this method
				ArrayList<String> a=new ArrayList<String>();
				a.add("svc power reboot");
				return a;
			}
		}.execute();
	}

	public void DoShutdown(String []args)
	{
		new ExecuteAsRootBase(){

			@Override
			protected ArrayList<String> getCommandsToExecute()
			{
				// TODO: Implement this method
				ArrayList<String> a=new ArrayList<String>();
				a.add("svc power shutdown");
				return a;
			}
		}.execute();
	}
	
	public void DoWikiHow(String[] words)
	{
		DoWget("http://m.wikihow.com/" + FinishArgs(words));
	}
	
	public void DoTranslate(String[] words)
	{
		DoWget("https://translate.google.co.kr/m/translate?hl=ko#auto/en/" + FinishArgs(words));
	}

	public void DoNdic(String[] words)
	{
		DoWget("https://m.search.naver.com/search.naver?query=" + FinishArgs(words) + "&where=m_ldic&sm=msv_hty",FinishArgs(words)+".txt");
	}

	public void DoWiki(String[] words)
	{
		DoWget("http://en.wikipedia.org/wiki/" + FinishArgs(words),FinishArgs(words)+".txt");
	}

	public void DoNamuwiki(String[] words)
	{
		DoWget("http://namu.wiki/w/" + FinishArgs(words),FinishArgs(words)+".txt");
	}

	public void DoGoogle(String[] words)
	{
		DoWget("https://google.co.kr/search?q=" + FinishArgs(words),FinishArgs(words)+".txt");
	}
	public void DoGet(String[] words) throws IOException
	{
		String path=Utility.CompletePath(session.path, FinishArgs(words));
		Log.d(TAG,"DoGet() path: "+path);
		session.sender.Send(typeConverter.GetBatchInfo(FinishArgs(words),typeConverter.getFileType(path),path));
	}
	
	public void DoWgetF(String[] words)
	{
		try
		{
			String s=FinishArgs(words);
			String filename=File.createTempFile("Daemon", ".".concat(Utility.GetFileExtension(s))).getCanonicalPath();
			FileUrlDownload.fileUrlDownload(s, filename);
			session.sender.Send(typeConverter.GetBatchInfo(
										new File(filename).getName(),
										typeConverter.getFileType(filename),
										filename));
//			{
//				session.sender.SendError("error wgetf " + s + filename);
//			}
		}
		catch (Exception e)
		{
			session.sender.SendError("Exception wgetf:" ,e);
		}
	}

	public void DoWgetImports(String[] words)
	{
		try
		{
			StringBuilder b=new StringBuilder();
			Document doc=Jsoup.connect(FinishArgs(words)).get();
			//SendStringViaOPP(doc.text());
			//Elements links = doc.select("a[href]");
			//Elements media = doc.select("[src]");
			Elements imports = doc.select("link[href]");

			/*b.append(new String().format("\nMedia: (%d)", media.size()));
			 for (Element src : media) {
			 if (src.tagName().equals("img"))
			 b.append(new String().format(" * %s: <%s> %sx%s (%s)",
			 src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
			 trim(src.attr("alt"), 20)));
			 else
			 b.append(new String().format(" * %s: <%s>", src.tagName(), src.attr("abs:src")));
			 }
			 */
			b.append(new String().format("\nImports: (%d)", imports.size()));
			for (Element link : imports)
			{
				b.append(new String().format(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel")));
			}

			//b.append(new String().format("\nLinks: (%d)", links.size()));
			/*for (Element link : links) {
			 b.append(new String().format(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35)));
			 }*/
			session.sender.Send(b.toString());
		}
		catch (Exception e)
		{
			session.sender.SendError( e);
		}
	}

	public void DoWgetLink(String[] words)
	{
		try
		{
			StringBuilder b=new StringBuilder();
			Document doc=Jsoup.connect(FinishArgs(words)).get();
			//SendStringViaOPP(doc.text());
			Elements links = doc.select("a[href]");
			//Elements media = doc.select("[src]");
			//Elements imports = doc.select("link[href]");

			//b.append(new String().format("\nMedia: (%d)", media.size()));
			/*for (Element src : media) {
			 if (src.tagName().equals("img"))
			 b.append(new String().format(" * %s: <%s> %sx%s (%s)",
			 src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
			 trim(src.attr("alt"), 20)));
			 else
			 b.append(new String().format(" * %s: <%s>", src.tagName(), src.attr("abs:src")));
			 }
			 */
			//	b.append(new String().format("\nImports: (%d)", imports.size()));
			/*for (Element link : imports) {
			 b.append(new String().format(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel")));
			 }*/

			b.append(new String().format("\nLinks: (%d)", links.size()));
			for (Element link : links)
			{
				b.append(new String().format(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35)));
			}
			session.sender.Send(b.toString());
		}
		catch (Exception e)
		{
			session.sender.SendError( e);
		}
	}

	public void DoWgetMedia(String[] words)
	{
		try
		{
			StringBuilder b=new StringBuilder();
			Document doc=Jsoup.connect(FinishArgs(words)).get();
			//SendStringViaOPP(doc.text());
			//Elements links = doc.select("a[href]");
			Elements media = doc.select("[src]");
			//Elements imports = doc.select("link[href]");

			b.append(new String().format("\nMedia: (%d)", media.size()));
			for (Element src : media)
			{
				if (src.tagName().equals("img"))
					b.append(new String().format(" * %s: <%s> %sx%s (%s)",
												 src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
												 trim(src.attr("alt"), 20)));
				else
					b.append(new String().format(" * %s: <%s>", src.tagName(), src.attr("abs:src")));
			}

			//b.append(new String().format("\nImports: (%d)", imports.size()));
			/*for (Element link : imports) {
			 b.append(new String().format(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel")));
			 }*/

			//b.append(new String().format("\nLinks: (%d)", links.size()));
			/*for (Element link : links) {
			 b.append(new String().format(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35)));
			 }*/
			session.sender.Send(b.toString());
		}
		catch (Exception e)
		{
			session.sender.SendError( e);
		}
	}

	public void DoWgetHtm(String[] words)
	{
		try
		{
			Document doc=Jsoup.connect(FinishArgs(words)).get();
			//SendStringViaOPP(doc.text());
			session.sender.Send(doc.text());
		}
		catch (Exception e)
		{
			session.sender.SendError( e);
		}
	}
	
	
	public void DoShell(String []args)
	{
		Log.v(TAG,"Doshell start");
		DataOutputStream os=null;
		// TODO: Implement this method
		try
		{
			java.lang.Process shProcess;
			try
			{
				shProcess = Runtime.getRuntime().exec("sh");

				os = new DataOutputStream(shProcess.getOutputStream());
				DataInputStream osRes = new DataInputStream(shProcess.getInputStream());

				if (null != os && null != osRes)
				{
					// Getting the id of the current user to check if this is root
	
						os.writeBytes(FinishArgs(args));
						//os.writeBytes("exit\n");		
						os.flush();
						os.close();
						//shProcess.waitFor();
						String answer="";
						String s;
						int i=0;
						while ((s=osRes.readLine())!=null&&"".compareToIgnoreCase(s)!=0&&i<100)
						{
							answer += s+"\n";
							Log.d(TAG,s+""+i);
							++i;
						}
						Log.d(TAG,answer);
						session.sender.Send(answer);
					}			
				
			}
			catch (IOException e)
			{
				Log.e(TAG,"",e);
				session.sender.SendError(e);
			}		
		}catch(Exception e)
		{
			Log.e(TAG,"",e);
			session.sender.SendError(e);
		}
		Log.v(TAG,"Doshell end");
		
		/*try
		{
			os.writeBytes("exit\n");
			os.flush();
			os.close();
		}
		catch (Exception e)
		{
			Log.e(TAG,"",e);
			session.sender.SendError(e);
		}*/
	}
	public String FinishArgs(String[] words)
	{
		// TODO: Implement this method
		//String result=new String();
		String [] tmp=new String[words.length - 1];
		System.arraycopy(words, 1, tmp, 0, words.length - 1);
		String s= TextUtils.join(" ", tmp);
		s = s.substring(0, s.length() - 1);

		Log.d(TAG,"FinishArgs: "+s);

		return s;
		/*int i=0;
		 for (String s:words)
		 {
		 if (i != 0)
		 {
		 result += (s + " ");
		 //Log.d(TAG, result);
		 }

		 ++i;
		 }
		 int j=result.lastIndexOf(" ");
		 if(j>0)
		 result= result.substring(0,j);
		 Log.d(TAG,"FinishArgs: "+result);*/
		//return result;
	}	
	public String FinishArgs(String []args, int shift,String delim)
	{
		args=ShiftArgs(args,shift);
		StringBuilder sb=new StringBuilder();
		sb.append(args[0]);
		int i=1;
		for(;i<args.length;++i)
		{
			sb.append(delim+args[i]);
		}
		return sb.toString();
	}

	private String[] ShiftArgs(String[] args, int shift)
	{
		// TODO: Implement this method
		String []result=new String[args.length-shift];
		System.arraycopy(args,shift,result,0,args.length-shift);
		return result;
	}
}
