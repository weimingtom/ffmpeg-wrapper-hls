#CAL_PATH := $(call my-dir)
#include $(CLEAR_VARS)
#LOCAL_WHOLE_STATIC_LIBRARIES := libavformat libavcodec libavutil libpostproc libswscale
#LOCAL_MODULE := ffmpeg
#include $(BUILD_SHARED_LIBRARY)
#include $(call all-makefiles-under,$(LOCAL_PATH))	
#LOCAL_STATIC_LIBRARIES := libavformat libavcodec libavutil libpostproc libswscale libavfilter libavdevice

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_WHOLE_STATIC_LIBRARIES := libavformat libavcodec libavutil libpostproc libswscale libswresample libavfilter libavdevice
LOCAL_MODULE := ffmpeg

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

LOCAL_LDLIBS    := -lz 
#LOCAL_LDLIBS    := -lz -llog -lGLESv2

include $(BUILD_EXECUTABLE)
#include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_STATIC_LIBRARY)
include $(call all-makefiles-under,$(LOCAL_PATH))
