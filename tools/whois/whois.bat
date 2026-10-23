@echo off


REM *************************************
REM whoisコマンドの起動用I/Fバッチ
REM 
REM USAGE : 
REM ************************


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\


REM whoisコマンドのパスをセット
set WHOIS_CMD=%PRJ_DIR%\tools\whois\t0rzz\whois\v20260730-fb42fa1\whois-x64.exe


REM コマンド実行
%WHOIS_CMD% %*

exit /b %ERRORLEVEL%






