@set PATH=C:\Program Files (x86)\Java\jdk1.7.0_45\bin;%PATH%
@javah -jni -classpath ./bin/classes -d jni/ffmpeg com.iteye.weimingtom.ffmpegwrapper.FFmpegJni
@pause
