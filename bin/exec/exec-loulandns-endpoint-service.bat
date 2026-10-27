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



REM 共通モジュール(common.bat)のパス
set COMMON_BAT=%PRJ_DIR%\bin\lib\common\common.bat


REM ----- 共通モジュール(common.bat)を呼び出す.
call %COMMON_BAT%



java ^
-Dspring.datasource.url="%SPRING_DATASOURCE_URL%" ^
-Dlogging.level.org.springframework.web=DEBUG ^
-jar %PRJ_DIR%\LoulanDNS\target\LoulanDNSEndpointService.jar ^
%* 


exit /b %ERRORLEVEL%







