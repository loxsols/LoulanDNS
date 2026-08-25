@echo off
REM *********************
REM digコマンドを呼ぶためのスタブ
REM args : <digコマンドの引数群 ...>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM digコマンドのパス
set DIG_CMD=%CMD_DIR%\..\..\..\tools\dig\BIND9.16.43.x64\command\dig.exe


%DIG_CMD% %*

exit /b %ERRORLEVEL%





