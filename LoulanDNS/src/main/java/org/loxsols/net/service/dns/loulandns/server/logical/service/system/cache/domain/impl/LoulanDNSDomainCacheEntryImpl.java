package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.impl;

import java.time.ZonedDateTime;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.ILoulanDNSDomainCacheEntry;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.impl.base.*;

import io.netty.handler.codec.dns.DnsMessage;



/**
 * LoulanDNSのドメインキャッシュサービスのエントリ要素I/F
 * 
 * ILoulanDNSDomainCacheEntry
 */
public class LoulanDNSDomainCacheEntryImpl extends LoulanDNSCommonCacheEntryBaseImpl implements ILoulanDNSDomainCacheEntry
{

    String domainName;
    Integer queryType;
    Integer queryClass;


    /**
     * コンストラクタ.
     * 
     * @param key
     * @param object
     * @param ttl
     * @param properties
     * @throws DNSCacheServiceCommonException
     */
    public LoulanDNSDomainCacheEntryImpl(String domainName, int queryType, int queryClass, IDNSResponseMessage dnsResponseMessage, int ttl, Properties options) throws DNSCacheServiceCommonException
    {
        setDomainName( domainName );
        setQueryType( queryType );
        setQueryClass( queryClass );

        String key = buildDomainCacheKey();

        init(key, dnsResponseMessage, ttl, options);
    }


    /**
     * 本ドメインキャッシュエントリのドメイン名を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getDomainName() throws DNSCacheServiceCommonException
    {
        return this.domainName;
    }

    /**
     * 本ドメインキャッシュエントリの問い合わせタイプ(Ex.A, AAAA, ANY...etc)を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public Integer getQueryType() throws DNSCacheServiceCommonException
    {
        return this.queryType;
    }


    /**
     * 本ドメインキャッシュエントリの問い合わせクラス(IN, CS, ...etc)を返す.
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public Integer getQueryClass() throws DNSCacheServiceCommonException
    {
        return this.queryClass;
    }


    /**
     * 本ドメインキャッシュエントリの目的物たるDNSレスポンスメッセージを返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public IDNSResponseMessage getDNSresponseMessage() throws DNSCacheServiceCommonException
    {
        Object cacheObject = getObject();

        if ( ( cacheObject instanceof IDNSResponseMessage ) == false )
        {
            String msg = String.format("Failed to get DomainCache Object. cacheObject is not instanceof IDNSResponseMessage. %s", cacheObject.getClass().getName() );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        return (IDNSResponseMessage)cacheObject;
    }


    /**
     * 本ドメインキャッシュのキー値を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public String getDomainCacheKey() throws DNSCacheServiceCommonException
    {
        String key = super.getKey();

        if ( key.equals( buildDomainCacheKey() ) == false )
        {
            String msg = String.format("Invalid DomainCache key. key=%s, domainCacheKey=%s", key, buildDomainCacheKey() );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        return key;
    }




    /**
     * ドメイン名、クエリタイプ, クエリクラスを基にキャッシュキーを生成する.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    protected String buildDomainCacheKey() throws DNSCacheServiceCommonException
    {
        if ( getDomainName() == null || getDomainName().isEmpty() )
        {
            String msg = String.format("Failed to create DomainCache key. DomainName is not specified. dname=%s", getDomainName() );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        if ( getQueryType() == null )
        {
            String msg = String.format("Failed to create DomainCache key. QueryType is not specified. qType=%d", getQueryType());
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        if ( getQueryClass() == null )
        {
            String msg = String.format("Failed to create DomainCache key. QueryClass is not specified. qClass=%d", getQueryClass());
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }


        String domainCacheKey = LoulanDNSDomainCacheUtils.buildDomainCacheKey( getDomainName(), getQueryType(), getQueryClass() );
        return domainCacheKey;
    }


    protected void setDomainName(String dname) throws DNSCacheServiceCommonException
    {
        this.domainName = dname;
    }


    protected void setQueryType(Integer qType) throws DNSCacheServiceCommonException
    {
        this.queryType = qType;        
    }


    protected void setQueryClass(Integer qClass) throws DNSCacheServiceCommonException
    {
        this.queryClass = qClass;
    }
    
}