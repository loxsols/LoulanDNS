package org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.doh;



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



import org.xbill.DNS.tools.*;
import org.xbill.DNS.ZoneTransferException;




import org.xbill.DNS.Address;
import org.xbill.DNS.CNAMERecord;
import org.xbill.DNS.Cache;
import org.xbill.DNS.Credibility;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DNAMERecord;
import org.xbill.DNS.ExtendedFlags;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.NameTooLongException;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Section;
import org.xbill.DNS.SetResponse;
import org.xbill.DNS.TSIG;
import org.xbill.DNS.TSIGRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.Zone;
import org.xbill.DNS.ZoneTransferException;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.SimpleResolver;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.ApplicationContext;


import org.loxsols.net.service.dns.loulandns.server.common.constants.*;
import org.loxsols.net.service.dns.loulandns.app.spring.service.endpoint.doh.DoHServiceApplicationConfig;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceInsufficientDNSRequestException;
import org.loxsols.net.service.dns.loulandns.server.common.InsufficientDNSMessageException;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSCommonUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.model.DNSServiceEndpointInstance;
import org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.base.DNSServiceEndpointInstanceImplBase;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDyanmicServiceDescriptor;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.IDynamicServiceLauncher;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.factory.IDynamicServiceLauncherFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.endpoint.*;

/**
 * DoHサービスエンドポイントの実装クラス.
 */
@SpringBootApplication
@ComponentScan(
    // 起動クラス側のスキャンからは、一旦すべての @RestController を一括除外する
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ANNOTATION,
        classes = RestController.class
    )
)
public class DoHServiceEndpointInstanceImpl extends DNSServiceEndpointInstanceImplBase implements IDNSServiceEndpointInstance, Runnable
{


  String dohServiceEndpointAddress;
  int dohServiceEndpointPort;


  LoulanDNSProtocolUtils protocolUtils = new LoulanDNSProtocolUtils();
  LoulanDNSCommonUtils commonUtils = new LoulanDNSCommonUtils();


  SpringContextHolder springContextHolder;


  IDynamicServiceLauncherFactory dynamicServiceLauncherFactory;


    @Autowired
    @Qualifier("dnsServiceInstanceFactoryImpl")
    public void setDNSServiceInstanceFactory(IDNSServiceInstanceFactory instance)
    {
        super.setDNSServiceInstanceFactory(instance);
    }


    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFactory(IDNSMessageFactory instance)
    {
        super.setDNSMessageFactory(instance);
    }


    @Autowired
    @Qualifier("springContextHolder")
    public void setSpringContextHolder(SpringContextHolder instance)
    {
      this.springContextHolder = instance;
    }



    @Autowired
    @Qualifier("loulanDNSDynamicServiceLauncherFactoryImpl")
    public void setDynamicServiceLauncherFactory(IDynamicServiceLauncherFactory instance)
    {
      this.dynamicServiceLauncherFactory = instance;
    } 

    public DoHServiceEndpointInstanceImpl()
    {
      super();
    }


    // DNSサービスエンドポイントのパラメーターを設定する.
    public void initDNSServiceEndpoint(Properties properties) throws DNSServiceCommonException
    {
      
        super.initDNSServiceEndpoint(properties);
        
        initDoHServiceEndpoint(properties);
    }

    /**
     * DoHサービスエンドポイントのパラメータを設定する.
     * @param properties
     * @throws DNSServiceCommonException
     */
    protected void initDoHServiceEndpoint(Properties properties) throws DNSServiceCommonException
    {

      // 本エンドポイントの設定には以下のパラメータを使用する.
      //  ----------------------------------------------
      //  以下のパラメータはベースクラスで既に読み取り済み.
      //    - loulan.dns.user.name
      //    - loulan.dns.service.instance.name
      //  ----------------------------------------------
      //
      //  -----------------------------------------------
      //  DoHエンドポイント独自のパラメータ
      //    - loulan.dns.service.endpoint.doh.address    :     bindするIPアドレスの文字列(IPv4/IPv6).オプション.省略時は例外をスロー.
      //    - loulan.dns.service.endpoint.doh.port       :     bindするポート番号の文字列.必須.省略時は例外をスロー.
      //  -----------------------------------------------

      String address = properties.getProperty( LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_DOH_BIND_ADDRESS );
      
      if ( address == null || address.equals("") )
      {
          String msg = String.format("DoH Service Endpoint Address is not specified. key=%s", LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_DOH_BIND_ADDRESS );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg);
          throw exception;
      }

