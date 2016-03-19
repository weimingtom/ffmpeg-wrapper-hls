LOCAL_PATH := $(call my-dir)
##############################
include $(CLEAR_VARS)
include $(LOCAL_PATH)/../av.mk
LOCAL_SRC_FILES := $(FFFILES)
LOCAL_C_INCLUDES :=
LOCAL_C_INCLUDES += $(SYSROOT_INC)/usr/include 
#LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(LOCAL_PATH)/..

#LOCAL_SHORT_COMMANDS := true

LOCAL_CFLAGS += $(FFCFLAGS)
#LOCAL_LDLIBS := -lz
LOCAL_STATIC_LIBRARIES := $(FFLIBS)
LOCAL_MODULE := $(FFNAME)
include $(BUILD_STATIC_LIBRARY)
##############################
include $(CLEAR_VARS)
LOCAL_MODULE := postproc_prebuild
LOCAL_SRC_FILES := ../prebuild/$(TARGET_ARCH_ABI)/libpostproc.a
#LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_EXPORT_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)
##############################
