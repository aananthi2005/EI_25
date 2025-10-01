@echo off
echo ============================
echo Building RealTimeChat App...
echo ============================

REM Delete old compiled files
if exist out rmdir /s /q out

REM Compile all Java files into "out" folder
echo Compiling sources...
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo ============================
echo Compilation successful!
echo ============================

REM Run the Main class
echo Starting Chat Application...
java -cp out com.chatapp.Main 12345

pause
