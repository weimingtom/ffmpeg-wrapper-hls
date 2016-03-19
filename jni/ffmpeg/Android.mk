#CAL_PATH := $(call my-dir)
#include $(CLEAR_VARS)
#LOCAL_WHOLE_STATIC_LIBRARIES := libavformat libavcodec libavutil libpostproc libswscale
#LOCAL_MODULE := ffmpeg
#include $(BUILD_SHARED_LIBRARY)
#include $(call all-makefiles-under,$(LOCAL_PATH))	
#LOCAL_STATIC_LIBRARIES := libavformat libavcodec libavutil libpostproc libswscale libavfilter libavdevice

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeg

# true if build a.out, or empty
LOCAL_FF_EXEC :=
# true if use prebuilt .a file, or empty
LOCAL_FF_PREBUILD := true

ifneq ($(LOCAL_FF_PREBUILD),)
LOCAL_WHOLE_STATIC_LIBRARIES := avformat_prebuild avcodec_prebuild avutil_prebuild postproc_prebuild swscale_prebuild swresample_prebuild avfilter_prebuild avdevice_prebuild 
else
LOCAL_WHOLE_STATIC_LIBRARIES := avformat avcodec avutil postproc swscale swresample avfilter avdevice avresample
endif

LOCAL_C_INCLUDES :=\
		   $(SYSROOT_INC)/usr/include \
		   $(LOCAL_PATH)/\
		   $(LOCAL_PATH)/libavcodec \
		   $(LOCAL_PATH)/libavdevice \
		   $(LOCAL_PATH)/libavfilter \
		   $(LOCAL_PATH)/libavformat \
		   $(LOCAL_PATH)/libavutil \
		   $(LOCAL_PATH)/libpostproc \
		   $(LOCAL_PATH)/libswscale \

LOCAL_SRC_FILES := \
		$(LOCAL_PATH)/ffmpeg.c\
		cmdutils.c \
		ffmpeg_opt.c \
		ffmpeg_filter.c 

ifneq ($(LOCAL_FF_EXEC),)
LOCAL_SRC_FILES +=
LOCAL_CFLAGS += 
else
LOCAL_SRC_FILES += ffmpeg_wrapper.c
LOCAL_CFLAGS += -DENABLE_ANDROID_FFMEPG_WRAPPER=1
endif

LOCAL_LDLIBS    := -lz -llog
#LOCAL_LDLIBS    := -lz -llog -lGLESv2

ifneq ($(LOCAL_FF_EXEC),)
include $(BUILD_EXECUTABLE)
else
include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY)
endif
include $(call all-makefiles-under,$(LOCAL_PATH))
