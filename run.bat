
@echo off
setlocal

echo ===================================================
echo Launching The Groom Room - Salon Management System
echo ===================================================

set "VLC_DIR=C:\Program Files\VideoLAN\VLC"

if not exist "%VLC_DIR%\libvlc.dll" (
    set "VLC_DIR=C:\Program Files (x86)\VideoLAN\VLC"
)

if not exist "%VLC_DIR%\libvlc.dll" (
    echo ERROR: VLC native libraries were not found.
    echo Check your VLC installation folder.
    pause
    exit /b 1
)

set "PATH=%VLC_DIR%;%PATH%"
set "VLC_PLUGIN_PATH=%VLC_DIR%\plugins"

java --enable-native-access=ALL-UNNAMED ^
 -Dfile.encoding=UTF-8 ^
 -Djna.library.path="%VLC_DIR%" ^
 -Djava.library.path="%VLC_DIR%" ^
 -cp "bin;lib\*" com.thegroomroom.Main

echo.
echo Application exited with code %ERRORLEVEL%
pause
endlocal