      setDoHServiceEndpointAddress(address);

      String portString = properties.getProperty( LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_DOH_BIND_PORT );

      if ( portString == null || portString.equals("") )
      {
          String msg = String.format("UDP Service Endpoint is not specified. key=%s", LoulanDNSConstants.PROP_KEY_SERVICE_ENDPOINT_DOH_BIND_PORT );
          DNSServiceCommonException exception = new DNSServiceCommonException(msg);
          throw exception;
      }

      int port = Integer.parseInt(portString);
      setDoHServiceEndpointPort(port);

    }


    
    /**
     * サービスエンドポイントのメイン処理(スレッド内)
     * 
     * @throws DNSServiceCommonException
     */
    protected void doEndpointServiceTask() throws DNSServiceCommonException
    {

      
        // doDoHEndpointServiceTaskOnCurrentSpringContext();

        // doDoHEndpointServiceTaskOnOtherSpringContext();

        // doDoHEndpointServiceTaskOnOtherJVM();

        doDoHEndpointServiceTaskOnDynamicService();


    }

    

    /**
     * UDPサービスエンドポイントのメイン処理(スレッド内)
     * 
     * @throws DNSServiceCommonException
     */
    /*
    protected void doDoHEndpointServiceTask() throws DNSServiceCommonException
    {
      SocketAddress socketAddress = getDoHServiceSocketAddress();

      String[] args = new String[]{};
      // SpringApplication.run(DoHServiceEndpointInstanceImplConfig.class, args);

      var context = this.springContextHolder.getContext();

            context.close(); // 1つ目の古いサーバーを完全にシャットダウン（ポートを解放）
            

          // 1. 現在のコンテキスト（Webサーバー含む）を別スレッドで安全に終了させつつ、再起動する
            try
            {
              while(true)
              {
                Thread.sleep(1000);

                if ( context.isRunning() == false )
                {
                  break;
                }
              }

            }
            catch(InterruptedException cause)
            {
              String msg = String.format("Failed to Thread.sleep to wait for stop SpringApplication.");
              DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
              throw exception;
            }


        Thread thread = new Thread(() ->
        {

            // 2. 完全にクリーンな状態で、新しいインスタンスを立ち上げる
            SpringApplication.run(DoHServiceEndpointInstanceImplConfig.class, args);
        });
        
        thread.setDaemon(false);
        thread.start();

    }
    */

    /*
    protected void doDoHEndpointServiceTask() throws DNSServiceCommonException
    {

      // 1. 現在動いている java コマンドのパスを自動取得 (環境依存を吸収)
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";

        System.out.println("[DEBUG] doDoHEndpointServiceTask() : javaBin=" + javaBin);

        // 2. 現在実行中のJARファイル、またはクラスパスを自動取得
        String classPath = System.getProperty("java.class.path");

        System.out.println("[DEBUG] doDoHEndpointServiceTask() : classPath=" + classPath);


        // 3. コマンドラインの組み立て
        List<String> command = new ArrayList<>();
        command.add(javaBin);
        
        // DBからロードした動的パラメータをシステムプロパティとして外付け
        // command.add("-Dserver.port=" + port);
        // command.add("-Dtarget.controller=" + targetController);
        command.add("-Dlogging.level.org.springframework.web=DEBUG"); // 必須のデバッグ用

        // クラスパス（またはJAR）を指定して、自分自身と同じメインクラスを実行
        command.add("-cp");
        command.add(classPath);
        command.add("org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.doh.DoHServiceEndpointInstanceSpringApplication"); // あなたのSpring Bootの起動メインクラス名

        ProcessBuilder pb = new ProcessBuilder(command);
        
        // ★ ログ出力を親プロセスと完全に同期（一番楽なログ確認方法）
        pb.inheritIO(); 

        try {
            System.out.println("====== [親] 子プロセス（別JVM）を動的に生成します ======");
            Process process = pb.start();
            
            // 💡 起動した子プロセスのハンドリング（必要に応じて）
            // process.destroy() で後から親の都合で殺すことも可能です。
            
        } catch (IOException e) {
            System.err.println("子JVMのフォークに失敗しました: " + e.getMessage());
        }

    }
    */



    /**
     * UDPサービスエンドポイントのメイン処理(現在のJVM/Springコンテキスト内の別スレッド)
     * 
     * @throws DNSServiceCommonException
     */
    protected void doDoHEndpointServiceTaskOnCurrentSpringContext() throws DNSServiceCommonException
    {

      System.out.println("[DEBUG] doDoHEndpointServiceTaskOnCurrentSpringContext()");

      SocketAddress socketAddress = getDoHServiceSocketAddress();

      // TODO

    }

  
    protected void doDoHEndpointServiceTaskOnOtherSpringContext() throws DNSServiceCommonException
    {
      SocketAddress socketAddress = getDoHServiceSocketAddress();

      String[] args = new String[]{};
      // SpringApplication.run(DoHServiceEndpointInstanceImplConfig.class, args);

      var context = this.springContextHolder.getContext();

            context.close(); // 1つ目の古いサーバーを完全にシャットダウン（ポートを解放）
            

          // 1. 現在のコンテキスト（Webサーバー含む）を別スレッドで安全に終了させつつ、再起動する
            try
            {
              while(true)
              {
                Thread.sleep(1000);

                if ( context.isRunning() == false )
                {
                  break;
                }
              }

            }
            catch(InterruptedException cause)
            {
              String msg = String.format("Failed to Thread.sleep to wait for stop SpringApplication.");
              DNSServiceCommonException exception = new DNSServiceCommonException(msg, cause);
              throw exception;
            }


        Thread thread = new Thread(() ->
        {

            // 2. 完全にクリーンな状態で、新しいインスタンスを立ち上げる
            SpringApplication.run(DoHServiceEndpointInstanceImplConfig.class, args);
        });
        
        thread.setDaemon(false);
        thread.start();

    }



    protected void doDoHEndpointServiceTaskOnOtherJVM() throws DNSServiceCommonException
    {



      // 1. 現在動いている java コマンドのパスを自動取得 (環境依存を吸収)
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
        
        // 現在実行中のFat JARの物理パスを自動取得
        String jarPath = new File(this.getClass().getProtectionDomain()
                .getCodeSource().getLocation().getPath()).getAbsolutePath();

        // 2. 現在実行中のJARファイル、またはクラスパスを自動取得
        String classPath = System.getProperty("java.class.path");

        System.out.println("[DEBUG] doDoHEndpointServiceTask() : javaBin=" + javaBin);
        System.out.println("[DEBUG] doDoHEndpointServiceTask() : jarPath=" + jarPath);
        System.out.println("[DEBUG] doDoHEndpointServiceTask() : classPath=" + classPath);

        // 3. コマンドラインの組み立て
        List<String> command = new ArrayList<>();
        command.add(javaBin);
        
        // DBからロードした動的パラメータをシステムプロパティとして外付け
        // command.add("-Dserver.port=" + port);
        // command.add("-Dtarget.controller=" + targetController);
        command.add("-Dlogging.level.org.springframework.web=DEBUG"); // 必須のデバッグ用


      // 2. ★超重要: PropertiesLauncherに「本来起動してほしいメインクラス」を教える
        command.add("-Dloader.main=org.loxsols.net.service.dns.loulandns.server.impl.service.endpoint.doh.DoHServiceEndpointInstanceSpringApplication"); 


        // ★動的に生成したJDBC URLを引数として追加
        String jdbcURL = "jdbc:hsqldb:file:C:\\data\\workspace\\dev\\src\\LoulanDNS\\101_working\\LoulanDNS_20260531-001\\LoulanDNS\\bin\\exec\\.\\..\\..\\db\\HSQLDB\\LoulanDNS\\LoulanDNS";
        command.add("-Dspring.datasource.url=" + jdbcURL);


        command.add("-Ddebug.mode=true"); // 必須のデバッグ用



        // クラスパス（またはJAR）を指定して、自分自身と同じメインクラスを実行
        command.add("-cp");
        command.add(classPath);

        // 4. ★超重要: 起動する起点クラスを Spring Boot公式の PropertiesLauncher にする
        command.add("org.springframework.boot.loader.launch.PropertiesLauncher");


        ProcessBuilder pb = new ProcessBuilder(command);
        
        // ★ ログ出力を親プロセスと完全に同期（一番楽なログ確認方法）
        pb.inheritIO(); 

        try {
            System.out.println("====== [親] 子プロセス（別JVM）を動的に生成します ======");
            Process process = pb.start();
            
            // 💡 起動した子プロセスのハンドリング（必要に応じて）
            // process.destroy() で後から親の都合で殺すことも可能です。

          var context = this.springContextHolder.getContext();
          context.close(); // 1つ目の古いサーバーを完全にシャットダウン（ポートを解放）

          // JVM自体を正常終了コードで完全にストップ
          System.exit(0);

            
        } catch (IOException e) {
            System.err.println("子JVMのフォークに失敗しました: " + e.getMessage());
        }

    }

  

    /**
     * 動的サービス起動システムを使用して、DoHエンドポイントのサービスタスクを起動する.
     * 
     * @throws DNSServiceCommonException
     */
    protected void doDoHEndpointServiceTaskOnDynamicService() throws DNSServiceCommonException
    {

      String serviceName = "DoHEndpointService";
      String mainClass = DoHServiceEndpointInstanceSpringApplication.class.getName();
      String[] args = new String[]{};
      Properties jvmProperties  = new Properties();

      // TODO : とりあえず、デフォルトユーザー名をadminに設定する.
      jvmProperties.put(LoulanDNSConstants.PROP_KEY_DEFAULT_SERVICE_INSTANCE_USER_NAME, "admin");

      // TODO : とりあえず、デフォルトのDNSサービスインスタンス名を"default"に設定する.
      jvmProperties.put(LoulanDNSConstants.PROP_KEY_DEFAULT_SERVICE_INSTANCE_NAME, "default");


      IDynamicServiceLauncher  serviceLauncher = dynamicServiceLauncherFactory.getOrCreateDynamicServiceLauncher();
      IDyanmicServiceDescriptor  servoiceDescriptor = serviceLauncher.createDynamicServiceDiscriptor(serviceName, mainClass, args, jvmProperties );

      servoiceDescriptor.startDynamicService();


    }







    public void setDoHServiceEndpointAddress(String address) throws DNSServiceCommonException
    {
      if ( commonUtils.isValidIPAddressString(address) == false )
      {
        // 指定された文字列はIPアドレス形式として不適.
        String msg = String.format("Specified address is not valid for DoHService Endpoint Address. address=%s", address );
        DNSServiceCommonException exception = new DNSServiceCommonException(msg);
        throw exception;
      }

      this.dohServiceEndpointAddress = address;
    }

    public String getDoHServiceEndpointAddress() throws DNSServiceCommonException
    {
      return this.dohServiceEndpointAddress;
    }

    public void setDoHServiceEndpointPort(int port) throws DNSServiceCommonException
    {
      if (port < 0 || port > 65535 )
      {
        // TCPポート番号として不適.
        String msg = String.format("Specified port number is not valid for DoHService Endpoint Port. address=%d", port );
        DNSServiceCommonException exception = new DNSServiceCommonException(msg);
        throw exception;
      }

      this.dohServiceEndpointPort = port;
    }

    public int getUDPServiceEndpointPort() throws DNSServiceCommonException
    {
      return this.dohServiceEndpointPort;
    }


    public SocketAddress getDoHServiceSocketAddress() throws DNSServiceCommonException
    {
      String address = getDoHServiceEndpointAddress();
      int port = getUDPServiceEndpointPort();

      SocketAddress socketAddress = new InetSocketAddress(address, port);

      return socketAddress;
    }


}