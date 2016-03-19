#pragma once

#include <setjmp.h>
#include "com_iteye_weimingtom_ffmpegwrapper_FFmpegJni.h"

extern jmp_buf jmp_exit;

extern jmethodID FFMPEG_methodid_log;
extern JNIEnv * FFMPEG_env;
extern jobject FFMPEG_obj;
extern jboolean FFMPEG_verbose;
extern void FFMPEG_callback_log(char* str);
extern void FFMPEG_callback_progress(jlong size, jlong timestemp, jlong duration);
