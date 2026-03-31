@echo off


REM *************************************
REM LoulanDNSの汎用エンドポイントサービスの起動コマンド
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
-Dspring.datasource.url="%SPRING_DATASOURCE_URL%" ^
-jar LoulanDNS\target\LoulanDNSEndpointService.jar ^
%* 








