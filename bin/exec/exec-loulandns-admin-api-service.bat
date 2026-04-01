@echo off

REM *************************************
REM LoulanDNSのDoHサービスの起動コマンド
REM 
REM USAGE : 
REM ************************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\



REM spring.datasource.urlのパス
set SPRING_DATASOURCE_URL="jdbc^:hsqldb^:file^:%PRJ_DIR%/db/HSQLDB/LoulanDNS/LoulanDNS"



REM LoulanDNS(UDPサービス)のbindアドレスを指定する.
set UDP_BIND_ADDRESS=0.0.0.0


REM LoulanDNS(UDPサービス)のbindポート番号を指定する.
set UDP_BIND_PORT=50053


REM 外部DNSサーバー(UDP問い合わせ先)を指定する.
set FOREIGN_PRIMARY_DNS_SERVER=1.1.1.1

REM 外部DNSサーバー(UDP問い合わせ先)のポート番号を指定する.
set FOREIGN_PRIMARY_DNS_SERVER_PORT=53


REM デフォルトのユーザー名
set DEFAULT_USER_NAME=admin

REM デフォルトのDNSサービスインスタンス名
set DEFAULT_DNS_SERVICE_INSTANCE=default



java ^
-Dloulan.dns.service.udp.address=%UDP_BIND_ADDRESS% ^
-Dloulan.dns.service.udp.port=%UDP_BIND_PORT% ^
-Dloulan.dns.recursive.server.primary.host=%FOREIGN_PRIMARY_DNS_SERVER% ^
-Dloulan.dns.recursive.server.primary.port=%FOREIGN_PRIMARY_DNS_SERVER_PORT% ^
-Dloulan.dns.service.instance.default.instance.name=%DEFAULT_DNS_SERVICE_INSTANCE% ^
-Dloulan.dns.service.instance.default.instance.user.name=%DEFAULT_USER_NAME% ^
-Dspring.datasource.url="%SPRING_DATASOURCE_URL%" ^
-jar LoulanDNS\target\LoulanDNSAdminAPIService.jar 







