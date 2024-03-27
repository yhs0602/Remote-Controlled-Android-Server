package com.kyunggi.worker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import net.ypresto.androidtranscoder.MediaTranscoder;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.ypresto.androidtranscoder.format.*;
/**
 * VideoTranscodingUtils
 * Created by Pyxis on 2017-07-11.
 https://blog.uzuki.live/android-video-transcoding-api-18/
 */
public class VideoTranscodingUtils implements MediaTranscoder.Listener {
    private Activity activity;
    private ProgressDialog progressDialog;
    private String incomePath;
    private String outcomePath;
    private OnResultListener listener;
    public static final int TRANSCODING_SUCCESS = 1;
    public static final int TRANSCODING_FAILED = 2;
    public VideoTranscodingUtils(Activity activity, String path) {
        this.activity = activity;
        this.incomePath = path;
        File folder = new File(Environment.getExternalStorageDirectory(), "encoding_output/");
        folder.mkdir();
        try {
            this.outcomePath = File.createTempFile("encoding", ".mp4", folder).getAbsolutePath();
        } catch (IOException e) {
            Log.d(VideoTranscodingUtils.class.getSimpleName(), "encoding failed");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressDialog = new ProgressDialog(activity, android.R.style.Theme_Material_Light_Dialog);
        } else {
            progressDialog = new ProgressDialog(activity, android.R.style.Theme_Holo_Light_Dialog);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Transcoding...");
        progressDialog.setCancelable(false);
    }
    public void transcode(final OnResultListener listener) {
        this.listener = listener;
        FileDescriptor descriptor = getFileDescriptor();
        MediaTranscoder.getInstance().transcodeVideo(descriptor, outcomePath,
													 MediaFormatStrategyPresets.createAndroid720pStrategy(8000 * 1000, 128 * 1000, 1), this);

        activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					progressDialog.show();
				}
			});
    }
    private FileDescriptor getFileDescriptor() {
        File file = new File(incomePath);
        try {
            FileInputStream stream = new FileInputStream(file);
            return stream.getFD();
        } catch (IOException e) {
            return null;
        }
    }
    private void dismissDialog() {
        activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if (activity.isDestroyed() && !progressDialog.isShowing())
							return;
						progressDialog.dismiss();
					} catch (Exception e) {
					}
				}
			});
    }
    @Override
    public void onTranscodeProgress(final double progress) {
    }
    @Override
    public void onTranscodeCompleted() {
        dismissDialog();
        listener.onResult(TRANSCODING_SUCCESS, outcomePath);
    }
    @Override
    public void onTranscodeCanceled() {
        dismissDialog();
        listener.onResult(TRANSCODING_FAILED, incomePath);
    }
    @Override
    public void onTranscodeFailed(Exception exception) {
        dismissDialog();
        listener.onResult(TRANSCODING_FAILED, incomePath);
    }
    public interface OnResultListener {
        void onResult(int resultCode, String outPath);
    }
}
