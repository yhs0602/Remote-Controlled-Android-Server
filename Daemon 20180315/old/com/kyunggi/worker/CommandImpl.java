package com.kyunggi.worker;

import android.os.*;
import android.text.*;
import android.util.*;
import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class CommandImpl
{
	WorkerSession session;

	public String TAG;
	public CommandImpl(WorkerSession s)
	{
		session=s;
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

	
	public void DoWgetF(String[] words)
	{
		try
		{
			String s=FinishArgs(words);
			String filename=File.createTempFile("Daemon", ".".concat(Utility.GetFileExtension(s))).getCanonicalPath();
			FileUrlDownload.fileUrlDownload(s, filename);
			if (session.sender.Send(filename) == false)
			{
				
				session.sender.SendError("error wgetf " + s + filename);
			}
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

	public void DoCd(String[] words)
	{
		String p="";
		try
		{				
			p = Utility.AppendPath(session.path, FinishArgs(words));
			Log.d(TAG, p);
		}
		catch (IOException e)
		{
			p = null;
		}		
		if (p == null)
		{
			session.sender.SendError("No such file or directory");
			//return;
		}
		session.path = p;
		session.sender.Send((DoLsSub(session.path)));
	}

	public void TurnOnWifi()
	{
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
	public void DoMailTo(String []args)
	{
		//TurnOnWifi();
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
		try
		{
			Document doc=Jsoup.connect(query).get();
			session.sender.Send(doc.text());
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

	public void DoTranslate(String[] words)
	{
		DoWget("https://translate.google.co.kr/m/translate?hl=ko#auto/en/" + FinishArgs(words));
	}

	public void DoNdic(String[] words)
	{
		DoWget("https://m.search.naver.com/search.naver?query=" + FinishArgs(words) + "&where=m_ldic&sm=msv_hty");
	}

	public void DoWiki(String[] words)
	{
		DoWget("http://en.wikipedia.org/wiki/" + FinishArgs(words));
	}

	public void DoNamuwiki(String[] words)
	{
		DoWget("http://namu.wiki/w/" + FinishArgs(words));
	}

	public void DoGoogle(String[] words)
	{
		DoWget("https://google.co.kr/search?q=" + FinishArgs(words));
	}
	public void DoGet(String[] words)
	{
		session.sender.Send(Utility.CompletePath(session.path, FinishArgs(words)));
	}
	public void DoShell(String []args)
	{
		
	}
	public String FinishArgs(String[] words)
	{
		// TODO: Implement this method
		//String result=new String();
		String [] tmp=new String[words.length - 1];
		System.arraycopy(words, 1, tmp, 0, words.length - 1);
		String s= TextUtils.join(" ", tmp);
		s = s.substring(0, s.length() - 1);

		Log.d(TAG, s);

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
	
}
