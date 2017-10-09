# ffmpeg-wrapper-hls
This is fork of https://github.com/hyperoslo/ffmpeg-wrapper  

## HOW TO BUILD
* Generate dummy .a files, using gen_dummy.bat (in 4 folders)  
* Modify jni/ffmpeg/Android.mk : LOCAL_FF_EXEC, LOCAL_FF_PREBUILD, **recommend** LOCAL_FF_PREBUILD := (let it be empty string)  
* If LOCAL_FF_PREBUILD := true, please copy prebuild .a files to prebuild folder first.  
* **Building is very very slow, because LOCAL_SHORT_COMMANDS := true**  

## TODO  
* Merge patches under jni\doc\calc_duration_5_3  
