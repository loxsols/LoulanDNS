package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.impl;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.DNSProtocolConstants;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.factory.impl.message.SimpleDNSMessageFactoryImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.IDNSQueryPart;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.impl.base.LoulanDNSCommonCacheServiceImplBase;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.ILoulanDNSDomainCacheEntry;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.ILoulanDNSDomainCacheService;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.*;

import java.util.Properties;
import java.util.ArrayList;
import java.util.List;



/**
 * LoulanDNSのドメインキャッシュサービスの実装クラス.
 * 
 * LoulanDNSDomainCacheServiceImpl
 */
public class LoulanDNSDomainCacheServiceImpl extends LoulanDNSCommonCacheServiceImplBase implements ILoulanDNSDomainCacheService 
{


    /**
     * 初期化メソッド
     * 
     * @param properties
     * @throws DNSCacheServiceCommonException
     */
    public void init(Properties properties) throws DNSCacheServiceCommonException
    {
        super.init(properties);
    }



    /**
     * キャッシュエントリを登録(又は更新)する.
     * 
     * @param domainName
     * @param queryType
     * @param queryClass
     * @param dnsResponseMessage
     * @param ttl
     * @param options
     * @throws DNSCacheServiceCommonException
     */
    public void put(String domainName, int queryType, int queryClass, IDNSResponseMessage dnsResponseMessage, int ttl, Properties options) throws DNSCacheServiceCommonException
    {
        ILoulanDNSDomainCacheEntry cacheEntry = new LoulanDNSDomainCacheEntryImpl(domainName, queryType, queryClass, dnsResponseMessage, ttl, options);
        super.put( (ILoulanDNSCommonCacheEntry)cacheEntry );
    }


    /**
     * キャッシュエントリを登録(又は更新)する.
     * 
     * @param domainName
     * @param queryType
     * @param dnsResponseMessage
     * @param ttl
     * @param options
     * @throws DNSCacheServiceCommonException
     */
    public void put(String domainName, int queryType, IDNSResponseMessage dnsResponseMessage, int ttl, Properties options) throws DNSCacheServiceCommonException
    {
        // ドメインキャッシュの更新時にクエリクラス値を(IN 1 the Internet)で固定して検索する.
        int qClass = DNSProtocolConstants.DNS_CLASS_IN;
        put(domainName, queryType, qClass, dnsResponseMessage, ttl, options);
    }


    /**
     * キャッシュエントリを登録(又は更新)する.
     * 
     * @param dnsResponseMessage
     * @param ttl
     * @param options
     * @throws DNSCacheServiceCommonException
     */
    public void put(IDNSResponseMessage dnsResponseMessage, int ttl, Properties options) throws DNSCacheServiceCommonException
    {
        String qName = LoulanDNSDomainCacheUtils.getDNSMessageQName(dnsResponseMessage);
        int qType = LoulanDNSDomainCacheUtils.getDNSMessageQType(dnsResponseMessage);
        int qClass = LoulanDNSDomainCacheUtils.getDNSMessageQClass(dnsResponseMessage);

        put( qName, qType, qClass, dnsResponseMessage, ttl, options);

    }




    /**
     * キャッシュエントリを取得する.
     * 
     * @param domainName
     * @param queryType
     * @param queryClass
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public IDNSResponseMessage get(String domainName, int queryType, int queryClass) throws DNSCacheServiceCommonException
    {
        ILoulanDNSDomainCacheEntry cacheEntry = getDomainCacheEntry(domainName, queryType, queryClass);

        IDNSResponseMessage responseMessage = cacheEntry.getDNSresponseMessage();
        return responseMessage;
    }


    /**
     * キャッシュエントリを取得する.
     * 
     * @param domainName
     * @param queryType
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public IDNSResponseMessage get(String domainName, int queryType ) throws DNSCacheServiceCommonException
    {
        // ドメインキャッシュの取得時にクエリクラス値を(IN 1 the Internet)で固定して検索する.
        int qClass = DNSProtocolConstants.DNS_CLASS_IN;
        IDNSResponseMessage cacheObject = get(domainName, queryType, qClass );
        return cacheObject;
    }


    /**
     * キャッシュエントリを取得する.
     * 
     * @param questionMessage
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public IDNSResponseMessage get(IDNSQuestionMessage questionMessage) throws DNSCacheServiceCommonException
    {
        String qName = LoulanDNSDomainCacheUtils.getDNSMessageQName(questionMessage);
        int qType = LoulanDNSDomainCacheUtils.getDNSMessageQType(questionMessage);
        int qClass = LoulanDNSDomainCacheUtils.getDNSMessageQClass(questionMessage);

        ILoulanDNSDomainCacheEntry cacheEntry = getDomainCacheEntry(qName, qType, qClass);
        if ( cacheEntry == null )
        {
            // キャッシュが未登録のためnullで復帰する.
            return null;
        }

        // ドメインキャッシュエントリから、ID値などを適当に調製したレスポンスメッセージを生成する.
        IDNSResponseMessage responseMessage = LoulanDNSDomainCacheUtils.createDNSResponseMessageFromDomainCacheEntry(cacheEntry, questionMessage);

        return responseMessage;
    }




    /**
     * キャッシュエントリを消去する.
     * 
     * @param domainName
     * @param queryType
     * @param queryClass
     * @throws DNSCacheServiceCommonException
     */
    public void delete(String domainName, int queryType, int queryClass) throws DNSCacheServiceCommonException
    {
        String key = LoulanDNSDomainCacheUtils.buildDomainCacheKey(domainName, queryType, queryClass);
        super.delete(key);
    }


