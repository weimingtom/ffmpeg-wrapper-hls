http://stackoverflow.com/questions/12598933/ndk-build-createprocess-make-e-87-the-parameter-is-incorrect

Ndk-build: CreateProcess: make (e=87): The parameter is incorrect

Maybe the LOCAL_SHORT_COMMANDS flag, to be set in your Android.mk, could help you. It is designed to overcome the limitations on the number of characters a Windows command can handle.

According to $(NDK folder)/docs/ANDROID-MK.html:

LOCAL_SHORT_COMMANDS

Set this variable to 'true' when your module has a very high number of sources and/or dependent static or shared libraries. This forces the build system to use an intermediate list file, and use it with the library archiver or static linker with the @$(listfile) syntax.

This can be useful on Windows, where the command-line only accepts a maximum of 8191 characters, which can be too small for complex projects.

This also impacts the compilation of individual source files, placing nearly all compiler flags inside list files too.

Note that any other value than 'true' will revert to the default behaviour. You can also define APP_SHORT_COMMANDS in your Application.mk to force this behaviour for all modules in your project.

NOTE: We do not recommend enabling this feature by default, since it makes the build slower.
Hope this helps!


