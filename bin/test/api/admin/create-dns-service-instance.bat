@echo off

REM **************************************
REM DNSサーバーインスタンスの新規作成 操作 テストバッチ
REM 
REM "USAGE : <server port> <admin user> <admin password> <target user> <service-instance> <explain> <service-type-code> <resolver-instance-id> <record-status> <memo>"
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
set EXPLAIN=%6


if "%~7"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set SERVICE_TYPE_CODE=%7


if "%~8"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set RESOLVER_ID=%8


if "%~9"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set RECORD_STATUS=%9


if "%~10"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set MEMO=%10




call %CURL_CMD% -X PUT localhost:8080/admin/api/dns/service/create/dns-service-instance -u %LOGIN_USER_NAME%:%LOGIN_PASSWORD% -d "UserName=%TARGET_USER%" -d "DNSServiceInstanceName=%TARGET_SERVICE_INSTANCE%" -d "DNSServiceInstanceExplain=%EXPLAIN%" -d "DNSServiceTypeCode=%SERVICE_TYPE_CODE%" -d "DNSResolverInstanceID=%RESOLVER_ID%" -d "RecordStatus=%RECORD_STATUS%" -d "Memo=%MEMO%" 




exit /b %ERRORLEVEL%




rem -----------
rem SUB PRINT_USAGE
rem -----------
:PRINT_USAGE
echo "USAGE : <server port> <admin user> <admin password> <target user> <service-instance> <explain> <service-type-code> <resolver-instance-id> <record-status> <memo>"

echo "<service-type-code> :=  DEFAULT 0"




exit /b 0



