@echo off
REM *********************
REM Exec JUnit test.
REM args : HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\..\..\



REM spring.datasource.urlのパス
set SPRING_DATASOURCE_URL="jdbc^:hsqldb^:file^:%PRJ_DIR%/db/HSQLDB/LoulanDNS/LoulanDNS"



REM set JAR_JUNIT_STANDALONE=%PRJ_DIR%\lib\junit-platform-console-standalone-1.9.3.jar

set JAR_JUNIT4=%PRJ_DIR%\lib\junit-4.12.jar


REM set MAIN_CLASS=org.junit.runner.JUnitCore

set MAIN_JAR_FILE=%PRJ_DIR%\LoulanDNS\target\SpringTestRunnerCommandApplication.jar


set JAR_HAMCREST=%PRJ_DIR%\lib\hamcrest-core-1.3.jar


set JAR_SPRING_TEST=%CMD_DIR%\lib\spring-test-6.2.11.jar
REM set JAR_SPRING_TEST=%CMD_DIR%\lib\spring-test-5.2.25.RELEASE.jar
REM set JAR_SPRING_TEST=%PRJ_DIR%\lib\spring-test-4.0.5.RELEASE.jar


set JAR_SPRING_CORE=%CMD_DIR%\lib\spring-core-6.2.11.jar
REM set JAR_SPRING_CORE=%CMD_DIR%\lib\spring-core-5.2.25.RELEASE.jar
REM set JAR_SPRING_CORE=%CMD_DIR%\lib\spring-core-4.0.5.RELEASE.jar


set JAR_SPRING_BEANS=%CMD_DIR%\lib\spring-beans-6.2.11.jar
REM set JAR_SPRING_BEANS=%CMD_DIR%\lib\spring-beans-5.2.25.RELEASE.jar
REM set JAR_SPRING_BEANS=%CMD_DIR%\lib\spring-beans-4.0.5.RELEASE.jar


set JAR_SPRING_CONTEXT=%CMD_DIR%\lib\spring-context-6.2.11.jar
REM set JAR_SPRING_CONTEXT=%CMD_DIR%\lib\spring-context-5.2.25.RELEASE.jar
REM set JAR_SPRING_CONTEXT=%CMD_DIR%\lib\spring-context-4.0.5.RELEASE.jar


set JAR_SPRING_AOP=%CMD_DIR%\lib\spring-aop-6.2.11.jar
REM set JAR_SPRING_AOP=%CMD_DIR%\lib\spring-aop-5.2.25.RELEASE.jar
REM set JAR_SPRING_AOP=%CMD_DIR%\lib\spring-aop-4.0.5.RELEASE.jar


set JAR_SPRING_EXPRESSION=%CMD_DIR%\lib\spring-expression-6.2.11.jar
REM set JAR_SPRING_AOP=%CMD_DIR%\lib\spring-expression-5.2.25.RELEASE.jar
REM set JAR_SPRING_AOP=%CMD_DIR%\lib\spring-expression-4.0.5.RELEASE.jar




set JAR_COMMONS_LOGGING=%CMD_DIR%\lib\commons-logging-1.3.5.jar



set CLASS_PATH_JARS=%JAR_JUNIT4%;%JAR_COMMONS_LOGGING%;%JAR_SPRING_TEST%;%JAR_SPRING_CORE%;%JAR_SPRING_BEANS%;%JAR_SPRING_CONTEXT%;%JAR_SPRING_AOP%;%JAR_SPRING_EXPRESSION%;%JAR_HAMCREST%;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar


REM ----------- デバッグ用 -----------
REM echo CLASS_PATH_JARS=%CLASS_PATH_JARS%
REM ----------- デバッグ用 -----------


REM 引数チェック
set ARG1=%1
set ARG2=%2
set ARG3=%3


if "%ARG1%"=="" (
    echo "args : HELP | JUNIT-HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>"
    exit /b 0
)

if "%ARG1%"=="JUNIT-HELP" (
    java -cp %CLASS_PATH_JARS% -jar %MAIN_JAR_FILE%  --help
    exit /b 0
)

if "%ARG1%"=="HELP" (
    echo "args : HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>"
    exit /b 0
)


if "%ARG1%"=="ALL" (
    java -cp %CLASS_PATH_JARS% -Dspring.datasource.url="%SPRING_DATASOURCE_URL%" -jar %MAIN_JAR_FILE% --scan-classpath %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes 
    exit /b 0
)

if "%ARG1%"=="PACKAGE" (
    java-cp %CLASS_PATH_JARS%  -Dspring.datasource.url="%SPRING_DATASOURCE_URL%" %-jar %MAIN_JAR_FILE%  --select-package="%ARG2%"
    exit /b 0
)

if "%ARG1%"=="CLASS" (
    java -cp %CLASS_PATH_JARS%  -Dspring.datasource.url="%SPRING_DATASOURCE_URL%" -jar %MAIN_JAR_FILE%  "%ARG2%"
    exit /b 0
)

if "%ARG1%"=="METHOD" (
    java -cp %CLASS_PATH_JARS%  -Dspring.datasource.url="%SPRING_DATASOURCE_URL%" -jar %MAIN_JAR_FILE%  "%ARG2%"
    exit /b 0
)


REM *******************
REM 
REM 
REM *******************

REM java -jar %JAR_JUNIT_STANDALONE%  -cp %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar 

REM java -jar %JAR_JUNIT_STANDALONE%  -cp %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar --scan-classpath %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes

REM java -jar %JAR_JUNIT_STANDALONE%  -cp %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar --select-class="org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.DNSMessageDecodeTest"

REM java -jar %JAR_JUNIT_STANDALONE%  -cp %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar --select-method="org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.DNSMessageDecodeTest#testDecodeDNSQuestionMessage001"


REM org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.DNSMessageDecodeTest#testDecodeDNSQuestionMessage001

exit /b 0


