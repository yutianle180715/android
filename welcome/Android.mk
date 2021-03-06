# Copyright (C) 2008 The Android Open Source Project
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := android-common android-support-v13 android-support-v7-recyclerview android-support-v17-leanback

LOCAL_INCLUDE_TV_RESOURCES := tvvi-res2k15 tvstrings
LOCAL_JAVA_LIBRARIES := tvfw tvwidgets2k15 jedijar
LOCAL_SRC_FILES := $(call all-java-files-under, src) 
LOCAL_SRC_FILES += src/org/droidtv/weather/WeatherBinder.aidl
LOCAL_SRC_FILES += src/org/droidtv/weather/WeatherCallback.aidl
LOCAL_SRC_DIR := $(LOCAL_PATH)/src
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_AAPT_FLAGS := --auto-add-overlay
LOCAL_PACKAGE_NAME := PhilipsWelcomeScreen
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

#include $(CLEAR_VARS)
#LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := jedi:/libs/jedijar.jar
#include $(BUILD_MULTI_PREBUILT)

include $(call all-makefiles-under,$(LOCAL_PATH))

