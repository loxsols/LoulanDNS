set CMD_DIR=%~dp0
set ROOT_DIR=%CMD_DIR%\..\..\..\
set LIB_DIR=%ROOT_DIR%\lib
set HSQLDB_JAR=%LIB_DIR%\hsqldb.jar

rem DBのファイルを配置するディレクトリ
set DATA_DIR=%ROOT_DIR%\db\HSQLDB\

rem HSWLDBサーバーがbindするポート番号(デフォルトは9001)
set HSQLDB_BIND_PORT=9001

rem %DATA_DIR%に配置したDBファイルを開いてサーバーモードで起動する.
java -cp ^
%LIB_DIR%\hsqldb.jar ^
org.hsqldb.server.Server ^
--database.0 file:db\\HSQLDB\\LoulanDNS\\LoulanDNS  ^
--dbname.0 LoulanDNS ^
--port %HSQLDB_BIND_PORT%




