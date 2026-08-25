
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.outbound;


import java.net.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;


import org.springframework.beans.factory.annotation.Qualifier;

import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleDNSSubwayResolverImpl;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.SimpleUDPResolverImpl;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayConstants;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.IDNSMessageFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.IDNSResolverInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.*;

import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;

/**
 * DNS-SubwayのSSHトンネルGWリゾルバインスタンスの実装クラス.
 */
public class DNSSubwaySSHTunnelGWResolverInstanceImpl extends UDPResolverInstanceImpl implements IDNSResolverInstance
{

    IDNSMessageFactory dnsMessageFactory;
    IDNSMessageTransporter messageTransporter;
    IDNSResolverInstanceFactory resolverInstanceFactory;

    // -----------------------------------------------
    // 以下はsetter/getter関数の実装
    // -----------------------------------------------
    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFactory(IDNSMessageFactory instance)
    {
        this.dnsMessageFactory = instance;
    }

    public IDNSMessageFactory getDNSMessageFactory()
    {
        return this.dnsMessageFactory;
    }


    @Autowired
    @Qualifier("dnsResolverInstanceFactoryImpl")
    public void setDNSResolverInstanceFactory(IDNSResolverInstanceFactory instance)
    {
        this.resolverInstanceFactory = instance;
    }

    public IDNSResolverInstanceFactory getDNSResolverInstanceFactory()
    {
        return this.resolverInstanceFactory;
    }




    /**
     * コンストラクタ
     * 
     */
    public DNSSubwaySSHTunnelGWResolverInstanceImpl(Properties properties, IDNSResolverInstanceFactory resolverInstanceFactory) throws DNSServiceCommonException
    {

        super(properties);

        // 2026/08/17 メモ.
        // IDNSResolverInstanceFactoryインスタンスはコンストラクタ引数で必ず指定すること.
        // initメソッド内で、下位モジュールの初期化処理に使用するから、コンストラクタの定義を変えてでもここで指定する必要がある.
        this.setDNSResolverInstanceFactory(resolverInstanceFactory);

        init(properties);

        // DNS-Subwayリゾルバを初期化する.
        initDNSSubwayResolver(properties);
    }



    /**
     * 初期化メソッド.
     * 
     */
    public void init(Properties properties) throws DNSServiceCommonException
    {
        super.init(properties);
    }


    /**
     * DNSSubwayのリゾルバ(IDNSLookupClientオブジェクト)を初期化する.
     * ※ initメソッドから外出ししておかないと、スーパークラスのinitメソッド呼び出しの過程でおかしくなる.
     * 
     * @param properties
     * @throws DNSServiceCommonException
     */
    protected void initDNSSubwayResolver(Properties properties) throws DNSServiceCommonException
    {
        // DNSリゾルバクライアントのオブジェクトを設定.
        // IDNSLookupClient client = new SimpleDNSSubwayResolverImpl();
        IDNSLookupClient client = new DNSSubwayResolverImpl();

        // 各種DIのインスタンスを設定する.
        IDNSMessageFactory messageFactory = getDNSMessageFactory();
        ( (DNSSubwayResolverImpl)client ).setDNSMessageFacotry(messageFactory);

        IDNSResolverInstanceFactory resolverInstanceFactory = getDNSResolverInstanceFactory();
        if ( resolverInstanceFactory == null )
        {
            String msg = String.format("Failed to init DNSSubwaySSHTunnelGWResolverInstanceImpl. IDNSResolverInstanceFactory instance is not set.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        ( (DNSSubwayResolverImpl)client ).setDNSResolverInstanceFactory(resolverInstanceFactory);

        client.init(properties);

        for( var domain : ( (DNSSubwayResolverImpl)client ).getDNSSubwayDomainList() )
        {
            System.out.println( String.format("[DEBUG] DNSSubwaySSHTunnelGWResolverInstanceImpl.initDNSSubwayResolver() : targetDomain=%s", domain ) );
        }

        setDNSLookupClient(client);
    }


    // 必須パラメータキーの一覧を取得する.
    public List<String> getRequiredParameterKeys() throws DNSServiceCommonException
    {
        List<String> list = new ArrayList<String>();
        list.add( LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_ADDRESS );
        list.add( LoulanDNSClientConstants.PROP_KEY_DNS_SERVER_PORT );
        list.add( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_TARGET_DOMAIN );
        list.add( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_TARGET_PORT );

        List<String> superList = super.getRequiredParameterKeys();
        list.addAll(superList);

        return list;
    }






}