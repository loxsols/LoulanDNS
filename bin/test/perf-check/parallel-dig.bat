@echo off

:: ==========================================
:: 設定項目
:: ==========================================
REM 本コマンドのディレクトリのパスを取得する.
set CMD_DIR=%~dp0

REM LoulanDNSプロジェクトのルートディレクトリのパスを取得する.
set PRJ_DIR=%CMD_DIR%..\..\..\
set PARALLEL_DIG_CMD=%PRJ_DIR%\bin\exec\command\parallel-dig.bat

echo %PARALLEL_DIG_CMD%

call %PARALLEL_DIG_CMD% %*

exit /b %ERRORLEVEL%





