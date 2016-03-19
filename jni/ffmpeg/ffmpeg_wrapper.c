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
 *
 */
#include <string.h>
#include <jni.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libswscale/swscale.h>
#include <assert.h>
#include <android/log.h>
#include <signal.h>

#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <stdio.h>
#include <getopt.h>

#include "ffmpeg_wrapper.h"

extern void sigterm_handler(int sig);
extern void sigterm_handler_reset();

jmp_buf jmp_exit;

#define MODULE_FFMPEG "FFMPEG"

extern int __main(int argc, char** argv);

jmethodID FFMPEG_methodid_log = NULL;
jmethodID FFMPEG_methodid_progress = NULL;
JNIEnv * FFMPEG_env = NULL;
jobject FFMPEG_obj = NULL;
jboolean FFMPEG_verbose = 0;




//==================================================



static void log_callback_android(void *ptr, int level, const char *fmt, va_list vl)
{
	__android_log_vprint(ANDROID_LOG_INFO, MODULE_FFMPEG, fmt, vl);
}


JNIEXPORT jint JNICALL Java_com_iteye_weimingtom_ffmpegwrapper_FFmpegJni_main
  (JNIEnv *env, jobject self, jstring jCmd)
{
	int i;
	char* argv[32] = {0};
	char* pch;
	int argc = 0;
	char *cmd;

	const char *tmp = (*env)->GetStringUTFChars(env, jCmd, 0);
	i = (*env)->GetStringUTFLength(env, jCmd);
	cmd = calloc(sizeof(const char), i);
	strcpy(cmd, tmp);
	(*env)->ReleaseStringUTFChars(env, jCmd, tmp);

	__android_log_print(ANDROID_LOG_INFO, MODULE_FFMPEG, "cmd=[%s]", cmd);

	pch = strtok(cmd, " ");
	while (pch != NULL)
	{
		argv[argc++] = pch;
		pch = strtok(NULL, " ");
	}

	for (i = 0; i < argc; i++)
		__android_log_print(ANDROID_LOG_INFO, MODULE_FFMPEG, "argv[%d]=%s", i, argv[i]);

	av_log_set_callback(log_callback_android);

	if(setjmp(jmp_exit))
		return i;

	__main(argc, argv);

	free (cmd);
	return 0;
}

//============================================


void FFMPEG_callback_log(char* str)
{
    jclass stringClass;
    jmethodID cid;
    jcharArray elemArr;
    jstring result;
    jchar *chars = NULL;
    jint len;
    int i;
    JNIEnv * env;
    jobject obj;

    if (FFMPEG_env != NULL && FFMPEG_obj != NULL)
    {
    	env = FFMPEG_env;
    	obj = FFMPEG_obj;
		len = strlen(str);
		chars = (jchar *) malloc(len * sizeof(jchar));
		for (i = 0; i < len; i++)
		{
			chars[i] = str[i];
		}

		stringClass = (*env)->FindClass(env, "java/lang/String");
		if (stringClass == NULL)
		{
			return ;
		}

		cid = (*env)->GetMethodID(env, stringClass, "<init>", "([C)V");
		if (cid == NULL)
		{
			return ;
		}

		elemArr = (*env)->NewCharArray(env, len);
		if (elemArr == NULL)
		{
			return ;
		}

		(*env)->SetCharArrayRegion(env, elemArr, 0, len, chars);
		result = (*env)->NewObject(env, stringClass, cid, elemArr);
		(*env)->DeleteLocalRef(env, elemArr);

		if (FFMPEG_methodid_log != NULL)
		{
			(*env)->CallVoidMethod(env, obj, FFMPEG_methodid_log, result);
		}
		(*env)->DeleteLocalRef(env, result);
		(*env)->DeleteLocalRef(env, stringClass);

		if (chars != NULL)
		{
			free(chars);
			chars = NULL;
		}
    }
}

void FFMPEG_callback_progress(jlong size, jlong timestemp, jlong duration)
{
	JNIEnv * env;
    jobject obj;

    if (FFMPEG_env != NULL && FFMPEG_obj != NULL)
    {
    	env = FFMPEG_env;
    	obj = FFMPEG_obj;
		if (FFMPEG_methodid_progress != NULL)
		{
			(*env)->CallVoidMethod(env, obj, FFMPEG_methodid_progress, size, timestemp, duration);
		}
    }

    //__android_log_print(ANDROID_LOG_INFO, MODULE_FFMPEG, "size == %d, %d, %f\n", size, (long)((double)timestemp), (float)duration);
}






















JNIEXPORT void JNICALL Java_com_iteye_weimingtom_ffmpegwrapper_FFmpegJni_cancel
  (JNIEnv * env, jobject self)
{
	sigterm_handler(SIGINT);
}

JNIEXPORT void JNICALL Java_com_iteye_weimingtom_ffmpegwrapper_FFmpegJni_stop
  (JNIEnv * env, jobject self)
{
	Java_com_iteye_weimingtom_ffmpegwrapper_FFmpegJni_cancel(env, self);
}

JNIEXPORT void JNICALL Java_com_iteye_weimingtom_ffmpegwrapper_FFmpegJni_init
  (JNIEnv * env, jobject obj, jstring url, jstring dest, jboolean resume, jboolean verbose)
{
	const char *nativeUrl = (*env)->GetStringUTFChars(env, url, 0);
	const char *nativeDest = (*env)->GetStringUTFChars(env, dest, 0);
	char *argv[10];
	jclass cls;
	int argc;

	/*
	 * boolean Z
	 * byte	B
	 * char C
	 * short S
	 * int I
	 * long	J
	 * float F
	 * double D
	 * void	V
	 * Object Ljava/lang/String;
	 * Array [I [Ljava/lang/String;
	 *
	 */
	cls = (*env)->GetObjectClass(env, obj);
	FFMPEG_methodid_log = (*env)->GetMethodID(env, cls, "log", "(Ljava/lang/String;)V");
	FFMPEG_methodid_progress = (*env)->GetMethodID(env, cls, "progress", "(JJJ)V");
	FFMPEG_env = env;
	FFMPEG_obj = obj;
	FFMPEG_verbose = verbose;

	FFMPEG_callback_log("FFMPEG init...\n");
	__android_log_print(ANDROID_LOG_INFO, MODULE_FFMPEG, "FFMPEG init...\n");
	__android_log_print(ANDROID_LOG_INFO, MODULE_FFMPEG, "nativeUrl : %s\n", nativeUrl);
	__android_log_print(ANDROID_LOG_INFO, MODULE_FFMPEG, "nativeDest : %s\n", nativeDest);
	
	argv[0] = "ffmpeg";
	argv[1] = "-i";
	argv[2] = (char *)nativeUrl;
	argv[3] = "-vcodec";
	argv[4] = "copy";
	argv[5] = "-acodec";
	argv[6] = "copy";
	argv[7] = "-bsf:a";
	argv[8] = "aac_adtstoasc";
	argv[9] = "-y";
	argv[10] = (char *)nativeDest;
	argv[11] = NULL;
	argc = 11;

    sigterm_handler_reset();
    __main(argc, argv);
//    (*env)->ReleaseStringUTFChars(env, url, nativeUrl);
//    (*env)->ReleaseStringUTFChars(env, dest, nativeDest);
//    (*env)->DeleteLocalRef(env, cls);
}

