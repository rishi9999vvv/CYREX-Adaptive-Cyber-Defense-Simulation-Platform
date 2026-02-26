@echo off
call "%~dp0find-java.cmd" || exit /b 1
set "PATH=%JAVA_BIN%;%PATH%"
for %%A in ("%JAVA_BIN%\..") do set "JAVA_HOME=%%~fA"
call "%~dp0mvnw.cmd" javafx:run
exit /b %ERRORLEVEL%
