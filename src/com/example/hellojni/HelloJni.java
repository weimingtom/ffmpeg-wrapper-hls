/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.hellojni;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.iteye.weimingtom.ffmpegwrapper.FFmpegJni;
import com.iteye.weimingtom.ffmpegwrapper.FFmpegJniCallback;
import com.iteye.weimingtom.ffwhls.R;

public class HelloJni extends Activity {
	private final static boolean D = true;
	private final static String TAG = "HelloJni";
	
	private TextView textView1;
	private Button button1;
	private TextView textViewDetail;
	private Button buttonStop;
	
	private static final String INPUT_NAME = 
			"https://vms-api.hibiki-radio.jp/api/v1/videos/playlist.m3u8?token=SH1NPCkVMFZi8cyo0e6J5Dz7qb8aBfZrx8JyeL5KmAE%3D&vms_video_id=828&user_id=-1"; //TODO: This is Test URL, modified by yourself!!!
	private static final String OUTPUT_NAME = 
			"/mnt/sdcard/out.mp4";
	
	private static final String CMD_SAMPLE = "ffmpeg -i " +
			" %s " +
			" -vcodec copy -acodec copy -bsf:a aac_adtstoasc -y " +
			" %s ";

	public String url = null;
	private long duration = 0;
	private long progress = 0;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg != null) {
				switch (msg.what) {
				case FFmpegJni.MESSAGE_DURATION_THREAD:
					duration = ((Long)msg.obj).longValue();
					updateDetail();
					break;
					
				case FFmpegJni.MESSAGE_PROGRESS_THREAD:
					progress = ((Long)msg.obj).longValue();
					updateDetail();
					break;
				}
			}
		}
	};
	private StopTask curTask;
	
	public void updateDetail() {
		this.textViewDetail.setText(String.format("downloading : %d / %d / %.2f%%", progress, duration, ((double)progress / (double)duration) * 100.0));
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		textView1 = (TextView)this.findViewById(R.id.textView1);
		button1 = (Button)this.findViewById(R.id.button1);
		textViewDetail = (TextView)this.findViewById(R.id.textViewDetail);
		buttonStop = (Button)this.findViewById(R.id.buttonStop);
		
		Intent intent = this.getIntent();
		if (intent != null) {
			Uri data = intent.getData();
			if (data != null) {
				this.url = data.toString();
			}
		}
		
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (url != null) {
					curTask = new FFmpegJniTask(url, OUTPUT_NAME);
					curTask.start();
				} else {
					curTask = new FFmpegJniTask2();
					curTask.start();
				}
			}
		});
		buttonStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (curTask != null) {
					curTask.stop();
				}
			}
		});
	}

	private interface StopTask {
		void start();
		void stop();
	}
	
	private class FFmpegJniTask extends AsyncTask<Void, Void, Void> implements StopTask {
		private boolean isSuccess = false;
		private FFmpegJni ffmpegService = new FFmpegJni(handler);
		private String url, outputPath;
		
		public FFmpegJniTask(String url, String outputPath) {
			this.url = url;
			this.outputPath = outputPath;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			textView1.setText("url: " + url + "\noutput: " + outputPath);
			textViewDetail.setText("status: converting start");
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			ffmpegService.init(url, outputPath, false, true);
			publishProgress();
			isSuccess = true;
			return null;
		}
	
		@Override
		protected void onPostExecute(Void result_) {
			super.onPostExecute(result_);
			
			if (isSuccess) {
				
			}
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			textViewDetail.setText("status: converting end");
		}
		
		public void start() {
			this.execute();
		}
		
		public void stop() {
			ffmpegService.stop();
		}
	}
	
	
	private class FFmpegJniTask2 extends AsyncTask<Void, Void, Void> implements StopTask {
		private boolean isSuccess = false;
		private FFmpegJni ffmpegService = new FFmpegJni(handler);
		private String strCommandLine;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			strCommandLine = String.format(CMD_SAMPLE, INPUT_NAME, OUTPUT_NAME);
			if (D) {
				Log.e(TAG, "strCommandLine == " + strCommandLine);
			}
			textView1.setText("command: " + strCommandLine);
			textViewDetail.setText("status: converting start");
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			ffmpegService.execute(strCommandLine, new FFmpegJniCallback() {
				public void onFinish(final int ret) {
					publishProgress();
				}
				
				public void onProgress(int percent) {
					
				}
			});
			isSuccess = true;
			return null;
		}
	
		@Override
		protected void onPostExecute(Void result_) {
			super.onPostExecute(result_);
			
			if (isSuccess) {
				
			}
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
			textViewDetail.setText("status: converting end");
		}
		
		public void start() {
			this.execute();
		}
		
		public void stop() {
			ffmpegService.cancel();
		}
	}
	
}
