package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base;

import java.util.ArrayList;
import java.util.Properties;

import java.util.ArrayList;
import java.util.Properties;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import java.io.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import android.providers.settings.GlobalSettingsProto.DateTime;


import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;



/**
 * 新しいプロセスを立ち上げてサービスを起動するランチャークラスの基底クラス.
 * 
 */
public class DynamicServiceLauncherBaseImpl implements IDynamicServiceLauncher
{


    /**
     * 動的にサービスを開始する.
     * 
     * @param serviceName
     * @param mainClassName
     * @param args
     * @param properties
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String mainClassName, String[] args, Properties properties) throws LoulanDNSSystemServiceException
    {
        LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException("NOT Implemented.");
        throw exception;
    }

    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String jarFilePath, String mainClassName, String[] args, Properties jvmProperties) throws LoulanDNSSystemServiceException
    {
        LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException("NOT Implemented.");
        throw exception;
    }



        /**
     * 現在実行中のJVMの実行ファイルのパスを取得する.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected String getCurrentJVMPath() throws LoulanDNSSystemServiceException
    {
        // 1. 現在動いている java コマンドのパスを自動取得 (環境依存を吸収)
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";

        return javaBin;
    }


    /**
     * 現在実行中のJARファイルのパスを取得する.
     * 
     * @return
     */
    protected String getCurrentJarPath() throws LoulanDNSSystemServiceException
    {
        // 現在実行中のFat JARの物理パスを自動取得
        String jarPath = new File(this.getClass().getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getAbsolutePath();

        return jarPath;
    }

    protected String getCurrentClassPath() throws LoulanDNSSystemServiceException
    {
        // 2. 現在実行中のJARファイル、またはクラスパスを自動取得
        String classPath = System.getProperty("java.class.path");
        return classPath;
    }


    protected List<String> buildJVMOptions(String mainClass, Properties userJvmProperties) throws LoulanDNSSystemServiceException
    {
        List<String> options = new ArrayList<>();

        // DBからロードした動的パラメータをシステムプロパティとして外付け
        // options.add("-Dserver.port=" + port);
        // options.add("-Dtarget.controller=" + targetController);
        options.add("-Dlogging.level.org.springframework.web=DEBUG"); // 必須のデバッグ用

        // 2. ★超重要: PropertiesLauncherに「本来起動してほしいメインクラス」を教える
        // options.add("-Dloader.main=org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.doh.DoHServiceEndpointInstanceSpringApplication"); 
        options.add( String.format("-Dloader.main=%s", mainClass ) ); 


        // ★動的に生成したJDBC URLを引数として追加
        // TODO : JDBCURLを動的に取得して生成し直す必要がある.
        // String jdbcURL = "jdbc:hsqldb:file:C:\\data\\workspace\\dev\\src\\LoulanDNS\\101_working\\LoulanDNS_20260531-001\\LoulanDNS\\bin\\exec\\.\\..\\..\\db\\HSQLDB\\LoulanDNS\\LoulanDNS";

        String jdbcURL = System.getProperty("spring.datasource.url");
        if ( jdbcURL == null || jdbcURL.isEmpty() )
        {
            String msg = String.format("Failed to build spring.datasource.url. jdbcURL is not specified. jdbcURL=%s", jdbcURL);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }
        System.out.println("[DEBUG] buildJVMOptions() jdbcURL=" + jdbcURL );

        options.add("-Dspring.datasource.url=" + jdbcURL);

        options.add("-Ddebug.mode=true"); // 必須のデバッグ用


        // ユーザー指定のJVMプロパティを設定.
        Object[] keys = userJvmProperties.keySet().toArray();
        for( int i=0; i < keys.length; i++ )
        {
            String key = (String)keys[i];
            String value = (String)userJvmProperties.get(key);

            String option = String.format("-D%s=%s", key, value);
            options.add( option );
        }

        return options;
    }


    protected List<String> buildCommnadLine(String mainClass, Properties userJvmProperties) throws LoulanDNSSystemServiceException
    {

        // 3. コマンドラインの組み立て
        List<String> command = new ArrayList<>();

        String javaBin = getCurrentJVMPath();
        command.add(javaBin);
        
        List<String> jvmOptions = buildJVMOptions(mainClass, userJvmProperties);
        command.addAll(jvmOptions);

        // クラスパス（またはJAR）を指定して、自分自身と同じメインクラスを実行
        String classPath = getCurrentClassPath();
        command.add("-cp");
        command.add(classPath);

        // 4. ★超重要: 起動する起点クラスを Spring Boot公式の PropertiesLauncher にする
        command.add("org.springframework.boot.loader.launch.PropertiesLauncher");

        return command;
    }


}