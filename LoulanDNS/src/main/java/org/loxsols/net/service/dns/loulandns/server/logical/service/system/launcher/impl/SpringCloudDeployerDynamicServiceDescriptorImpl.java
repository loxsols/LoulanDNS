package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl;

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
import java.net.URISyntaxException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.cloud.deployer.spi.core.AppDefinition;
import org.springframework.cloud.deployer.spi.core.AppDeploymentRequest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.*;
import org.springframework.core.*;
import org.springframework.core.io.Resource;


/**
 * 動的サービスのディスクリプタの実装クラス
 * 
 */
@ComponentScan
public class SpringCloudDeployerDynamicServiceDescriptorImpl extends DyanmicServiceDescriptorBaseImpl implements IDyanmicServiceDescriptor
{

    // SpringCloudDeployerのインスタンス.
    // AppDeployerは、どのクラスを使用するかで、ローカルの別プロセスで起動するか、リモートやクラウド上で起動するかを制御できる.
    // LocalAppDeployerなどをConfigクラスでインスタンス化してDIするのがいいだろう.
    AppDeployer appDeployer;
    public void setAppDeployer(AppDeployer instance)
    {
        this.appDeployer = instance;
    }



    private String springExecutableJarPath;

    public void setSpringExecutableJarPath(String value)
    {
        this.springExecutableJarPath = value;
    }

    public String getSpringExecutableJarPath()
    {
        return this.springExecutableJarPath;
    }



    private String springApplicationMainClass;
    
    public void setSpringApplicationMainClass(String value)
    {
        this.springApplicationMainClass = value;
    }

    public String getSpringApplicationMainClass()
    {
        return this.springApplicationMainClass;
    }


    private String[] springApplicationArgs;
    public void setSpringApplicationArgs(String[] value)
    {
        this.springApplicationArgs = value;
    }

    public String[] getSpringApplicationArgs()
    {
        return this.springApplicationArgs;
    }

    private Properties jvmProperties;
    public void setJVMProperties(Properties value)
    {
        this.jvmProperties = value;
    }
    public Properties getJVMProperties()
    {
        return this.jvmProperties;
    }


    int bindWebServicePort;
    public void setBindWebServicePort(int value)
    {
        this.bindWebServicePort = value;
    }
    public int getBindWebServicePort()
    {
        return this.bindWebServicePort;
    }

    /**
     * コンストラクタ
     * 
     * @param commandLine   JVMの実行ファイルを含む起動コマンドライン
     * @throws LoulanDNSSystemServiceException
     */
    public SpringCloudDeployerDynamicServiceDescriptorImpl(AppDeployer appDeployer, String dynamicServiceName, String springExecutableJarPath, String springApplicationClass, String[] springApplicationArgs, Properties jvmProperties, int bindWebServicePort) throws LoulanDNSSystemServiceException
    {
        init(appDeployer, dynamicServiceName, springExecutableJarPath, springApplicationClass, springApplicationArgs, jvmProperties, bindWebServicePort);
        this.setDynamicServiceStatus( DynamicServiceLauncherConstants.CONST_SERVICE_STATUS_CODE_INACTIVE );
    }


    private void init(AppDeployer appDeployer, String dynamicServiceName, String springExecutableJarPath, String springApplicationMainClass, String[] springApplicationArgs, Properties jvmProperties, int bindWebServicePort) throws LoulanDNSSystemServiceException
    {
        setAppDeployer(appDeployer);

        setDynamicServiceName(dynamicServiceName);
        setSpringExecutableJarPath(springExecutableJarPath);
        setSpringApplicationMainClass(springApplicationMainClass);
        setSpringApplicationArgs(springApplicationArgs);
        setJVMProperties(jvmProperties);
        setBindWebServicePort(bindWebServicePort);
    }



