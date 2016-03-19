LOCAL_PATH := $(call my-dir)
##############################
include $(CLEAR_VARS)
include $(LOCAL_PATH)/../av.mk
LOCAL_SRC_FILES := $(FFFILES)
LOCAL_C_INCLUDES :=
LOCAL_C_INCLUDES += $(SYSROOT_INC)/usr/include 
#LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/..

LOCAL_SHORT_COMMANDS := true

LOCAL_CFLAGS += $(FFCFLAGS)
#LOCAL_CFLAGS += -include "string.h" -Dipv6mr_interface=ipv6mr_ifindex
#LOCAL_LDLIBS := -lz
#openssl
LOCAL_STATIC_LIBRARIES := $(FFLIBS) ssl crypto
LOCAL_MODULE := $(FFNAME)
include $(BUILD_STATIC_LIBRARY)
##############################
include $(CLEAR_VARS)
LOCAL_MODULE := avformat_prebuild
LOCAL_SRC_FILES := ../prebuild/$(TARGET_ARCH_ABI)/libavformat.a
LOCAL_STATIC_LIBRARIES := ssl_prebuild crypto_prebuild
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_EXPORT_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)
##############################
