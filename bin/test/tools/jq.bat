@echo off
REM *********************
REM jqコマンドを呼ぶためのスタブ
REM args : <jqコマンドの引数群 ...>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM jqコマンドのパス
set JQ_CMD=%CMD_DIR%\..\..\..\tools\jq\jq.bat


%JQ_CMD% %*

exit /b %ERRORLEVEL%





