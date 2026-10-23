

REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\..\


set LIB_DIR=%PRJ_DIR%\lib
set HSQLDB_JAR=%LIB_DIR%\hsqldb.jar

REM 共通モジュール(common.bat)のパス
set COMMON_BAT=%PRJ_DIR%\bin\lib\common\common.bat


REM ----- 共通モジュール(common.bat)を呼び出す.
call %COMMON_BAT%



REM java -cp .\;%HSQLDB_JAR% org.hsqldb.util.DatabaseManagerSwing ^
REM --driver org.hsqldb.jdbc.JDBCDriver ^
REM --url %JDBC_DATASOURCE_URL_FILE% ^
REM --user loulan ^
REM --password loulan 




REM java -cp .\;%HSQLDB_JAR% org.hsqldb.util.DatabaseManagerSwing ^
REM --driver org.hsqldb.jdbc.JDBCDriver ^
REM --url "jdbc:hsqldb:file:.\..\..\..\..\data\db\HSQLDB\LoulanDNS\LoulanDNS" ^
REM --user loulan ^
REM --password loulan 




java -cp .\;%HSQLDB_JAR% org.hsqldb.util.DatabaseManagerSwing ^
--driver org.hsqldb.jdbc.JDBCDriver ^
--url "%JDBC_DATASOURCE_URL_FILE%" ^
--user loulan ^
--password loulan 



