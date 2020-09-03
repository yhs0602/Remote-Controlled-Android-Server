package com.kyunggi.worker2;

import android.app.*;
import android.os.*;
import android.util.*;
import java.io.*;
import java.util.*;

public class CompatibleService
{
//	public class MainActivity extends Activity {
//
//		public static final ArrayList<TestFileObserver> sListFileObserver = new ArrayList<TestFileObserver>();
//
//		 class TestFileObserver extends FileObserver {
//			private String mPath;
//
//			int[] eventValue = new int[] {FileObserver.ACCESS, FileObserver.ALL_EVENTS, FileObserver.ATTRIB, FileObserver.CLOSE_NOWRITE,FileObserver.CLOSE_WRITE, FileObserver.CREATE,
//				FileObserver.DELETE, FileObserver.DELETE_SELF,FileObserver.MODIFY,FileObserver.MOVED_FROM,FileObserver.MOVED_TO, FileObserver.MOVE_SELF,FileObserver.OPEN};
//			String[] eventName = new String[] {"ACCESS", "ALL_EVENTS", "ATTRIB", "CLOSE_NOWRITE", "CLOSE_WRITE", "CREATE",
//				"DELETE", "DELETE_SELF" , "MODIFY" , "MOVED_FROM" ,"MOVED_TO", "MOVE_SELF","OPEN"};
//
//
//			public TestFileObserver(String path) {
//				super(path);
//				mPath = path;
//				sListFileObserver.add(this);
//			}
//
//			public TestFileObserver(String path, int mask) {
//				super(path, mask);
//				mPath = path;
//				sListFileObserver.add(this);
//			}
//
//			@Override
//			public void onEvent(int event, String path) {
//				StringBuilder strEvents = new StringBuilder();
//				strEvents.append("Event : ").append('(').append(event).append(')');
//				for(int i = 0; i < eventValue.length; ++i) {
//					if((eventValue[i] & event) == eventValue[i]) {
//						strEvents.append(eventName[i]);
//						strEvents.append(',');
//					}
//				}
//				if((event & FileObserver.DELETE_SELF) == FileObserver.DELETE_SELF) {
//					sListFileObserver.remove(this);
//				}
//				strEvents.append("\tPath : ").append(path).append('(').append(mPath).append(')');
//				Log.i("TestFileObserverTestFileObserver",strEvents.toString());
//			}
//		}
//
//		@Override
//		protected void onCreate(Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//			monitorAllFiles(Environment.getExternalStorageDirectory());
//		}
//
//		private void monitorAllFiles(File root) {
//			File[] files = root.listFiles();
//			for(File file : files) {
//				TestFileObserver fileObserver = new TestFileObserver(file.getAbsolutePath());
//				fileObserver.startWatching();
//				if(file.isDirectory()) monitorAllFiles(file);
//			}
//		}
//	}
}
