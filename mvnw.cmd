@echo off
setlocal
set "MAVEN_PROJECTBASEDIR=%~dp0"
set "LOCAL_MAVEN=%MAVEN_PROJECTBASEDIR%.mvn\maven\bin\mvn.cmd"

if exist "%LOCAL_MAVEN%" (
    call "%LOCAL_MAVEN%" %*
    exit /b %ERRORLEVEL%
)

echo Maven not found. Downloading Maven (one-time)...
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup-maven-local.ps1"
if errorlevel 1 exit /b 1
call "%LOCAL_MAVEN%" %*
exit /b %ERRORLEVEL%
