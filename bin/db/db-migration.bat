@echo off


REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\


REM sqltoolのバッチファイル
set SQLTOOL_CMD=%PRJ_DIR%\bin\db\sqltool\HSQLDB_SQLTool.bat


REM 共通モジュール(common.bat)のパス
set COMMON_BAT=%PRJ_DIR%\bin\lib\common\common.bat


REM ----- 共通モジュール(common.bat)を呼び出す.
call %COMMON_BAT%

echo "Build for DB Schema..."
call %SQLTOOL_CMD% %PRJ_DIR%\db\DDL.sql 


REM DBのロック解除を確実にするために3秒待機する.
timeout /t 3 /nobreak >nul


echo "Build for DB Index..."
call %SQLTOOL_CMD% %PRJ_DIR%\db\IDX.sql 

REM DBのロック解除を確実にするために3秒待機する.
timeout /t 3 /nobreak >nul

echo "Build for DB init data..."
call %SQLTOOL_CMD% %PRJ_DIR%\db\INIT_DATA.sql 


echo "Finished DB migration process."


