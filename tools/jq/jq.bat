@echo off


REM *************************************
REM jqコマンドの起動用I/Fバッチ
REM 
REM USAGE : 
REM ************************


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\


REM jqコマンドのパスをセット
set JQ_CMD=%PRJ_DIR%\tools\jq\jq-1.8.2\jq-windows-amd64.exe


REM コマンド実行
%JQ_CMD% %*

exit /b %ERRORLEVEL%






