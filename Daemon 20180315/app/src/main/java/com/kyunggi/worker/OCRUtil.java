package com.kyunggi.worker;

import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.kyunggi.worker.*;
import java.io.*;

public class OCRUtil
{
	public class MainActivit {

		Bitmap image; //사용되는 이미지
		private TessBaseAPI mTess; //Tess API reference
		String datapath = "" ; //언어데이터가 있는 경로

		
		protected void onCreate(Bundle savedInstanceState) {
			//super.onCreate(savedInstanceState);
			//setContentView(R.layout.activity_main);

			//이미지 디코딩을 위한 초기화
			image = BitmapFactory.decodeFile(""); //샘플이미지파일
			//언어파일 경로
			datapath = getFilesDir()+ "/tesseract/";

			//트레이닝데이터가 카피되어 있는지 체크
			checkFile(new File(datapath + "tessdata/"));

			//Tesseract API
			String lang = "eng";

			mTess = new TessBaseAPI();
			mTess.init(datapath, lang);
		}

		//Process an Image
		public void processImage(View view) {
			String OCRresult = null;
			mTess.setImage(image);
			OCRresult = mTess.getUTF8Text();
			//TextView OCRTextView = (TextView) findViewById(R.id.OCRTextView);
			//OCRTextView.setText(OCRresult);
		}


		//copy file to device
		private void copyFiles() {
			try{
				String filepath = datapath + "/tessdata/eng.traineddata";
				AssetManager assetManager = getAssets();
				InputStream instream = assetManager.open("tessdata/eng.traineddata");
				OutputStream outstream = new FileOutputStream(filepath);
				byte[] buffer = new byte[1024];
				int read;
				while ((read = instream.read(buffer)) != -1) {
					outstream.write(buffer, 0, read);
				}
				outstream.flush();
				outstream.close();
				instream.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//check file on the device
		private void checkFile(File dir) {
			//디렉토리가 없으면 디렉토리를 만들고 그후에 파일을 카피
			if(!dir.exists()&& dir.mkdirs()) {
				copyFiles();
			}
			//디렉토리가 있지만 파일이 없으면 파일카피 진행
			if(dir.exists()) {
				String datafilepath = datapath+ "/tessdata/eng.traineddata";
				File datafile = new File(datafilepath);
				if(!datafile.exists()) {
					copyFiles();
				}
			}
		}
	}
}
