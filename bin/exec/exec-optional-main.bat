@echo off


REM *************************************
REM LoulanDNSの任意メインクラスの起動コマンド
REM 
REM USAGE : 
REM ************************


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\


REM 共通モジュール(common.bat)のパス
set COMMON_BAT=%PRJ_DIR%\bin\lib\common\common.bat


set MAIN_CLASS=%1



REM PropertiesLauncherを起動.なお、%1は既にmainクラスの指定で使用しているため%2以降をコマンド引数として渡す.
java ^
-cp %PRJ_DIR%\LoulanDNS\target\LoulanDNSAdminAPIService.jar ^
-Dloader.main=%MAIN_CLASS% ^
org.springframework.boot.loader.launch.PropertiesLauncher ^
%2 %3 %4 %5 %6 %7 %8 %9 


exit /b %ERRORLEVEL%





