set CMD_DIR=%~dp0
set ROOT_DIR=%CMD_DIR%\..\..\..\
set LIB_DIR=%ROOT_DIR%\lib
set HSQLDB_JAR=%LIB_DIR%\hsqldb.jar

set DATA_DIR=%ROOT_DIR%\db\HSQLDB\


java -cp .\;%HSQLDB_JAR% org.hsqldb.util.DatabaseManagerSwing 






