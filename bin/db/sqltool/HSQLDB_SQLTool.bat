echo off


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\..\


set LIB_DIR=%PRJ_DIR%\lib
set HSQLDB_JAR=%LIB_DIR%\hsqldb.jar
set SQLTOOL_JAR=%LIB_DIR%\sqltool-2.7.4.jar

REM 共通モジュール(common.bat)のパス
set COMMON_BAT=%PRJ_DIR%\bin\lib\common\common.bat


REM ----- 共通モジュール(common.bat)を呼び出す.
call %COMMON_BAT%


if "%~1"=="" (
	call :PRINT_USAGE
	exit /b 201
)
set SQL_FILE=%1


java ^
-jar  %SQLTOOL_JAR% ^
--rcFile=%PRJ_DIR%\db\sqltool.ini ^
LoulanDNS ^
%SQL_FILE% 


exit /b %ERRORLEVEL%




rem -----------
rem SUB PRINT_USAGE
rem -----------
:PRINT_USAGE
echo "USAGE : <sql-file>"

exit /b 0


