@echo off
REM *********************
REM Exec JUnit test.
REM args : HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\..\


set JAR_JUNIT_STANDALONE=%PRJ_DIR%\lib\junit-platform-console-standalone-1.9.3.jar



REM 引数チェック
set ARG1=%1
set ARG2=%2
set ARG3=%3


if "%ARG1%"=="" (
    echo "args : HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>"
    exit /b 0
)

if "%ARG1%"=="HELP" (
    echo "args : HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>"
    exit /b 0
)


if "%ARG1%"=="ALL" (
    java -jar %JAR_JUNIT_STANDALONE%  -cp %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar --scan-classpath %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes
    exit /b 0
)

if "%ARG1%"=="PACKAGE" (
    java -jar %JAR_JUNIT_STANDALONE%  -cp %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar --select-package="%ARG2%"
    exit /b 0
)

if "%ARG1%"=="CLASS" (
    java -jar %JAR_JUNIT_STANDALONE%  -cp %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar --select-class="%ARG2%"
    exit /b 0
)

if "%ARG1%"=="METHOD" (
    java -jar %JAR_JUNIT_STANDALONE%  -cp %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar --select-method="%ARG2%"
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


