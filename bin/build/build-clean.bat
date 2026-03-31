@echo off

REM LoulanDNSの一括クリーンコマンド


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
REM プロジェクトをクリーン
REM ******
mvn clean -f %PRJ_DIR%\LoulanDNS\pom.xml



