@echo off
REM *********************
REM exec simple doh query command.
REM args : <doh server URI> <domain-name>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\..\





REM 引数チェック
set ARG1=%1
set ARG2=%2


if "%ARG1%"=="" (
    echo "args : <doh server URI> <domain-name>"
    echo "Ex : https://1.1.1.1/dns-query google.co.jp"
    exit /b 0
)

java ^
-cp  ^
%PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar ^
org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleDoHMessageTransporterImpl ^
%ARG1% ^
%ARG2% 

