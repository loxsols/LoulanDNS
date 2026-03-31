@echo off

REM LoulanDNSの汎用エンドポイントサービスのビルドコマンド

REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0
set PRJ_DIR=%CMD_DIR%..\..\..\


REM ******
REM Windows11のコンソール文字コードをUTF-8に変更する.
REM (コンソールの文字コードがSJISの場合はMavenの出力文字列が化ける)
REM ******
chcp 65001



REM ******
REM ビルド
REM ******
mvn package spring-boot:repackage -f %PRJ_DIR%\LoulanDNS\pom.xml -PLoulanDNSEndpointService







