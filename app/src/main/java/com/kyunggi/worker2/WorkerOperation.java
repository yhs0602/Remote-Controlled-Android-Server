package com.kyunggi.worker2;

public class WorkerOperation
{
	String line;
	String command;
	String [] parsed;
	Object data;
	public enum DATATYPE
	{
		STRING,
		PATH,
		URL,
		VARNAME	
	};
	DATATYPE dataType;
	
	public WorkerOperation(String command,DATATYPE resultType,Object data)
	{
		this.data=data;
		this.dataType=resultType;
		this.command=command;
	}
	
}
