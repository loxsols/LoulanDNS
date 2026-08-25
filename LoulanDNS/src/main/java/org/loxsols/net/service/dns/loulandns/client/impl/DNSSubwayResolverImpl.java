package org.loxsols.net.service.dns.loulandns.client.impl;


import java.net.InetAddress;
import java.util.HashMap;

import org.apache.http.impl.conn.InMemoryDnsResolver;
import org.loxsols.net.service.dns.loulandns.client.*;
import org.loxsols.net.service.dns.loulandns.client.impl.simple.*;
import org.loxsols.net.service.dns.loulandns.client.subway.IDNSSubwayGatewayInstancePool;
import org.loxsols.net.service.dns.loulandns.client.subway.common.DNSSubwayConstants;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.IDNSSubwayGatewayInstancePoolFactory;
import org.loxsols.net.service.dns.loulandns.client.subway.factory.impl.DNSSubwayGatewayInstancePoolFactoryImpl;
import org.loxsols.net.service.dns.loulandns.client.common.DNSClientCommonException;
import org.loxsols.net.service.dns.loulandns.client.common.LoulanDNSClientConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.SimpleDNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.DNSAdditionalSectionFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.section.rr.DNSResourceRecordFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.model.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.IDNSResolverInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.common.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import java.util.Properties;


@ComponentScan
public class DNSSubwayResolverImpl extends SimpleDNSSubwayResolverImpl implements IDNSLookupClient
{

    IDNSResolverInstanceFactory dnsResolerInstanceFactory;

    public DNSSubwayResolverImpl() throws DNSServiceCommonException
    {
        super();
    }


    @Autowired
    @Qualifier("dnsMessageFactoryImpl")
    public void setDNSMessageFacotry(IDNSMessageFactory instance)
    {
        super.setDNSMessageFacotry(instance);
    }



    @Autowired
    @Qualifier("dnsResolverInstanceFactoryImpl")
    public void setDNSResolverInstanceFactory(IDNSResolverInstanceFactory instance)
    {
        this.dnsResolerInstanceFactory = instance;
    }

    public IDNSResolverInstanceFactory getDNSResolverInstanceFactory()
    {
        return this.dnsResolerInstanceFactory;
    }


    /**
     * 初期化メソッド.
     * 基底クラスの処理に加え、以下の処理を追加する.
     * - DNS-Subwayのサブリゾルバ(デフォルトゲートリゾルバ)を設定する.
     * 
     */
    public void init(Properties properties) throws DNSClientCommonException
    {

        super.init( properties );

        // 基底クラスで足りない処理を以下に追加する.
        // try
        {

            // ------------------ 
            // DNS-Subwayのサブリゾルバを設定する.
            // ------------------
            String subResolverName = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_SUB_RESOLVER_NAME);
            if ( subResolverName == null || subResolverName.isEmpty() )
            {
                String msg = String.format("Faield to init DNSSubwayResolverImpl. SubResolverName is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_SUB_RESOLVER_NAME );
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;      
            }
            

            String subResolverUserName = properties.getProperty( DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_SUB_RESOLVER_USER_NAME);
            if ( subResolverUserName == null || subResolverUserName.isEmpty() )
            {
                String msg = String.format("Faield to init DNSSubwayResolverImpl. SubResolverUserName is not specified. key=%s", DNSSubwayConstants.CONST_PROP_KEY_DNS_SUBWAY_SUB_RESOLVER_USER_NAME );
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;      
            }
            

            // サブリゾルバ(デフォルトゲートリゾルバ)のインスタンスを生成して本クラスに設定する.
            // TODO : GWの所有ユーザーと、サブリゾルバの所有ユーザーが異なる場合に例外をスローすべきか要検討.
            IDNSResolverInstanceFactory reslverInstancefactory = getDNSResolverInstanceFactory();
            if ( reslverInstancefactory == null )
            {
                String msg = String.format("Faield to init DNSSubwayResolverImpl. IDNSResolverInstanceFactory is null." );
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;    
            }


            IDNSResolverInstance subResolverInstance;
            try
            {
                subResolverInstance = reslverInstancefactory.getOrCreateResolverInstance(subResolverUserName, subResolverName);
            }
            catch(DNSServiceCommonException cause)
            {
                String msg = String.format("Faield to init DNSSubwayResolverImpl. Faield to get/create sub-resolver instance." );
                DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
                throw exception;
            }


            if ( subResolverInstance == null )
            {
                String msg = String.format("Faield to init DNSSubwayResolverImpl. Not able to create sub-resolver instance. subResolverUserName=%s, subResolverName=%s", subResolverUserName, subResolverName );
                DNSClientCommonException exception = new DNSClientCommonException(msg);
                throw exception;   
            }

            setSubResolverInstance(subResolverInstance);

        }
        // catch(DNSServiceCommonException cause)
        // {
        //    String msg = String.format("Faield to init DNSSubwayResolverImpl." );
        //    DNSClientCommonException exception = new DNSClientCommonException(msg, cause);
        //    throw exception;
        // }

    }

}