@echo off

REM *************************************
REM LoulanDNSのDNSLookupコマンド
REM 
REM USAGE : <query-name> [query-type] [query-class]
REM ************************


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


java -jar LoulanDNS\target\LoulanDNSClientCommand.jar %*






