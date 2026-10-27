@echo off

REM *************************************
REM LoulanDNSのDNSLookupコマンド
REM 
REM USAGE : <query-name> [query-type] [query-class]
REM ************************


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\


REM 共通モジュール(common.bat)のパス
set COMMON_BAT=%PRJ_DIR%\bin\lib\common\common.bat


REM ----- 共通モジュール(common.bat)を呼び出す.
call %COMMON_BAT%



rem 引数がない場合は終了する
REM if "%~1"=="" (
REM   echo "USAGE: <named.conf file>"
REM   exit /b 0
REM )


REM ******
REM 実行
REM ******
REM java -cp lib\dnsjava-3.5.3.jar;lib\slf4j-api-2.0.12.jar;LoulanDNS\target\LoulanDNS-springboot.jar ^
REM -jar LoulanDNS\target\LoulanDNS-springboot.jar ^
REM %1 




java -jar %PRJ_DIR%\LoulanDNS\target\LoulanDNSClientCommand.jar %*


exit /b %ERRORLEVEL%



