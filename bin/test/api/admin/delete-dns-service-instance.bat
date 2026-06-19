@echo off

REM **************************************
REM DNSサーバーインスタンスの削除操作 テストバッチ
REM 
REM "USAGE : <server port> <admin user> <admin password> <target user> <target service-instance>"
REM 
REM **************************************


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0

REM LoulanDNSプロジェクトのルートディレクトリのパス
set PRJ_ROOT_DIR=%CMD_DIR%\..\..\..\..\

REM CURLコマンドのスタブのパス
set CURL_CMD=%PRJ_ROOT_DIR%\bin\test\tools\curl.bat



if "%~1"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set SERVER_PORT=%1


if "%~2"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set LOGIN_USER_NAME=%2



if "%~3"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set LOGIN_PASSWORD=%3


if "%~4"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set TARGET_USER=%4


if "%~5"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set TARGET_SERVICE_INSTANCE=%5



call %CURL_CMD% -X DELETE localhost:8080/admin/api/dns/service/delete/dns-service-instance -u %LOGIN_USER_NAME%:%LOGIN_PASSWORD% -d "UserName=%TARGET_USER%" -d "DNSServiceInstanceName=%TARGET_SERVICE_INSTANCE%" 



exit /b %ERRORLEVEL%




rem -----------
rem SUB PRINT_USAGE
rem -----------
:PRINT_USAGE
echo "USAGE : <server port> <admin user> <admin password> <target user> <target service-instance>"


exit /b 0



