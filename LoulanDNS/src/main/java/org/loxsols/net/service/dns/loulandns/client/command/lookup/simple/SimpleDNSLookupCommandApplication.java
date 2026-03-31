package org.loxsols.net.service.dns.loulandns.client.command.lookup.simple;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;


import org.loxsols.net.service.dns.loulandns.client.impl.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.http.spring.common.security.provider.LoulanDNSAuthenticationProvider;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSDBService;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.DNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAnswerSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSHeaderSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSQuestionSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.model.DNSProtocolModelInstanceFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSAnswerSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSHeaderSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSQuestionSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.IDNSProtocolModelInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSQuestionSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.loxsols.net.service.dns.loulandns.util.DNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.*;
import org.loxsols.net.service.dns.loulandns.client.common.*;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.command.lookup.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties.Simple;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.xbill.DNS.ZoneTransferException;

import com.android.internal.org.bouncycastle.util.Arrays;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


// シンプルなdigコマンドのアプリケーション. Springの機能を使用しない.
public class SimpleDNSLookupCommandApplication
{

    String defaultServerAddress;
    int defaultServerPort;


    IDNSLookupClient dnsLookupClient;

    LoulanDNSProtocolUtils loulanDNSProtocolUtils = new LoulanDNSProtocolUtils();

    DNSUtils dnsUtils = new DNSUtils();


