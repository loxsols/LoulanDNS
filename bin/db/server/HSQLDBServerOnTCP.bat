REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\..\


REM 共通モジュール(common.bat)のパス
set COMMON_BAT=%PRJ_DIR%\bin\lib\common\common.bat


REM ----- 共通モジュール(common.bat)を呼び出す.
call %COMMON_BAT%


set CMD_DIR=%~dp0
set ROOT_DIR=%CMD_DIR%\..\..\..\
set LIB_DIR=%ROOT_DIR%\lib
set HSQLDB_JAR=%LIB_DIR%\hsqldb.jar





rem DBのファイルを配置するディレクトリ
set DATA_DIR=%ROOT_DIR%\db\HSQLDB\

rem HSWLDBサーバーがbindするポート番号(デフォルトは9001)
set HSQLDB_BIND_PORT=9001

rem %DATA_DIR%に配置したDBファイルを開いてサーバーモードで起動する.
REM java -cp ^
REM %LIB_DIR%\hsqldb.jar ^
REM org.hsqldb.server.Server ^
REM --database.0 file:..\\data\\db\\HSQLDB\\LoulanDNS\\LoulanDNS  ^
REM --dbname.0 LoulanDNS ^
REM --port %HSQLDB_BIND_PORT%


java -cp ^
%LIB_DIR%\hsqldb.jar ^
org.hsqldb.server.Server ^
--database.0 file:%HSQLDB_PATH%  ^
--dbname.0 LoulanDNS ^
--port %HSQLDB_BIND_PORT%




