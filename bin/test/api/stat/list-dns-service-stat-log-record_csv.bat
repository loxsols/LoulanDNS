@echo off

REM **************************************
REM DNSサービスインスタンス層のDNS統計情報ログの取得バッチ
REM 
REM "USAGE : <server port> <admin user> <admin password> <user> <service-name> [from-date (Ex."2024-06-10T15:30:30Z")] [until-time (Ex."2024-06-10T15:30:30Z")] [max-size]"
REM 
REM 
REM **************************************


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0

REM LoulanDNSプロジェクトのルートディレクトリのパス
set PRJ_ROOT_DIR=%CMD_DIR%\..\..\..\..\

REM CURLコマンドのスタブのパス
set CURL_CMD=%PRJ_ROOT_DIR%\bin\test\tools\curl.bat

REM JQコマンドのスタブのパス
set JQ_CMD=%PRJ_ROOT_DIR%\bin\test\tools\jq.bat


REM JQスクリプトのパス
set JQ_SCRIPT=%CMD_DIR%\modules\dnslog2csv.jq



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
set USER_NAME=%4



if "%~5"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set SERVICE_NAME=%5



set FROM_DATE=%6


set UNTIL_DATE=%7


set MAX_SIZE=%8


REM ヘッダー行を出力
echo loggingDateTime, userName, dnsserviceInstanceName, qname, qtype, qclass, query-id, dns-response, start-time, end-time, process-time-msec 


call %CMD_DIR%\list-dns-service-stat-log-record_json.bat %* | %JQ_CMD% -r -f %JQ_SCRIPT%




exit /b %ERRORLEVEL%




rem -----------
rem SUB PRINT_USAGE
rem -----------
:PRINT_USAGE
echo "USAGE : <server port> <admin user> <admin password> <user> <service-name> [from-date (Ex."2024-06-10T15:30:30Z")] [until-time (Ex."2024-06-10T15:30:30Z")] [max-size]"

exit /b 0



