@echo off

REM *************************************
REM LoulanDNSのバッチファイルの共通モジュール
REM 
REM USAGE : 
REM ************************



REM 本バッチファイルのあるディレクトリのパス.
set COMMON_BAT_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%COMMON_BAT_DIR%\..\..\..\


REM set HSQLDB_PATH="%PRJ_DIR%\..\data\db\HSQLDB\LoulanDNS\LoulanDNS"
set HSQLDB_PATH=%PRJ_DIR%/../data/db/HSQLDB/LoulanDNS/LoulanDNS


REM spring.datasource.urlのパス
REM ------- DBファイルを直接読み書きする場合.
REM set SPRING_DATASOURCE_URL="jdbc^:hsqldb^:file^:%PRJ_DIR%/../data/db/HSQLDB/LoulanDNS/LoulanDNS"
REM ------- サーバーモードのプロセスを経由する場合
set SPRING_DATASOURCE_URL="jdbc^:hsqldb^:hsql^:^//localhost/LoulanDNS"



REM set JDBC_DATASOURCE_URL_FILE="jdbc^:hsqldb^:file^:%PRJ_DIR%/../data/db/HSQLDB/LoulanDNS/LoulanDNS"
set JDBC_DATASOURCE_URL_FILE="jdbc^:hsqldb^:file^:%HSQLDB_PATH%"




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