    public void startDynamicService() throws LoulanDNSSystemServiceException
    {

        startSpringApplication(this.getDynamicServiceName(), this.getBindWebServicePort(),  this.getSpringExecutableJarPath(), this.getSpringApplicationMainClass(), this.getSpringApplicationArgs(), this.getJVMProperties() );

        this.setDynamicServiceStatus( DynamicServiceLauncherConstants.CONST_SERVICE_STATUS_CODE_ACTIVE );

    }


    public void stopDynamicService() throws LoulanDNSSystemServiceException
    {
        // プロセスを終了する.
        String msg = String.format("NOT Implemented.");
        LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
        throw exception;
    }


    /**
     * 動的にメインクラスとポートを指定してExecutable JARを起動する
     *
     * @param appName      アプリの論理名
     * @param port         起動ポート
     * @param jarPath      子プロセスのJARパス
     * @param mainClassName 起動したいメインクラスのフルパス (例: "com.example.SubAppApplication")
     */
    /*
    protected String startSpringApplication(String appName, int port, String jarPath, String mainClassName, String[] args, Properties jvmProperties) throws LoulanDNSSystemServiceException
    {


        System.out.println( String.format("[DEBUG] startSpringApplication() : appName=%s, port=%d, jarPath=%s, mainClassName=%s", appName, port, jarPath, mainClassName) );

        FileSystemResource resource = new FileSystemResource(jarPath);

        // 子プロセスに渡すアプリケーション引数
        Map<String, String> appProperties = new HashMap<>();
        appProperties.put("server.port", String.valueOf(port));
        
        // 【重要】PropertiesLauncherに対するメインクラスの動的指定
        appProperties.put("loader.main", mainClassName);

        // 定義の作成
        AppDefinition definition = new AppDefinition(appName, appProperties);
        
        // デプロイプロパティ（必要ならJVMのメモリ割り当てなどを指定）
        Map<String, String> deploymentProperties = new HashMap<>();

        // クラスパスとメインクラスを直接Javaの起動引数として渡す
        // 実際のコマンド: java -cp "your-app.jar:lib/*" com.example.MyMainClass --server.port=xxxx
        deploymentProperties.put(
            "spring.cloud.deployer.local.javaOpts", 
            String.format("-cp %s", mainClassName) 
        );

        deploymentProperties.put("spring.cloud.deployer.local.inherit-log", "true" );



        // リクエスト作成とデプロイ
        AppDeploymentRequest request = new AppDeploymentRequest(definition, resource, deploymentProperties);
        String id = this.appDeployer.deploy(request);

        System.out.println( String.format("[DEBUG] startSpringApplication() : id=%s", id) );


        return id;
    }
    */


