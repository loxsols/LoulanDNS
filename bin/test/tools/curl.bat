@echo off
REM *********************
REM curlコマンドを呼ぶためのスタブ
REM args : <curlコマンドの引数群 ...>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM curlコマンドのパス
set CURL_CMD=%CMD_DIR%\..\..\..\tools\curl\curl-8.10.1_7-win64-mingw\bin\curl.exe


%CURL_CMD% %*

exit /b %ERRORLEVEL%





