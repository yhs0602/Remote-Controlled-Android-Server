package com.kyunggi.worker;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
public class FileUrlDownload {
	/**
	 * 버퍼 사이즈
	 */
	final static int size = 1024;
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
			System.out.println("-------Download Start------");
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
			System.out.println("Download Successfully.");
			System.out.println("File name : " + localFileName);
			System.out.println("of bytes  : " + byteWritten);
			System.out.println("-------Download End--------");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 
	 * @param fileAddress
	 * @param downloadDir
	 */
	public static void fileUrlDownload(String fileAddress, String downloadDir) {
		int slashIndex = fileAddress.lastIndexOf('/');
		int periodIndex = fileAddress.lastIndexOf('.');
		// 파일 어드레스에서 마지막에 있는 파일이름을 취득
		String fileName = fileAddress.substring(slashIndex + 1);
		if (periodIndex >= 1 && slashIndex >= 0
			&& slashIndex < fileAddress.length() - 1) {
			fileUrlReadAndDownload(fileAddress, fileName, downloadDir);
		} else {
			System.err.println("path or file name NG.");
		}
	}

		// 파일 어드레스
		//String url = "http://localhost/download/index.php";
		// 다운로드 디렉토리
		//String downDir = "C:/Temp";
		// 다운로드 호출
		//fileUrlDownload(url, downDir);
	
}
