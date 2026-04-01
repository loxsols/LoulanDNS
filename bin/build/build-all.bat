@echo off

REM LoulanDNSの全プロファイルの一括ビルドコマンド


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0
set PRJ_DIR=%CMD_DIR%..\..\

set MODOULE_BUILD_CMD_DIR=%CMD_DIR%\modules\



REM ******
REM Windows11のコンソール文字コードをUTF-8に変更する.
REM (コンソールの文字コードがSJISの場合はMavenの出力文字列が化ける)
REM ******
chcp 65001



REM ******
REM ビルド
REM ******

REM --- DNSLookupCommandApplication
echo "build for DNSLookupCommandApplication..."
call %MODOULE_BUILD_CMD_DIR%\build-for-DNSLookupCommandApplication.bat


REM --- DoHServerApplication
echo "build for DoHServerApplication..."
call %MODOULE_BUILD_CMD_DIR%\build-for-DoHServerApplication.bat


REM --- LoulanDNSAdminAPIServiceApplication
echo "build for LoulanDNSAdminAPIServiceApplication..."
call %MODOULE_BUILD_CMD_DIR%\build-for-LoulanDNSAdminAPIServiceApplication.bat


REM --- SpringTestRunnerCommandApplication
echo "build for SpringTestRunnerCommandApplication..."
call %MODOULE_BUILD_CMD_DIR%\build-for-SpringTestRunnerCommandApplication.bat


REM --- UDPServerApplication
echo "build for UDPServerApplication..."
call %MODOULE_BUILD_CMD_DIR%\build-for-UDPServerApplication.bat


REM --- EndpointServiceApplication
echo "build for EndpointServiceApplication..."
call %MODOULE_BUILD_CMD_DIR%\build-for-EndpointServiceApplication.bat





echo "build ALL done."