    public SimpleDNSLookupCommandApplication() throws DNSClientCommonException
    {

        List<InetSocketAddress>  serverList;
        
        try
        {
            serverList = dnsUtils.getSystemDNSServerAddressList();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to get System DNS Server informations.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        if ( serverList == null || serverList.size() == 0 )
        {
            String msg = String.format("System DNS Server information is not found.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        InetSocketAddress serverSockAddress = serverList.get(0);

        String ipAddressString;
        try
        {
            ipAddressString = dnsUtils.toIPAddressString(serverSockAddress.getAddress());
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to convert DNS server address(%s) to IPAddress String.", serverSockAddress.getAddress() );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        setDefaultServerAddress(ipAddressString);
        setDefaultServerPort(serverSockAddress.getPort());

        init();
    }

    public SimpleDNSLookupCommandApplication(String serverAddress, int serverPort) throws DNSClientCommonException
    {
        setDefaultServerAddress(serverAddress);
        setDefaultServerPort(serverPort);

        init();
    }


    // デフォルトで参照するDNSサーバーアドレスを返す.
    public String getDefaultServerAddress() throws DNSClientCommonException
    {
        return this.defaultServerAddress;
    }

    // デフォルトで参照するDNSサーバーアドレスを設定する.
    public void setDefaultServerAddress(String serverAddress) throws DNSClientCommonException
    {
        this.defaultServerAddress = serverAddress;
    }


    // デフォルトで参照するDNSサーバーポートを返す.
    public int getDefaultServerPort() throws DNSClientCommonException
    {
        return this.defaultServerPort;
    }

    // デフォルトで参照するDNSサーバーポートを設定する.
    public void setDefaultServerPort(int serverPort) throws DNSClientCommonException
    {
        this.defaultServerPort = serverPort;
    }


    public void init() throws DNSClientCommonException
    {

        IDNSLookupClient simpleClient;
        
        try
        {
            simpleClient = new SimpleUDPResolverImpl();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to build IDNSLookupClient object : SimpleUDPResolverImpl");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        setDNSLookupClient( simpleClient );

    }


    // コマンド引数を解析する.
    // dig [@server] [-b address] [-c class] [-f filename] [-k filename] [-m] [-p port#] [-q name] [-t type] [-x addr] [-y [hmac:]name:key] [-4] [-6] [name] [type] [class] [queryopt...]
    public HashMap<String, String> parseCommandArguments(String[] args) throws DNSClientCommonException
    {
        DNSLookupCommandLineParser commandLineParser = new DNSLookupCommandLineParser();
        commandLineParser.parse(args);
        
        HashMap<String, String> properties = commandLineParser.getProperties();
        return properties;
    }

    public String getServerAddress(HashMap<String, String> commandProperties)
    {
        String value = commandProperties.get( LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS );
        return value;
    }

    public String getServerPort(HashMap<String, String> commandProperties)
    {
        String value = commandProperties.get( LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT );
        return value;
    }

    public String getQName(HashMap<String, String> commandProperties)
    {
        String value = commandProperties.get( LoulanDNSClientConstants.PROP_KEY_DNS_DOMAIN_NAME );
        return value;
    }

    public String getQClass(HashMap<String, String> commandProperties)
    {
        String value = commandProperties.get( LoulanDNSClientConstants.PROP_KEY_DNS_DOMAIN_CLASS );

        if ( value == null )
        {
            value = Integer.toString( LoulanDNSClientConstants.UDP_CLIENT_DEFAULT_QCLASS ) ;
        }

        return value;
    }

    public String getQType(HashMap<String, String> commandProperties)
    {
        String value = commandProperties.get( LoulanDNSClientConstants.PROP_KEY_DNS_DOMAIN_RR_TYPE );

        if ( value == null )
        {
            value = Integer.toString( LoulanDNSClientConstants.UDP_CLIENT_DEFAULT_QTYPE ) ;
        }

        return value;
    }


    // EDNSの有効/無効を取得する.
    public Boolean getIsEnableEDNS(HashMap<String, String> commandProperties)
    {
        String value = commandProperties.get( LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ENABLE );
        if ( value == null )
        {
            return null;
        }

        boolean ret = Boolean.parseBoolean(value);
        return ret;
    }

    // EDNS Client Subnetの有効/無効を取得する.
    public Boolean getIsEnableEDNSClientSubnet(HashMap<String, String> commandProperties)
    {
        String address = getEDNSClientSubnetAddress( commandProperties );
        if ( address == null )
        {
            return false;
        }

        return true;
    }
    
    // EDNS Client Subnetのアドレス(文字列)を取得する.
    public String getEDNSClientSubnetAddress(HashMap<String, String>  commandProperties )
    {
        String value = commandProperties.get( LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ECS_ADDRESS );
        if ( value == null )
        {
            return null;
        }

        String ret = value;
        return ret;
    }

    // EDNS Client Subnetのプリフィックスを取得する.
    public Integer getEDNSClientSubnetPrefix(HashMap<String, String>  commandProperties )
    {
        String value = commandProperties.get( LoulanDNSClientConstants.PROP_KEY_DNS_EDNS_ECS_PREFIX_BITS );
        if ( value == null )
        {
            return null;
        }

        int ret = Integer.parseInt( value );
        return ret;
    }



    // 設定されたコマンドパラメータをチェックする.
    private void checkCommmandParameters(HashMap<String, String> commandProperties) throws DNSClientCommonException
    {
        if ( commandProperties == null )
        {
            String msg = String.format("Invalid command parameters : parameter properties is null.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }


        // DNSサーバーのアドレスが明示的に指定されている場合は、アドレス形式のチェックを行う.
        String addressString = getServerAddress(commandProperties);
        if ( addressString != null )
        {
            try
            {
                InetAddress address = InetAddress.getByName(addressString);
            }
            catch( UnknownHostException cause )
            {
                String msg = String.format("Invalid command parameters : Invalid DNS server address. address=%s", addressString );
                DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
                throw exception;
            }
        }


        // DNSサーバーのポート番号が明示的に指定されている場合は、ポート番号のチェックを行う.
        String serverPortString = getServerPort(commandProperties);
        if ( serverPortString != null )
        {
            try
            {
                int port = Integer.parseInt( serverPortString );
            }
            catch( NumberFormatException cause )
            {
                String msg = String.format("Invalid command parameters : Invalid DNS server port. port=%s", serverPortString );
                DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
                throw exception;
            }
        }


        if ( getQName(commandProperties) == null )
        {
            String msg = String.format("Invalid command parameters : QName is null.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }



    }


    public void setDNSLookupClient(IDNSLookupClient instance)
    {
        this.dnsLookupClient = instance;
    }


    public IDNSLookupClient getDNSLookupClient()
    {
        return this.dnsLookupClient;
    }

    // DNSレスポンスメッセージをdigコマンドの出力風メッセージに変換する.
    public String toDigResultString(SimpleDNSLookupCommandJobStatus jobStatus) throws DNSClientCommonException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);

        try
        {
        /*
        // digコマンドの実行例
        // Windows版BIND-9.16.43付属digコマンドより
        // 正常系) 
        >dig @1.1.1.1 -p 53 www.google.co.jp

        ; <<>> DiG 9.16.43 <<>> @1.1.1.1 -p 53 www.google.co.jp
        ; (1 server found)
        ;; global options: +cmd
        ;; Got answer:
        ;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 61328
        ;; flags: qr rd ra; QUERY: 1, ANSWER: 1, AUTHORITY: 0, ADDITIONAL: 1

        ;; OPT PSEUDOSECTION:
        ; EDNS: version: 0, flags:; udp: 1232
        ;; QUESTION SECTION:
        ;www.google.co.jp.              IN      A

        ;; ANSWER SECTION:
        www.google.co.jp.       148     IN      A       142.250.207.99

        ;; Query time: 2435 msec
        ;; SERVER: 1.1.1.1#53(1.1.1.1)
        ;; WHEN: Thu Jun 19 12:54:06 ;; MSG SIZE  rcvd: 61
        */


        // ------ 定型的な文言出力.
        // ; <<>> DiG 9.16.43 <<>> @1.1.1.1 -p 53 www.google.co.jp
        ps.println( String.format("; <<>> DiG %s <<>> %s", jobStatus.getCommandVersion(), jobStatus.getCommandLine()));

        // ; (1 server found)
        ps.println( String.format("; (%d server found)", 1));

        // ;; global options: +cmd
        ps.println( String.format(";; global options: %s", jobStatus.getGlobalOptions()));

        // ;; Got answer:
        ps.println( String.format(";; Got answer:" ));


        IDNSResponseMessage message = jobStatus.getDNSResponseMessage();

        // ----- Headerセクションの出力
        if ( message.getDNSHeaderSection() != null )
        {
            // ;; ->>HEADER<<- opcode: QUERY, status: NOERROR, id: 61328
            String opcodeString = message.getDNSHeaderSection().getOPCodeString();
            String rcodeString = message.getDNSHeaderSection().getRCodeString();
            int id = (int)message.getDNSHeaderSection().getID();
            ps.println( String.format(";; ->>HEADER<<- opcode: %s, status: %s, id: %d", opcodeString, rcodeString, id));

            // ;; flags: qr rd ra; QUERY: 1, ANSWER: 1, AUTHORITY: 0, ADDITIONAL: 1
            boolean qr = message.getDNSHeaderSection().getBooleanQR();
            boolean aa = message.getDNSHeaderSection().getBooleanAA();
            boolean tc = message.getDNSHeaderSection().getBooleanTC();
            boolean rd = message.getDNSHeaderSection().getBooleanRD();
            boolean ra = message.getDNSHeaderSection().getBooleanRA();
            String flagsString = String.format("%s %s %s %s %s", 
                                                (qr ? "QR" : ""),
                                                (aa ? "AA" : ""),
                                                (tc ? "TC" : ""),
                                                (rd ? "RD" : ""),
                                                (ra ? "RA" : "") );
            ps.println( String.format(";; flags: %s; QUERY: %d, ANSWER: %d, AUTHORITY: %d, ADDITIONAL: %d", 
                                                flagsString,
                                                message.getDNSHeaderSection().getQDCOUNT(),
                                                message.getDNSHeaderSection().getANCOUNT(),
                                                message.getDNSHeaderSection().getNSCOUNT(),
                                                message.getDNSHeaderSection().getARCOUNT()));

            if  (message.isEDNS0Message() == true )
            {
                // ;; OPT PSEUDOSECTION:
                ps.println( String.format(";; OPT PSEUDOSECTION:"));
            
                // ; EDNS: version: 0, flags:; udp: 1232
                int ednsVersion = message.getEDNS0OPTResourceRecord().getEDNS0Version();
                int ednsUDPPayloadSize = message.getEDNS0OPTResourceRecord().getEDNS0UDPPayloadSize();
                ps.println( String.format(" EDNS: version: %d, flags:; udp: %d", ednsVersion, ednsUDPPayloadSize));
            }

        }

        // ----- Questionセクションの出力
        if ( message.getDNSQuestionSection() != null )
        {
            //  ;; QUESTION SECTION:
            ps.println( String.format(";; QUESTION SECTION:"));
            
            //  ;www.google.co.jp.              IN      A
            for( IDNSQueryPart query : message.getDNSQuestionSection().getDNSQueries() )
            {
                String dnsClassString = loulanDNSProtocolUtils.toDNSClassValueString(query.getDNSQueryClass() );

                String dnsRRTypeString;
                if ( loulanDNSProtocolUtils.isValidRangeDNSRRTypeValue( query.getDNSQueryType() ) )
                {
                    dnsRRTypeString = loulanDNSProtocolUtils.toDNSRRTypeString( query.getDNSQueryType() );
                }
                else
                {
                    dnsRRTypeString = "Invalid DNS RR Type : " + query.getDNSQueryType();
                }

                ps.println( String.format(";%s              %s      %s", query.getDNSQueryName(), dnsClassString, dnsRRTypeString));
            }

        }

        //Answerセクションの文字列化
        if ( message != null && message.getDNSAnswerSection() != null )
        {
            // ;; ANSWER SECTION:
             ps.println( String.format(";; ANSWER SECTION:"));

            //  www.google.co.jp.       148     IN      A       142.250.207.99
            for( IDNSResourceRecord rr : message.getDNSAnswerSection().getDNSResourceRecords() )
            {
                String dnsRName = rr.getDNSResourceName();

                String dnsRTTL = String.format("%d", rr.getResourceTTL() );

                String dnsRClassString = loulanDNSProtocolUtils.toDNSClassValueString( rr.getResourceClass() );

                String dnsRTypeString;
                if ( loulanDNSProtocolUtils.isValidRangeDNSRRTypeValue( rr.getResourceType() ) )
                {
                    dnsRTypeString = loulanDNSProtocolUtils.toDNSRRTypeString( rr.getResourceType() );
                }
                else
                {
                    dnsRTypeString = "Invalid DNS RR Type : " + rr.getResourceType();
                }

                String dnsRDataString = rr.getResourceRDataString();

                ps.println( String.format("%s       %s     %s      %s       %s", dnsRName, dnsRTTL, dnsRClassString, dnsRTypeString, dnsRDataString));
            }

        }


        // ;; Query time: 2435 msec
        long queryTimeMillSec = jobStatus.getQueryEndDate().getTime() - jobStatus.getQueryStartDate().getTime();
        ps.println( String.format(";; Query time: %s msec", queryTimeMillSec));

        // ;; SERVER: 1.1.1.1#53(1.1.1.1)
        String serverIP = jobStatus.getServerIPAddressString();
        int serverPort = jobStatus.getServerPort();
        String serverHostName = jobStatus.getServerHostString();
        ps.println( String.format(";; SERVER: %s#%d(%s)", serverIP, serverPort, serverHostName));

        // ;; WHEN: Thu Jun 19 12:54:06 ;; MSG SIZE  rcvd: 61
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss");
        String dateTimeString = dateTimeFormat.format( jobStatus.getQueryStartDate() );
        int sizeOfResponseMessage = message.getDNSMessageBytes().length;
        ps.println( String.format(";; WHEN: %s ;; MSG SIZE  rcvd: %d", dateTimeString, sizeOfResponseMessage) );



        // ---- 以下は異常系 -----
        // TODO 
        // ------ 異常系) タイムアウト
        // TODO
        // ; <<>> DiG 9.16.43 <<>> @2.1.1.3 -p 53 www.google.co.jp
        // ; (1 server found)
        // ;; global options: +cmd
        // ;; connection timed out; no servers could be reached

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to build dig command result text.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }



        // PrintStreamに出力したデータを文字列として回収する.
        ps.flush();
        String outputText = os.toString();

        return outputText;
    }



    // dig [@server] [-b address] [-c class] [-f filename] [-k filename] [-m] [-p port#] [-q name] [-t type] [-x addr] [-y [hmac:]name:key] [-4] [-6] [name] [type] [class] [queryopt...]
    public void exec(String[] args) throws DNSClientCommonException
    {
        InetAddress serverAddress;
        try
        {
            serverAddress = InetAddress.getByName( getDefaultServerAddress() );
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to build InetAddress Object from %s.",  getDefaultServerAddress() );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }
        
        int serverPort = getDefaultServerPort();


        SimpleDNSLookupCommandJobStatus jobStatus = doDNSLookupJob( serverAddress, serverPort, args);


        String outputText = toDigResultString( jobStatus );

        // コマンドの実行結果を画面に出力する.
        System.out.println( outputText );

    }


    // DNSLookupジョブを実行して結果をステータスクラスに格納して返す.
    public SimpleDNSLookupCommandJobStatus doDNSLookupJob(InetAddress dafaultServerAddress, int dafaultServerPort, String[] args) throws DNSClientCommonException
    {

        // コマンドのプロパティ
        HashMap<String, String> commandProperties;

        commandProperties = parseCommandArguments(args);

        checkCommmandParameters(commandProperties);


        // コマンドの引数でサーバーアドレスが明示的に指定されている場合は、引数で指定したアドレスを優先する.
        InetAddress serverAddress = dafaultServerAddress;
        if ( getServerAddress(commandProperties) != null )
        {
            String serverAddressValue = getServerAddress(commandProperties);
            try
            {
                serverAddress = InetAddress.getByName(serverAddressValue);
            }
            catch(UnknownHostException cause)
            {
                String msg = String.format("Faield to build server address object from String value : %s.", serverAddressValue);
                DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
                throw exception;
            }
        }

        // コマンドの引数でサーバーポート番号が明示的に指定されている場合は、引数で指定したポート番号を優先する.
        int serverPort = dafaultServerPort;
        if ( getServerPort(commandProperties) != null )
        {
            String serverPortValue = getServerPort(commandProperties);
            serverPort = Integer.parseInt(serverPortValue);
        }

        String qname = getQName(commandProperties);

        String qtypeValue = getQType(commandProperties);
        int qtype = Integer.parseInt(qtypeValue);

        String qclassValue = getQClass(commandProperties);
        int qclass = Integer.parseInt(qclassValue);


        IDNSLookupClient client = getDNSLookupClient();

        try
        {
            ((SimpleUDPResolverImpl)client).setUDPServerAddressInfo(serverAddress, serverPort);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Faield to set server address and port to DNSLookupClient object. serverAddress=%s, serverPort=%d", serverAddress.toString(), serverPort);
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        // EDNS(0)を使用するかを設定.
        Boolean isEnableEDNS = getIsEnableEDNS(commandProperties);
        if ( isEnableEDNS != null )
        {
            client.setEnableEDNS(isEnableEDNS);
        }

        // EDNS Clinet Subnet (ECS)の関係項目を設定.
        Boolean isEnableEDNSClientSubnet = getIsEnableEDNSClientSubnet(commandProperties);
        String ecsAddress = getEDNSClientSubnetAddress( commandProperties );
        Integer ecsPrefix = getEDNSClientSubnetPrefix( commandProperties );
        if ( isEnableEDNSClientSubnet != null )
        {
            client.setEnableEDNS(isEnableEDNSClientSubnet);
            if ( isEnableEDNSClientSubnet == true )
            {
                client.setEDNSClientSubnetAddress(ecsAddress, ecsPrefix);
            }
        }



        Date queryStartDate = new Date();

        // DNSサーバーへの問い合わせを実行する.
        IDNSMessage responseMessage = client.resolve(qname, qtype, qclass);

        Date queryEndDate = new Date();

        long queryTimeMillSec = queryEndDate.getTime() - queryStartDate.getTime();

        String commandVersion = "0.0.1";

        SimpleDNSLookupCommandJobStatus jobStatus = 
            new SimpleDNSLookupCommandJobStatus(serverAddress, serverPort, args, commandProperties, (IDNSResponseMessage)responseMessage, queryStartDate, queryEndDate);

        return jobStatus;
    }




    public static void main(String[] args)
    {
        try
        {
            SimpleDNSLookupCommandApplication application = new SimpleDNSLookupCommandApplication();
            application.exec(args);
        }
        catch(DNSClientCommonException cause)
        {
            cause.printStackTrace();
        }
    }


}