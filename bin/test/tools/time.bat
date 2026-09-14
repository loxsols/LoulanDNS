@echo off
REM *********************
REM timeコマンドを呼ぶためのスタブ
REM args : <timeコマンドの引数群 ...>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM timeコマンドのパス
REM Windows環境ではJavaで実装したtimeコマンドもどき(bin\exec\command\time.bat)を呼ぶ.
set TIME_CMD=%CMD_DIR%\..\..\..\bin\exec\command\time.bat

%TIME_CMD% %*

exit /b %ERRORLEVEL%





