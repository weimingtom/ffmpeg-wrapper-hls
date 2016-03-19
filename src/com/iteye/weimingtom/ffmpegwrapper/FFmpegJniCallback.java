package com.iteye.weimingtom.ffmpegwrapper;

public interface FFmpegJniCallback {
	public void onFinish(final int ret);
	public void onProgress(int percent);
}
