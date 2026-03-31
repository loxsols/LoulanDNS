@echo off
REM *********************
REM Exec JUnit test.
REM args : HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>
REM *********************



REM 本バッチファイルのあるディレクトリのパス.
set CMD_DIR=%~dp0


REM プロジェクトのディレクトリ
set PRJ_DIR=%CMD_DIR%\..\..\..\..\


REM set JAR_JUNIT_STANDALONE=%PRJ_DIR%\lib\junit-platform-console-standalone-1.9.3.jar

set JAR_JUNIT4=%PRJ_DIR%\lib\junit-4.12.jar


set JAR_HAMCREST=%PRJ_DIR%\lib\hamcrest-core-1.3.jar


set JAR_HSQLDB=%PRJ_DIR%\lib\hsqldb.jar


set JAR_JAKARTA_SERVLET_API=%CMD_DIR%\lib\jakarta.servlet-api-6.1.0.jar




set JAR_SPRING_BOOT=%CMD_DIR%\lib\spring-boot-3.5.6.jar

set JAR_SPRING_BOOT_STARTER=%CMD_DIR%\lib\spring-boot-starter-3.5.6.jar

set JAR_SPRING_BOOT_STARTER_SECURITY=%CMD_DIR%\lib\spring-boot-starter-security-3.5.6.jar



REM set JAR_SPRING_BOOT_STARTER_DATA_JPA=%CMD_DIR%\lib\spring-boot-starter-data-jpa-3.5.4.jar
set JAR_SPRING_BOOT_STARTER_DATA_JPA=%CMD_DIR%\lib\spring-boot-starter-data-jpa-3.5.6.jar




set JAR_SPRING_SECURITY_CORE=%CMD_DIR%\lib\spring-security-core-6.2.8.jar
set JAR_SPRING_SECURITY_CONFIG=%CMD_DIR%\lib\spring-security-config-6.2.8.jar
set JAR_SPRING_SECURITY_CRYPTO=%CMD_DIR%\lib\spring-security-crypto-6.2.8.jar
set JAR_SPRING_SECURITY_WEB=%CMD_DIR%\lib\spring-security-web-6.2.8.jar

set JAR_SPRING_SECURITY_OAUTH2=%CMD_DIR%\lib\spring-security-oauth2-2.5.2.RELEASE.jar


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


set JAR_SPRING_WEB=%CMD_DIR%\lib\spring-web-6.2.11.jar



REM set JAR_SPRING_DATA_JPA=%CMD_DIR%\lib\spring-data-jpa-3.5.5.jar
set JAR_SPRING_DATA_JPA=%CMD_DIR%\lib\spring-data-jpa-3.4.11.jar



set JAR_SPRING_DATA_COMMONS=%CMD_DIR%\lib\spring-data-commons-3.5.5.jar

set JAR_SPRING_DAO=%CMD_DIR%\lib\spring-dao-2.0.8.jar

set JAR_SPRING_ORM=%CMD_DIR%\lib\spring-orm-6.2.11.jar


set JAR_JAKARTA_PERSISTENCE_API=%CMD_DIR%\lib\jakarta.persistence-api-3.1.0.jar
REM set JAR_JAKARTA_PERSISTENCE_API=%CMD_DIR%\lib\jakarta.persistence-api-3.2.0.jar



set JAR_COMMONS_LOGGING=%CMD_DIR%\lib\commons-logging-1.3.5.jar



set CLASS_PATH_JARS=%JAR_JUNIT4%;%JAR_COMMONS_LOGGING%;%JAR_JAKARTA_SERVLET_API%;%JAR_SPRING_BOOT%;%JAR_SPRING_BOOT_STARTER%;%JAR_SPRING_BOOT_STARTER_DATA_JPA%;%JAR_SPRING_DATA_JPA%;%JAR_SPRING_DAO%;%JAR_SPRING_ORM%;%JAR_SPRING_DATA_COMMONS%;%JAR_SPRING_BOOT_STARTER_SECURITY%;%JAR_SPRING_SECURITY_WEB%;%JAR_SPRING_WEB%;%JAR_SPRING_SECURITY_CORE%;%JAR_SPRING_SECURITY_CONFIG%;%JAR_SPRING_SECURITY_CRYPTO%;%SPRING_SECURITY_WEB%;%JAR_SPRING_SECURITY_OAUTH2%;%JAR_SPRING_TEST%;%JAR_SPRING_CORE%;%JAR_SPRING_BEANS%;%JAR_SPRING_CONTEXT%;%JAR_SPRING_AOP%;%JAR_SPRING_EXPRESSION%;%JAR_HAMCREST%;%JAR_JAKARTA_PERSISTENCE_API%;%JAR_HSQLDB%;%PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes;%PRJ_DIR%\lib\dnsjava-3.5.3.jar;%PRJ_DIR%\lib\slf4j-api-2.0.12.jar;


REM ----------- デバッグ用 -----------
REM echo CLASS_PATH_JARS=%CLASS_PATH_JARS%
REM ----------- デバッグ用 -----------


set CLASS_PATH=%CLASS_PATH_JARS%;%PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes


REM ----------- デバッグ用 -----------
REM echo CLASS_PATH=%CLASS_PATH%
REM ----------- デバッグ用 -----------



set MAIN_CLASS=org.loxsols.net.service.dns.loulandns.server.http.spring.test.SpringTestRunnerCommandApplication





REM 引数チェック
set ARG1=%1
set ARG2=%2
set ARG3=%3


if "%ARG1%"=="" (
    echo "args : HELP | JUNIT-HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>"
    exit /b 0
)

if "%ARG1%"=="JUNIT-HELP" (
    java -cp %CLASS_PATH% %MAIN_CLASS%  --help
    exit /b 0
)

if "%ARG1%"=="HELP" (
    echo "args : HELP | ALL | PACKAGE <package> | CLASS <package>.<class> | METHOD <package>.<class>#<method>"
    exit /b 0
)


if "%ARG1%"=="ALL" (
    java -cp %CLASS_PATH% %MAIN_CLASS%  --scan-classpath %PRJ_DIR%\LoulanDNS\target\classes;%PRJ_DIR%\LoulanDNS\target\test-classes 
    exit /b 0
)

if "%ARG1%"=="PACKAGE" (
    java-cp %CLASS_PATH%  %MAIN_CLASS%  --select-package="%ARG2%"
    exit /b 0
)

if "%ARG1%"=="CLASS" (
    java -cp %CLASS_PATH%  %MAIN_CLASS%  "%ARG2%"
    exit /b 0
)

if "%ARG1%"=="METHOD" (
    java -cp %CLASS_PATH%  %MAIN_CLASS%  "%ARG2%"
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


