@echo off
REM *************************************
REM LoulanDNSのdigコマンド
REM 
REM USAGE : <query-name> [query-type] [query-class]
REM ************************


REM 本コマンドのディレクトリのパスを取得する.
set CMD_DIR=%~dp0

REM LoulanDNSプロジェクトのルートディレクトリのパスを取得する.
set PRJ_DIR=%CMD_DIR%..\..\..\

REM LoulanDNSのクライアントコマンド用jarファイルのパスを取得する.
set JAR_FILE_LOULAN_DNS_CLIENT_COMMAND=%PRJ_DIR%\LoulanDNS\target\LoulanDNSClientCommand.jar

REM LoulanDNSのクラスパスを取得する.
set CLASS_DIR_LOULAN_DNS=%PRJ_DIR%\LoulanDNS\target\classes\

REM LIBディレクトリのパス
set LIB_DIR=%PRJ_DIR%\lib\

REM -------- 以下は依存するJARファイル -------
REM args4jのJARファイル
set JAR_FILE_ARGS4J=%LIB_DIR%\args4j-2.33.jar

REM dnsjava(xbill)のJARファイル
set JAR_FILE_DNSJAVA=%LIB_DIR%\dnsjava-3.5.3.jar

REM slf4j-apiのJARファイル
set JAR_FILE_SLF4J_API=%LIB_DIR%\slf4j-api-2.0.12.jar
REM -------------------------------------------


REM メインクラス.
set MAIN_CLASS=org.loxsols.net.service.dns.loulandns.client.command.lookup.simple.SimpleDNSLookupCommandApplication

REM 引数チェック処理.
REM ---- java側で実装するので以下の処理はコメントアウトする.
REM if "%~1"=="" (
REM   echo "USAGE: <named.conf file>"
REM   exit /b 0
REM )


REM ******
REM 実行
REM ******
java -cp %CLASS_DIR_LOULAN_DNS%;%JAR_FILE_ARGS4J%;%JAR_FILE_DNSJAVA%;%JAR_FILE_SLF4J_API%; %MAIN_CLASS% %*