    /**
     * 動的にメインクラスとポートを指定してExecutable JARを起動する
     *
     * @param appName      アプリの論理名
     * @param port         起動ポート
     * @param jarPath      子プロセスのJARパス
     * @param mainClassName 起動したいメインクラスのフルパス (例: "com.example.SubAppApplication")
     */
    protected String startSpringApplication(String appName, int port, String jarPath, String mainClassName, String[] args, Properties jvmProperties) throws LoulanDNSSystemServiceException
    {

        Resource resource;
        // 1. 現在のクラス（このクラスが存在するJARまたはクラスパス）の場所を動的に特定
        // 1. Spring Bootが保持する元々のJARのパスを取得してみる
        String sunCommand = System.getProperty("sun.java.command"); // 例: "DynamicServiceLauncher.jar --port=8080"
        String javaClassPath = System.getProperty("java.class.path"); // クラスパス情報

        
        if (javaClassPath != null && javaClassPath.endsWith(".jar")) {
            // Fat JAR起動の場合はこれが直接JARのパスになる
            resource = new FileSystemResource(new File(javaClassPath));
        } else if (sunCommand != null && sunCommand.contains(".jar")) {
            // コマンドライン文字列からJARファイル部分だけを抽出
            String jarName = sunCommand.split(" ")[0];
            resource = new FileSystemResource(new File(jarName));
        } else {
            // 開発環境（IDE等）で通常のクラスディレクトリの場合のフォールバック
            String currentClassPath = SpringCloudDeployerDynamicServiceDescriptorImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            try {
                currentClassPath = java.net.URLDecoder.decode(currentClassPath, java.nio.charset.StandardCharsets.UTF_8);
            } catch (Exception e) {
              // ignore
               }
            resource = new FileSystemResource(new File(currentClassPath));
        }

        // 2. 子アプリケーションに渡すプロパティ（application.properties の上書きなど）
        Map<String, String> appProperties = new HashMap<>();
        appProperties.put("server.port", String.valueOf(58080));
        appProperties.put("loulansdns.odoh.enabled", "true"); // ODoHプロキシのみ有効化
        appProperties.put("spring.main.web-application-type", "none");

        // 3. デプロイ自体の制御プロパティ（JVM引数の指定など）
        Map<String, String> deploymentProperties = new HashMap<>();
        // 子プロセスのJVMメモリ割り当てを設定
        deploymentProperties.put("deployer.local.javaOpts", "-Xms256m -Xmx512m");

        // 【重要】Fat JAR内の標準メインクラスではなく、クラスパス内にある別のメインクラスを指定
        // LocalAppDeployerは、このプロパティを検知すると java -cp ... <指定クラス> の形で起動してくれます
        deploymentProperties.put("deployer.local.main", mainClassName );    
        
        // 子プロセスのログを親プロセスのコンソールにストリーム出力（これでエラーが100%見えるようになります）
        deploymentProperties.put("deployer.local.inheritLogging", "true");


// 【最重要】Fat JAR環境で別メインクラスを呼ぶための正しいアプローチ
        // java -cp ではなく、Spring Bootの引数としてメインクラスを上書き指示します
        appProperties.put("spring.main.main-class", mainClassName );
        // もし動かない場合は、以下のように loader.main を使用するためのJavaオプションを指定します
        deploymentProperties.put("deployer.local.javaOpts", mainClassName );


// 3. 【ここが重要】子プロセス（java -jar）に直接くっつける引数リストを明示的に作成
        List<String> commandlineArguments = new ArrayList<>();
commandlineArguments.add("--server.port=58080"); 
commandlineArguments.add("--spring.main.main-class=org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.test.TestApplication");

// 1. 【既存の設定にプラス】JPAの自動構成の除外を徹底する
commandlineArguments.add("--spring.autoconfigure.exclude="
        + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,"
        + "org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"); // Repositoriesの自動生成もオフに

// 2. 【これが本命】Springのコンポーネントスキャンに対して、LoulanDNSDBServiceを含むパッケージを無視するように指示する
// ※お使いの構成に応じて、DB系のクラスがまとまっているパッケージを指定してください
commandlineArguments.add("--spring.autoconfigure.exclude-by-package=org.loxsols.net.service.dns.loulandns.server.http.spring.service");

commandlineArguments.add("--spring.datasource.url=jdbc:hsqldb:file:C:\\data\\workspace\\dev\\src\\LoulanDNS\\101_working\\LoulanDNS_20260618-001\\LoulanDNS\\bin\\db\\HSQLDB\\LoulanDNS\\LoulanDNS");




        AppDefinition definition = new AppDefinition(appName, appProperties);

        // リクエストの組み立て
        // 第4引数に commandlineArguments を渡すことで、java -jar ... の後ろに確実に引数が追加されます
        AppDeploymentRequest request = new AppDeploymentRequest(
                definition, 
                resource, 
                deploymentProperties, 
                commandlineArguments
        );

        // 4. 別のJVMプロセスとしてデプロイ（非同期で起動します）
        String deploymentId = appDeployer.deploy(request);


        System.out.println( String.format("[DEBUG] startSpringApplication() : deploymentId=%s, mainClassName=%s", deploymentId, mainClassName) );




        // 返却されるdeploymentIdを使って、後からステータス確認やクローズ（undeploy）が可能
        return deploymentId;

    }

}

