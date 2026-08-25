package org.loxsols.net.service.dns.loulandns.client.impl.simple;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.subway.IDNSSubwayGateway;
import org.loxsols.net.service.dns.loulandns.client.subway.IDNSSubwayGatewayInstancePool;
import org.loxsols.net.service.dns.loulandns.client.subway.IDNSSubwayGatewayInstancePool;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayCommonException;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayConstants;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayUtils;
import org.loxsols.net.service.dns.loulandns.client.subway.common.IDNSSubwayGatewayDescriptor;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.IDNSSubwayGatewayFactory;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.IDNSSubwayGatewayInstancePoolFactory;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.impl.DNSSubwayGatewayInstancePoolFactoryImpl;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.DNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.SimpleDNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAdditionalSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.rr.DNSResourceRecordFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.IDNSAdditionalSectionFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.impl.rr.edns.DNSRROptPseudoRRDataForECSImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSAdditionalSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSHeaderSection;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.IDNSResourceRecord;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSRROptPseudoRRData;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.outbound.UDPResolverInstanceImpl;
import org.loxsols.net.service.dns.loulandns.util.DNSUtils;
import org.loxsols.net.service.dns.loulandns.server.common.*;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSDebugUtils;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSProtocolUtils;
import org.loxsols.net.service.dns.loulandns.client.impl.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.section.rr.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.xbill.DNS.EDNSOption;



/**
 * シンプルなDNSSubwayリゾルバ
 * 
 * SimpleDNSSubwayResolverImpl
 */
public class SimpleDNSSubwayResolverImpl extends DNSLookupClientBaseImpl implements IDNSLookupClient
{

    IDNSResolverInstance subResolverInstance;

    List<String> dnsSubwayDomainList = new ArrayList<String>();
    List<Integer> dnsSubwayPortList = new ArrayList<Integer>();

    IDNSSubwayGatewayInstancePoolFactory dnsSubwayGatewayPoolFactory;

    IDNSSubwayGatewayInstancePool dnsSubwayGatewayPool;

    Properties gatewayProperties;

    DNSSubwayUtils dnsSubwayUtils = new DNSSubwayUtils();



    /**
     * デフォルトの再帰リゾルバを設定する.
     * DNSサブウェイを通過しない問い合わせをこのデフォルトのリゾルバに渡す.
     * 
     * @throws DNSClientCommonException
     */
    public void setSubResolverInstance(IDNSResolverInstance instance) throws DNSClientCommonException
    {
        this.subResolverInstance = instance;
    }

    public IDNSResolverInstance getSubResolverInstance() throws DNSClientCommonException
    {
        return this.subResolverInstance;
    }

    public List<String> getDNSSubwayDomainList() throws DNSClientCommonException
    {
        return this.dnsSubwayDomainList;
    }


    public List<Integer> getDNSSubwayPortList() throws DNSClientCommonException
    {
        return this.dnsSubwayPortList;
    }





    public void setDNSSubwayGatewayInstancePoolFactory(IDNSSubwayGatewayInstancePoolFactory instance) throws DNSClientCommonException
    {
        this.dnsSubwayGatewayPoolFactory = instance;
    }

    public IDNSSubwayGatewayInstancePoolFactory getDNSSubwayGatewayInstancePoolFactory() throws DNSClientCommonException
    {
        return this.dnsSubwayGatewayPoolFactory;
    }


    public IDNSSubwayGatewayInstancePool getDNSSubwayGatewayPool() throws DNSClientCommonException
    {
        return this.dnsSubwayGatewayPool;
    }

    public void setDNSSubwayGatewayPool(IDNSSubwayGatewayInstancePool instance) throws DNSClientCommonException
    {
        this.dnsSubwayGatewayPool = instance;
    }


    public Properties getGatewayProperties() throws DNSClientCommonException
    {
        return this.gatewayProperties;
    }

    public void setGatewayProperties(Properties properties) throws DNSClientCommonException
    {
        this.gatewayProperties = properties;
    }



    public SimpleDNSSubwayResolverImpl() throws DNSServiceCommonException
    {
        super();
    }


