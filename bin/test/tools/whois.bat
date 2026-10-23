@echo off
REM *********************
REM whoisコマンドを呼ぶためのスタブ
REM args : <whoisコマンドの引数群 ...>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM whoisコマンドのパス
set WHOIS_CMD=%CMD_DIR%\..\..\..\tools\whois\whois.bat


%WHOIS_CMD% %*

exit /b %ERRORLEVEL%





