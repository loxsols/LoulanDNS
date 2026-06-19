@echo off

REM **************************************
REM DNSサーバーインスタンスの更新操作 テストバッチ
REM 
REM "USAGE : <server port> <admin user> <admin password> <target user> <target service-instance> <target key> <target value>"
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


if "%~6"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set TARGET_KEY=%6


if "%~7"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set TARGET_VALUE=%7



call %CURL_CMD% -X PUT localhost:8080/admin/api/dns/service/update/dns-service-instance -u %LOGIN_USER_NAME%:%LOGIN_PASSWORD% -d "UserName=%TARGET_USER%" -d "DNSServiceInstanceName=%TARGET_SERVICE_INSTANCE%" -d "%TARGET_KEY%=%TARGET_VALUE%



exit /b %ERRORLEVEL%




rem -----------
rem SUB PRINT_USAGE
rem -----------
:PRINT_USAGE
echo "USAGE : <server port> <admin user> <admin password> <target user> <target service-instance> <target key> <target value>"
echo "<target key> : DNSServiceInstanceExplain | DNSServiceTypeCode | DNSResolverInstanceID | RecordStatus | Memo "


exit /b 0



