@echo off


REM *************************************
REM LoulanDNSのDynamicServiceLauncherの起動コマンド
REM 
REM USAGE : 
REM ************************


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\


REM 共通モジュール(common.bat)のパス
set COMMON_BAT=%PRJ_DIR%\bin\lib\common\common.bat


REM ----- 共通モジュール(common.bat)を呼び出す.
call %COMMON_BAT%




java ^
-Dloulan.dns.service.udp.address=%UDP_BIND_ADDRESS% ^
-Dloulan.dns.service.udp.port=%UDP_BIND_PORT% ^
-Dloulan.dns.service.endpoint.udp.address=%UDP_BIND_ADDRESS% ^
-Dloulan.dns.service.endpoint.udp.port=%UDP_BIND_PORT% ^
-Dloulan.dns.recursive.server.primary.host=%FOREIGN_PRIMARY_DNS_SERVER% ^
-Dloulan.dns.recursive.server.primary.port=%FOREIGN_PRIMARY_DNS_SERVER_PORT% ^
-Dloulan.dns.service.instance.default.instance.name=%DEFAULT_DNS_SERVICE_INSTANCE% ^
-Dloulan.dns.service.instance.name=%DEFAULT_DNS_SERVICE_INSTANCE% ^
-Dloulan.dns.service.instance.default.instance.user.name=%DEFAULT_USER_NAME% ^
-Dloulan.dns.user.name=%DEFAULT_USER_NAME% ^
-Dspring.datasource.url="%SPRING_DATASOURCE_URL%" ^
-jar LoulanDNS\target\DynamicServiceLauncher.jar ^
%* 






