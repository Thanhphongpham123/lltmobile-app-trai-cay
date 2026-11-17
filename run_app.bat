@echo off
chcp 65001 >nul
set "JAVA_HOME=C:\Users\Phuong Anh\.jdk\jdk-21.0.10"
set "ANDROID_HOME=C:\Users\Phuong Anh\AppData\Local\Android\Sdk"
set "EMULATOR=%ANDROID_HOME%\emulator\emulator.exe"
set "PATH=%ANDROID_HOME%\platform-tools;%JAVA_HOME%\bin;%PATH%"

cd /d "c:\mb cuoi ky\FoodAppProject"

echo ===================================================
echo   KHOI CHAY PROJECT: QUAN TRAI CAY CAT SAN
echo ===================================================

echo [1/4] Dang bien dich APK (gradlew assembleDebug)...
call "c:\mb cuoi ky\FoodAppProject\gradlew.bat" assembleDebug
if errorlevel 1 (
    echo [LOI] Bien dich that bai!
    exit /b 1
)

adb.exe start-server >nul 2>&1
set DEVICE_FOUND=
for /f "tokens=1" %%d in ('adb.exe devices ^| findstr /r "device$"') do set DEVICE_FOUND=1

if not defined DEVICE_FOUND (
    echo [2/4] Dang bat may ao Android ^(Medium_Phone_API_36.1^)...
    start "" "%EMULATOR%" -avd Medium_Phone_API_36.1
) else (
    echo [2/4] May ao Android da dang mo san.
)

echo [3/4] Dang doi may ao san sang ket noi...
adb.exe wait-for-device
:wait_boot
set BOOT=
for /f "delims=" %%a in ('adb.exe shell getprop sys.boot_completed 2^>nul') do set BOOT=%%a
if not "%BOOT%"=="1" (
    ping 127.0.0.1 -n 3 >nul
    goto wait_boot
)

echo [4/4] Dang cai dat APK va mo ung dung tren may ao...
adb.exe install -r "c:\mb cuoi ky\FoodAppProject\app\build\outputs\apk\debug\app-debug.apk"
adb.exe shell am start -n "com.example.foodappproject/.login_activity"

echo ===================================================
echo   HOAN TAT! Ung dung da duoc mo tren may ao.
echo ===================================================
