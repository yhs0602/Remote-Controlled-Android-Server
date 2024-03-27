package com.kyunggi.worker;

import android.util.*;
import java.io.*;
import java.net.*;
public class FileUrlDownload {
	/**
	 * 버퍼 사이즈
	 */
	final static int size = 1024;

	private static String TAG="BTOPP FILEURLDWNLD";
	/**
	 * fileAddress에서 파일을 읽어, 다운로드 디렉토리에 다운로드
	 * 
	 * @param fileAddress
	 * @param localFileName
	 * @param downloadDir
	 */
	public static void fileUrlReadAndDownload(String fileAddress,
											  String localFileName, String downloadDir) {
		OutputStream outStream = null;
		URLConnection uCon = null;
		InputStream is = null;
		try {
			//System.out.println("-------Download Start------");
			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			Url = new URL(fileAddress);
			outStream = new BufferedOutputStream(new FileOutputStream(
													 downloadDir + "\\" + localFileName));
			uCon = Url.openConnection();
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((byteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}
			Log.v(TAG,"Download Successfully.");
			Log.v(TAG,"File name : " + localFileName);
			Log.v(TAG,"of bytes  : " + byteWritten);
			Log.v(TAG,"-------Download End--------");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				outStream.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	/**
	 * 
	 * @param fileAddress
	 * @param downloadDir
	 */
	public static void fileUrlDownload(String fileAddress, String downloadDir) throws IOException {
		int slashIndex = fileAddress.lastIndexOf('/');
		int periodIndex = fileAddress.lastIndexOf('.');
		// 파일 어드레스에서 마지막에 있는 파일이름을 취득
		String fileName = fileAddress.substring(slashIndex + 1);
		if (periodIndex >= 1 && slashIndex >= 0
			&& slashIndex < fileAddress.length() - 1) {
			fileUrlReadAndDownload(fileAddress, fileName, downloadDir);
		} else {
			throw new IOException("path or file name NG.");
		}
	}

		// 파일 어드레스
		//String url = "http://localhost/download/index.php";
		// 다운로드 디렉토리
		//String downDir = "C:/Temp";
		// 다운로드 호출
		//fileUrlDownload(url, downDir);
	
}
