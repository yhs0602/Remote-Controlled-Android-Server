package com.kyunggi.worker2;

public class VNTParser
{
	String content="";
	String firstLine="";
	VNTParser(String s)
	{			 
		String[] w=s.split("\n");
		int phase=0;
		int len=w.length;
		reading:
		for (String str:w)
		{

			switch (phase)
			{
				case 0:
					break;
				case 1:
					break;
				case 2:
					int colindex=str.indexOf(":");
					if (colindex < 0)
					{
						isVNT = false;
						break reading;
					}
					isVNT = true;
					firstLine = str.substring(colindex + 1);
					content += firstLine;
					content += "\n";
					break;
				default:
					if (phase < len - 4)
					{
						content += str;
						content += "\n";
						break;
					}
			}
			++phase;
		}
	}

	boolean isVNT=false;
	public boolean isVNT()
	{
		return isVNT;
	}
	public String getFirstLine()
	{
		return firstLine;
	}

}
