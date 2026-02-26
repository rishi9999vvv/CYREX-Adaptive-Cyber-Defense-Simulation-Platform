@echo off
setlocal
set "JAVA_BIN="

:: Prefer JAVA_HOME if it is JDK 17 or 21
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        "%JAVA_HOME%\bin\java.exe" -version 2>&1 | findstr /C:"17" /C:"21" /C:"22" >nul
        if not errorlevel 1 (
            set "JAVA_BIN=%JAVA_HOME%\bin"
            goto :found
        )
    )
)

:: Search only paths that typically contain JDK 17/21 (never use Java 8)
for %%P in (
    "C:\Program Files\Java\jdk-21\bin"
    "C:\Program Files\Java\jdk-17\bin"
    "C:\Program Files\Java\jdk-22\bin"
    "C:\Program Files\Microsoft\jdk-21.0.5\bin"
    "C:\Program Files\Microsoft\jdk-17.0.13\bin"
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot\bin"
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot\bin"
    "C:\Program Files\Amazon Corretto\jdk17.0.13_11\bin"
    "C:\Program Files\Red Hat\java-17-openjdk-17.0.13.0.9-1.windows.x86_64\bin"
) do (
    if exist "%%~P\java.exe" (
        "%%~P\java.exe" -version 2>&1 | findstr /C:"17" /C:"21" /C:"22" >nul
        if not errorlevel 1 (
            set "JAVA_BIN=%%~P"
            goto :found
        )
    )
)

:: Eclipse Adoptium: any jdk-17 or jdk-21 folder (e.g. jdk-17.0.18.8-hotspot)
for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*" "C:\Program Files\Eclipse Adoptium\jdk-21*" "C:\Program Files\Eclipse Adoptium\jdk-22*") do (
    if exist "%%D\bin\java.exe" (
        "%%D\bin\java.exe" -version 2>&1 | findstr /C:"17" /C:"21" /C:"22" >nul
        if not errorlevel 1 (
            set "JAVA_BIN=%%D\bin"
            goto :found
        )
    )
)

:: Any folder under Program Files\Java whose name contains 17 or 21
for /d %%D in ("C:\Program Files\Java\*17*" "C:\Program Files\Java\*21*" "C:\Program Files\Java\*22*") do (
    if exist "%%D\bin\java.exe" (
        "%%D\bin\java.exe" -version 2>&1 | findstr /C:"17" /C:"21" /C:"22" >nul
        if not errorlevel 1 (
            set "JAVA_BIN=%%D\bin"
            goto :found
        )
    )
)

echo.
echo This project requires JDK 17 or 21. Your current Java is 8 or older.
echo.
echo Install JDK 17 from: https://adoptium.net/temurin/releases/?version=17
echo Then run:  .\compile.cmd   and   .\run.cmd
echo.
exit /b 1
:found
endlocal & set "JAVA_BIN=%JAVA_BIN%"
exit /b 0
