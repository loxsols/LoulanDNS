@echo off

REM *********************
REM UDP Query program.
REM args : <UDP server port> <target-domain>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0




REM UDPサーバーポート
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
REM 以下のようにlookupコマンドを実行する.
REM	[dnsjavaのlookupサブコマンドでテスト]
REM	```
REM	>java -Ddns.server=127.0.0.1:50053 -cp ..\lib\slf4j-api-2.0.12.jar;..\lib\dnsjava-3.5.3.jar org.xbill.DNS.tools.Tools lookup <ドメイン>
REM	```
REM *******************
java -Ddns.server=127.0.0.1:%PORT% -cp %CMD_DIR%\..\..\lib\slf4j-api-2.0.12.jar;%CMD_DIR%\..\..\lib\dnsjava-3.5.3.jar org.xbill.DNS.tools.Tools lookup %DOMAIN%


exit /b 0





REM ---------
REM FUNC_PRINT_USAGE
REM 
REM ---------
:FUNC_PRINT_USAGE
echo "Usage : <UDP server port> <target-domain>"
exit /b 0


