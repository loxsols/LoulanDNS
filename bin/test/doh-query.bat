@echo off
REM *********************
REM DoH Query program.
REM args : <doh server port> <domain>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


set DOH_SERVER_HTTP_PATH="doh/default"


REM DoHサーバーポート
if "%~1"=="" (
	call :FUNC_PRINT_USAGE
	exit /b
)
set PORT=%1




REM 問い合わせるドメイン名
if "%~2"=="" (
	call :FUNC_PRINT_USAGE
	exit /b
)
set DOMAIN=%2



REM *******************
REM 以下のようにdoh.exeコマンドを実行する.
REM      doh query --servers http://127.0.0.1:8080/doh/default www.google.co.jp
REM 
REM 
REM *******************
%CMD_DIR%\tools\doh.exe query --servers http://127.0.0.1:%PORT%/%DOH_SERVER_HTTP_PATH% %DOMAIN%


exit /b 0





REM ---------
REM FUNC_PRINT_USAGE
REM 
REM ---------
:FUNC_PRINT_USAGE
echo "Usage : <DoH server port> <domain>"
exit /b 0