    /**
     * キャッシュエントリを消去する.
     * 
     * @param domainName
     * @param queryType
     * @throws DNSCacheServiceCommonException
     */
    public void delete(String domainName, int queryType ) throws DNSCacheServiceCommonException
    {
        // ドメインキャッシュの消去時にクエリクラス値を(IN 1 the Internet)で固定して操作する.
        int qClass = DNSProtocolConstants.DNS_CLASS_IN;
        delete(domainName, queryType, qClass);
    }


    /**
     * キャッシュエントリを消去する.
     * 
     * @param questionMessage
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public void delete(IDNSQuestionMessage questionMessage) throws DNSCacheServiceCommonException
    {
        String qName = LoulanDNSDomainCacheUtils.getDNSMessageQName(questionMessage);
        int qType = LoulanDNSDomainCacheUtils.getDNSMessageQType(questionMessage);
        int qClass = LoulanDNSDomainCacheUtils.getDNSMessageQClass(questionMessage);
        
        delete(qName, qType, qClass);
    }



    /**
     * 現在登録されているキャッシュエントリの一覧をリスト形式で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public List<ILoulanDNSDomainCacheEntry> getDomainCacheEntriesList() throws DNSCacheServiceCommonException
    {
        List<ILoulanDNSCommonCacheEntry> list = super.getCacheEntriesList();
        if ( list == null )
        {
            return null;
        }


        List<ILoulanDNSDomainCacheEntry> domainCacheList = new ArrayList<ILoulanDNSDomainCacheEntry>();

        // 本キャッシュサービスのキャッシュエントリは2重にラップされているため、外側の層をはがす.
        for( ILoulanDNSCommonCacheEntry cacheEntry : list )
        {
            ILoulanDNSDomainCacheEntry domainCacheEntry = (ILoulanDNSDomainCacheEntry)cacheEntry.getObject();
            domainCacheList.add( domainCacheEntry );
        }

        return domainCacheList;
    }



    /**
     * 試験用デバッグmainメソッド.
     * @param args
     */
    public static void main(String[] args)
    {
        Properties properties = new Properties();
        properties.setProperty(LoulanDNSConstants.PROP_KEY_MAX_CACHE_SIZE, "1000");
        properties.setProperty(LoulanDNSConstants.PROP_KEY_MAX_CACHE_TTL, "3600");
        properties.setProperty(LoulanDNSConstants.PROP_KEY_CACHE_SERVICE_TYPE, LoulanDNSConstants.CONST_CACHE_SERVICE_TYPE_DOMAIN_CACHE );

        try
        {

            ILoulanDNSCommonCacheServiceFactory factory = new LoulanDNSDomainCacheServiceFactoryImpl();
            ILoulanDNSDomainCacheService cacheService = (ILoulanDNSDomainCacheService)factory.getOrCreateCacheService("cacheService", properties);

            cacheService.init(properties);

            SimpleDNSMessageFactoryImpl messageFactory = new SimpleDNSMessageFactoryImpl();
 
            IDNSResponseMessage responseG = messageFactory.createSimpleResponseDNSMesssageA("google.co.jp", DNSProtocolConstants.DNS_RR_TYPE_A, DNSProtocolConstants.DNS_CLASS_IN, 3600, "127.0.0.1");
            cacheService.put("google.co.jp", DNSProtocolConstants.DNS_RR_TYPE_A, responseG, 3600, null);

            IDNSResponseMessage responseY = messageFactory.createSimpleResponseDNSMesssageA("yahoo.co.jp", DNSProtocolConstants.DNS_RR_TYPE_A, DNSProtocolConstants.DNS_CLASS_IN, 3600, "127.0.0.2");
            cacheService.put("yahoo.co.jp", DNSProtocolConstants.DNS_RR_TYPE_A, responseY, 3600, null);

            IDNSResponseMessage responseD = messageFactory.createSimpleResponseDNSMesssageA("duckduckgo.com", DNSProtocolConstants.DNS_RR_TYPE_A, DNSProtocolConstants.DNS_CLASS_IN, 3600, "127.0.0.3");
            cacheService.put("duckduckgo.com", DNSProtocolConstants.DNS_RR_TYPE_A, responseD, 3600, null);

            IDNSResponseMessage  cacheY = cacheService.get("yahoo.co.jp", DNSProtocolConstants.DNS_RR_TYPE_A);
            
            cacheService.delete("yahoo.co.jp", DNSProtocolConstants.DNS_RR_TYPE_A);

        }
        catch(DNSServiceCommonException exception)
        {
            exception.printStackTrace();
        }

    }


    private ILoulanDNSDomainCacheEntry getDomainCacheEntry(String domainName, int queryType, int queryClass) throws DNSCacheServiceCommonException
    {
        String key = LoulanDNSDomainCacheUtils.buildDomainCacheKey(domainName, queryType, queryClass);
        ILoulanDNSCommonCacheEntry cacheEntry = super.get(key);

        if ( cacheEntry == null )
        {
            return null;
        }

        if ( ( cacheEntry instanceof ILoulanDNSDomainCacheEntry ) == false )
        {
            String msg = String.format("Failed to get DomainCache Object. Invalid cache entry type:%s", cacheEntry.getClass().getName() );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        return (ILoulanDNSDomainCacheEntry)cacheEntry;
    }



    
}