    // SpringのDIを使用せずに機能するよう本クラスを初期化する.
    public void init(Properties properties) throws DNSClientCommonException
    {

        String dnsServerAddress = properties.getProperty(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS);
        if ( dnsServerAddress == null || dnsServerAddress.isEmpty() )
        {
            String msg = String.format("Faield to init SimpleDNSSubwayResolverImpl. DNS Server Address is not specified. key=%s", LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;      
        }
        setDNSServerAddress(dnsServerAddress);

        String dnsServerPort = properties.getProperty(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT);
        if ( dnsServerPort == null || dnsServerPort.isEmpty() )
        {
            String msg = String.format("Faield to init SimpleDNSSubwayResolverImpl. DNS Server Port is not specified. key=%s", LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;      
        }
        setDNSServerPort(dnsServerPort);


        // DNS-Subwayの対象ドメインのリストを設定する.
        String dnsSubwayTargetDomain = properties.getProperty(DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_TARGET_DOMAIN);
        if ( dnsSubwayTargetDomain == null || dnsSubwayTargetDomain.isEmpty() )
        {
            String msg = String.format("Faield to init SimpleDNSSubwayResolverImpl. DNS-Subway target domain is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_TARGET_DOMAIN );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;      
        }
        String[] dnsSubwayTargetDomainArray = dnsSubwayTargetDomain.split(",");
        addDNSSubwayDomainList(dnsSubwayTargetDomainArray);

        // DNS-Subwayの対象ポート番号のリストを設定する.
        String dnsSubwayTargetPort = properties.getProperty(DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_TARGET_PORT );
        if ( dnsSubwayTargetPort == null || dnsSubwayTargetPort.isEmpty() )
        {
            String msg = String.format("Faield to init SimpleDNSSubwayResolverImpl. DNS-Subway target port is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_TARGET_PORT );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;      
        }
        int[] dnsSubwayTargetPortArray = dnsSubwayUtils.getPortListFromString(dnsSubwayTargetPort);
        addDNSSubwayPortList(dnsSubwayTargetPortArray);



        // Gatewayプロパティを設定する.
        // リゾルバに与えられたプロパティの中から、"loulan.dns.subway"から始まるプロパティキーと値をはDNS-SubwayのGatewayプロパティとして設定する.
        Properties gwProperties = new Properties();
        for( Object keyObj : properties.keySet())
        {
            String key = (String)keyObj;

            // "loulan.dns.subway"から始まるプロパティキーはDNS-SubwayのGatewayプロパティとして設定する.
            if ( key.startsWith("loulan.dns.subway"))
            {
                String value = properties.getProperty(key);
                gwProperties.setProperty(key, value);
            }            
        }
        setGatewayProperties(gwProperties);



        // SpringのDIを使用せずに機能するよう本クラスを初期化する.
        try
        {
            setDNSMessageFacotry( new SimpleDNSMessageFactoryImpl() );
            setDNSMessageTransporter( new SimpleUDPMessageTransporterImpl() );

            setDNSResourceRecordFactory( new DNSResourceRecordFactoryImpl() );
            setDNSAdditionalSectionFactory( new DNSAdditionalSectionFactoryImpl() );

            IDNSSubwayGatewayInstancePoolFactory gwPoolFactory = new DNSSubwayGatewayInstancePoolFactoryImpl();
            setDNSSubwayGatewayInstancePoolFactory( gwPoolFactory );


            IDNSSubwayGatewayInstancePool  gwInstancePool = gwPoolFactory.createDNSSubwayGatewayInstancePool(properties);
            setDNSSubwayGatewayPool(gwInstancePool);


            // とりあえず、UDPリゾルバをデフォルトゲートのDNSリゾルバに設定する.
            Properties resolverProperties = new Properties();
            resolverProperties.setProperty(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_HOST_PRIMARY, getDNSServerAddress().getHostAddress()  );
            resolverProperties.setProperty(LoulanDNSConstants.PROP_KEY_RESOLVER_OUTBOUND_SERVER_PORT_PRIMARY, getDNSServerPortString()  );
            IDNSResolverInstance resolverInstance = new UDPResolverInstanceImpl(resolverProperties);
            setSubResolverInstance(resolverInstance);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Faield to init SimpleDNSSubwayResolverImpl." );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        // 基底クラスのinitを呼ぶ.
        super.init(properties);


    }







    // ---- 以下、本クラス独自の実装メソッド -----------------------
    public boolean isDNSSubwayDomain(String dname) throws DNSClientCommonException
    {
        List<String> list = getDNSSubwayDomainList();

        boolean ret = dnsSubwayUtils.isDNSSubwayTargetDomain(list, dname);

        return ret;
    }



    /**
     * DNS-Subwayの対象ドメインを追加する.
     * 
     */
    public void addDNSSubwayDomain(String dname) throws DNSClientCommonException
    {

        List<String> list = getDNSSubwayDomainList();

        System.out.println( String.format("[DEBUG] SimpleDNSSubwayResolverImpl.addDNSSubwayDomain() : dname=%s, list.size()=%d", dname, list.size() ) );

        if ( dname.endsWith(".") )
        {
            // 指定されたドメイン名がFQDN形式で末尾がドット記号(.)で終端する場合はそれを取り除く.
            dname = dname.substring(0, dname.length() - 1 );
        }

        if ( isDNSSubwayDomain(dname) == true )
        {
            // 既に指定されたdnameはDNS-Subwayの対象ドメインとして登録済み.
            String msg = String.format("Faield to add domain to DNSSubway list. Specified dname is already registered. dname=%s", dname );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

    
        list.add( dname );

    }


    /**
     * DNS-Subwayの対象ドメインのリストを追加する.
     * 
     */
    public void addDNSSubwayDomainList(String[] dnameArray) throws DNSClientCommonException
    {
        for( String dname : dnameArray )
        {
            addDNSSubwayDomain(dname);
        }

    }


    public void removeDNSSubwayDomain(String dname) throws DNSClientCommonException
    {
        if ( isDNSSubwayDomain(dname) == false )
        {
            String msg = String.format("Faield to remove domain to DNSSubway list. Specified dname is NOT registered. dname=%s", dname );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        this.getDNSSubwayDomainList().remove( dname );
    }


    /**
     * DNS-Subwayの対象ポートを追加する.
     * 
     */
    public void addDNSSubwayPort(Integer port) throws DNSClientCommonException
    {

        List<Integer> list = getDNSSubwayPortList();

        if ( port < 0 || port > 65535 )
        {
            // ポート番号の範囲として不正.
            String msg = String.format("Faield to add port to DNSSubway list. Invalid port value. port=%d", port );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        if ( list.contains(port) )
        {
            // 既に指定されたポートはDNS-Subwayの対象ドメインとして登録済み.
            String msg = String.format("Faield to add port to DNSSubway list. Specified port is already registered. port=%s", port );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        list.add( port );

    }


    /**
     * DNS-Subwayの対象ポートのリストを追加する.
     * 
     */
    public void addDNSSubwayPortList(int[] portArray) throws DNSClientCommonException
    {
        for( int port : portArray )
        {
            addDNSSubwayPort(port);
        }
    }


    public void removeDNSSubwayPort(Integer port) throws DNSClientCommonException
    {
        List<Integer> list = getDNSSubwayPortList();
        list.remove(port);
    }


    /**
     * DNSSubwayの名前解決処理.
     * DNSSubwayのGWを動的に構築し、かつそのGWの入り口をDNSレスポンスとして返す.
     * 
     * @param questionMessage
     * @return
     * @throws DNSClientCommonException
     */
    public IDNSResponseMessage resolveDNSSubwayGateway(IDNSQuestionMessage questionMessage) throws DNSClientCommonException
    {

        System.out.println( String.format("[DEBUG] SimpleDNSSubwayResolverImpl.resolveDNSSubwayGateway() called.") );

        String dname;
        try
        {
            dname = questionMessage.getDNSQuestionSection().getDNSQueries()[0].getDNSQueryName();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to resolve DNSSubway GW domain. Unable to get dname.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        IDNSSubwayGatewayInstancePool gwPool = getDNSSubwayGatewayPool();
        if( gwPool == null )
        {
            String msg = String.format("Failed to resolve DNSSubway GW domain. DNSSubwayGatewayInstancePool is not initialized.");
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;
        }

        // 本クラスでは、GWのタイプはSSHトンネル固定とする.
        String subwayLineType = DNSSubwayConstants.COSNT_DNS_SUBWAY_LINE_TYPE_SSH_TUNNEL;

        // プロトコルタイプは、TCP固定とする.
        String protocolType = DNSSubwayConstants.COSNT_DNS_SUBWAY_PROTOCOL_TYPE_TCP;


        // GWオプションは本リゾルバに与えられたプロパティの一部を継承して作成する.
        Properties gwOptions = (Properties)this.getGatewayProperties().clone();

        // ----------------------------------------------------------------
        // 以下では、GWオプションのうち、SSHTunnel-Gateway固有の設定を行う.

        // --- ターゲットホストのドメイン名(単独)の設定
        // 例えば、ターゲットホストが予め"google.co.jp,yahoo.co.jp"のように複数件指定されている場合に、実際にアクセスするサイトに応じてGWのオプションを固定する.
        String targetHost = dname;
        gwOptions.setProperty(DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_HOST, dname);


        // --- ターゲットホストのポート番号(複数件)の設定
        // ターゲットのポート番号は"80,443"のように","区切りでリスト指定する.
        List<Integer> targetPortList = this.getDNSSubwayPortList();
        String targetPortValue = dnsSubwayUtils.getPortListString( targetPortList );
        gwOptions.setProperty(DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_TARGET_PORT, targetPortValue );
        // ----------------------------------------------------------------


        int[] portList = dnsSubwayUtils.getPortArrayFromPortList(targetPortList);
        IDNSSubwayGatewayDescriptor  gwDescriptor = gwPool.getOrCreateDNSSubwayDescriptor(subwayLineType, dname, protocolType, portList, gwOptions );
        if ( gwDescriptor == null )
        {
            String msg = String.format("Failed to resolve DNSSubway GW domain. Unable to build Gateway Descriptor. subwayLineType=%s, dname=%s, protocolType=%s, port=%d", subwayLineType, dname, protocolType, dnsSubwayUtils.getPortListString(portList) );
            DNSClientCommonException exception = new DNSClientCommonException(msg);
            throw exception;   
        }


        String gwAddress = gwDescriptor.getGatewayAddress();
        int[] gwPortList = gwDescriptor.gatGatewayPortList();

        
        // とりあえず、GWのIPアドレスを含むAレコードのみからなるレスポンスを生成して返す.
        IDNSQueryPart query;
        String qname;
        int qtype;
        int qclass;
        try
        {
            query = questionMessage.getDNSQuestionSection().getDNSQueries()[0];
            qname = query.getDNSQueryName();
            qtype = query.getDNSQueryType();
            qclass = query.getDNSQueryClass();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to create DNS Gateway response, caused by Unable to get query information.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }

        int rTTL = 3600;    // TTLはとりえあず3600秒に設定する.
        byte[] rdata;
        try
        {
            rdata = InetAddress.getByName(gwAddress).getAddress();
        }
        catch(UnknownHostException cause)
        {
            String msg = String.format("Failed to create DNS Gateway rdata.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        System.out.println(String.format("[DEBUG] SimpleDNSSubwayResolverImpl.resolveDNSSubwayGateway() rdata.length=%d", rdata.length ) );

        IDNSMessageFactory dnsMessageFactory = getDNSMessageFactory();
        IDNSResponseMessage response;
        try
        {
            response = dnsMessageFactory.createSimpleResponseDNSMesssage(qname, qtype, qclass, rTTL, rdata);
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response. qname=%s, qtype=%d, qclass=%d, rTTL=%d, gwAddress=%s, gwPort=%s", qname, qtype, qclass, rTTL, gwAddress, dnsSubwayUtils.getPortListString(gwPortList)  );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        // DNSクエリIDをレスポンスメッセージに設定する.
        try
        {
            int dnsQueryID = questionMessage.getDNSHeaderSection().getID();
            response.getDNSHeaderSection().setID( dnsQueryID );
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response, caued by setting to DNS Query ID.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }  


        // ------
        // 2026/08/21 : ヘッダーセクションの各値がレスポンスに適していない不具合対応.
        try
        {
            IDNSHeaderSection headerSection = response.getDNSHeaderSection();

            // QRビットをオンにして「レスポンス」にする
            headerSection.setQR(1);

            // RAビットをオンにして「再帰応答可能」にする
            headerSection.setRA(1);

            // RCODEを 0 (NOERROR) に設定
            headerSection.setRCode(0);

            // 再度DNSレスポンスメッセージのヘッダセクションをsetする.
            response.setDNSHeaderSection(headerSection);

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response, caued by setting to DNS Response header values.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }  
        // ------


        //-----------------------------------------------------
        // 以下デバッグ用
        //-----------------------------------------------------
        
        try
        {
            String headerDebugString = String.format("QR=%b, AA=%b, RA=%b,RCODE=%d",response.getDNSHeaderSection().getBooleanQR(), response.getDNSHeaderSection().getBooleanAA(), response.getDNSHeaderSection().getBooleanRA(), response.getDNSHeaderSection().getRCode() );

            LoulanDNSDebugUtils.printHexString(getClass(), "DNS-Subway DNS Response message. hex=", response.getDNSMessageBytes() );
            LoulanDNSDebugUtils.printDebug(getClass(),"DNS-Subway DNS Response message. header=", headerDebugString );

            System.out.println( String.format("[DEBUG] SimpleDNSSubwayResolverImpl.resolveDNSSubwayGateway() gwAddress=%s, rdata.length=%d, response=%s, headerDebugString=%s", gwAddress, rdata.length, response, headerDebugString) );

        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response, caued by DEBUG.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        //-----------------------------------------------------


        try
        {
            response.validate();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to craete DNS Subway Gateway response, caued by validation.");
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        return response;

    }






    // ---- 以下、IDNSLookupClient I/Fのオーバーライド ------------------------


    /**
     * DNS問い合わせを実行する.
     * 予め指定された、ドメイン名の問い合わせの場合は、DNSSubwayのGWを動的に構築し、そのGWの入り口を返す.
     * 
     */
    public IDNSResponseMessage resolve(IDNSQuestionMessage questionMessage) throws DNSClientCommonException
    {

        IDNSResponseMessage responseMessage;

        String dname;
        
        try
        {
            dname = questionMessage.getDNSQuestionSection().getDNSQueries()[0].getDNSQueryName();
        }
        catch(DNSServiceCommonException cause)
        {
            String msg = String.format("Failed to get domain name from DNS Question Message. questionMessage=%s", questionMessage );
            DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
            throw exception;
        }


        System.out.println( String.format("[DEBUG] SimpleDNSSubwayResolverImpl.resolve() dname=%s, isDNSSubwayDomain(dname)=%s", dname, isDNSSubwayDomain(dname) ) );


        if ( isDNSSubwayDomain(dname) )
        {
            responseMessage = resolveDNSSubwayGateway(questionMessage);
        }
        else
        {
            responseMessage = super.resolve(questionMessage);
        }

        return responseMessage;
    }


    /**
     * デバッグ用のmain関数
     * Usage : <SSH server host>　<SSH server port> [local bind addresss scope <Ex. 127.0.0.0/24>]"
     * 
     */
    public static void main(String[] args)
    {

        if ( args.length < 3 )
        {
            System.out.println("Usage : <dname> <SSH server host> <SSH server port> [local bind addresss scope <Ex. 127.0.0.0/24>]");
            return ;
        }

        String dname = args[0];
        String sshServerHost = args[1];
        String sshServerPort = args[2];


        Properties properties = new Properties();

        // SSHトンネルの接続先のSSHサーバーの情報を設定する.
        properties.setProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_HOST, sshServerHost );
        properties.setProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_GW_SSH_TUNNEL_SERVER_PORT, sshServerPort );


        // 再帰問い合わせをするDNSサーバーのアドレス情報を設定する.
        // とりあえず、Cloudflareに設定する.
        properties.setProperty(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS, "1.1.1.1");
        properties.setProperty(LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT, "53");


        try
        {
            SimpleDNSSubwayResolverImpl resolverImpl = new SimpleDNSSubwayResolverImpl();            
            resolverImpl.init(properties);

            IDNSResponseMessage response = resolverImpl.resolve(dname, 1, 1);

            System.out.println(response.toString());

        }
        catch(DNSServiceCommonException exception)
        {
            exception.printStackTrace();
        }

    }


}