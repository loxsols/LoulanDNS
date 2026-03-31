@echo off

REM **************************************
REM ユーザー更新 テストバッチ
REM 
REM "USAGE : <server-port> <admin-user> <admin-password> <user-name> <user-password> [record-status] [memo]"
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
set USER_NAME=%4


if "%~5"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set USER_PASSWORD=%5





if "%~6" neq "" (
	set USER_RECORD_STATUS=%6
)

if "%~7" neq "" (
	set USER_MEMO=%7
)


if "%USER_RECORD_STATUS%" == "" (
	call %CURL_CMD% -X PUT localhost:%SERVER_PORT%/admin/api/update/user -u %LOGIN_USER_NAME%:%LOGIN_PASSWORD%  -d UserName=%USER_NAME% -d UserPassword=%USER_PASSWORD% 
) else (
	call %CURL_CMD% -X PUT localhost:%SERVER_PORT%/admin/api/update/user -u %LOGIN_USER_NAME%:%LOGIN_PASSWORD%  -d UserName=%USER_NAME% -d UserPassword=%USER_PASSWORD% -d RecordStatus=%USER_RECORD_STATUS% -d Memo=USER_MEMO%
)


exit /b %ERRORLEVEL%




rem -----------
rem SUB PRINT_USAGE
rem -----------
:PRINT_USAGE
echo "USAGE : <server-port> <admin-user> <admin-password> <user-name> <user-password> [record-status] [memo]"

exit /b 0



