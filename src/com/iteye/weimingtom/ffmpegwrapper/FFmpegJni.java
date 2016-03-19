package com.iteye.weimingtom.ffmpegwrapper;

import android.os.Handler;
import android.util.Log;

/**
 * original package:
 * 	no.hyper.ffmpegwrapper
 *
 */
public class FFmpegJni {
	private final static boolean D = true;
	private final static String TAG = "FFmpegJni";
	
	public static final int MESSAGE_DURATION_THREAD = 4441;
	public static final int MESSAGE_PROGRESS_THREAD = 4442;
	
	private Handler handler;
	private long timestamp;
	private long mDuration;
	
	public FFmpegJni(Handler handler) {
		this.handler = handler;
	}
	
	public native void init(String url, String dest, boolean resume, boolean verbose);
	public native void stop();

	private void log(String str) {
		if (D) {
			Log.e("FFmpegJni", str);
		}
		if (this.handler != null) {
			//this.handler.sendMessage(handler.obtainMessage(RTMPDownloadThread.MESSAGE_LOG_THREAD, 0, 0, str));
		}
	}
	
	private void progress(long size, long timestamp, long duration) {
		log("size = " + size + ", timestamp = " + timestamp + ", duration = " + duration + " \n");
		if (this.mDuration != duration) {
			this.mDuration = duration;
			if (this.handler != null && this.mDuration > 0) {
				this.handler.sendMessage(handler.obtainMessage(MESSAGE_DURATION_THREAD, 0, 0, Long.valueOf(this.mDuration)));
			}
		}
		if (this.handler != null) {
			this.handler.sendMessage(handler.obtainMessage(MESSAGE_PROGRESS_THREAD, 0, 0, Long.valueOf(timestamp)));
		}
	}

	//==============================================
	public FFmpegJni() {
		
	}
	
	public int execute(final String cmd, final FFmpegJniCallback cb) {
		int ret = main(cmd);
		cb.onFinish(ret);
		return 1;
	}
	
	public native int main(String strCommandLine);
	public native void cancel();
	//==============================================
	
	static {
//		System.loadLibrary("avutil-54");
//		System.loadLibrary("swscale-3");
//		System.loadLibrary("swresample-1");
//		System.loadLibrary("avcodec-56");
//		System.loadLibrary("avformat-56");
//		System.loadLibrary("avfilter-5");
//		System.loadLibrary("avdevice-56");
//		System.loadLibrary("ffmpeg-app");
		System.loadLibrary("ffmpeg");
	}
}
