package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSQuestionMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;

import java.util.Properties;
import java.util.List;

/**
 * LoulanDNSのドメインキャッシュサービスI/F
 * 
 * ILoulanDNSDomainCacheService
 */
public interface ILoulanDNSDomainCacheService
{


    /**
     * 初期化メソッド
     * 
     * @param properties
     * @throws DNSCacheServiceCommonException
     */
    public void init(Properties properties) throws DNSCacheServiceCommonException;


    /**
     * キャッシュエントリの最大保持サイズを設定する.
     * 
     * @param max
     * @throws DNSCacheServiceCommonException
     */
    public void setMaxCacheEntries(int max) throws DNSCacheServiceCommonException;

    /**
     * キャッシュエントリの最大保持サイズを返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getMaxCacheEntries() throws DNSCacheServiceCommonException;


    /**
     * キャッシュの最大TTL(sec)を設定する.
     * 
     * @param maxTTLSec
     * @throws DNSCacheServiceCommonException
     */
    public void setMaxCacheTTL(int maxTTLSec) throws DNSCacheServiceCommonException;

    /**
     * キャッシュの最大TTL(sec)を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getMaxCacheTTL() throws DNSCacheServiceCommonException;
    


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
    public void put(String domainName, int queryType, int queryClass, IDNSResponseMessage dnsResponseMessage, int ttl, Properties options) throws DNSCacheServiceCommonException;


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
    public void put(String domainName, int queryType, IDNSResponseMessage dnsResponseMessage, int ttl, Properties options) throws DNSCacheServiceCommonException;


    /**
     * キャッシュエントリを登録(又は更新)する.
     * 
     * @param dnsResponseMessage
     * @param ttl
     * @param options
     * @throws DNSCacheServiceCommonException
     */
    public void put(IDNSResponseMessage dnsResponseMessage, int ttl, Properties options) throws DNSCacheServiceCommonException;




    /**
     * キャッシュエントリを取得する.
     * 
     * @param domainName
     * @param queryType
     * @param queryClass
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public IDNSResponseMessage get(String domainName, int queryType, int queryClass) throws DNSCacheServiceCommonException;


    /**
     * キャッシュエントリを取得する.
     * 
     * @param domainName
     * @param queryType
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public IDNSResponseMessage get(String domainName, int queryType ) throws DNSCacheServiceCommonException;


    /**
     * キャッシュエントリを取得する.
     * 
     * @param questionMessage
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public IDNSResponseMessage get(IDNSQuestionMessage questionMessage) throws DNSCacheServiceCommonException;



    /**
     * キャッシュエントリを消去する.
     * 
     * @param domainName
     * @param queryType
     * @param queryClass
     * @throws DNSCacheServiceCommonException
     */
    public void delete(String domainName, int queryType, int queryClass) throws DNSCacheServiceCommonException;


    /**
     * キャッシュエントリを消去する.
     * 
     * @param domainName
     * @param queryType
     * @throws DNSCacheServiceCommonException
     */
    public void delete(String domainName, int queryType ) throws DNSCacheServiceCommonException;


    /**
     * キャッシュエントリを消去する.
     * 
     * @param questionMessage
     * @throws DNSCacheServiceCommonException
     */
    public void delete(IDNSQuestionMessage questionMessage) throws DNSCacheServiceCommonException;


    /**
     * キャッシュエントリの現状登録件数を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getCurrentCacheEntriesCount()throws DNSCacheServiceCommonException;


    /**
     * 現在登録されているキャッシュエントリの一覧をリスト形式で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public List<ILoulanDNSDomainCacheEntry> getDomainCacheEntriesList() throws DNSCacheServiceCommonException;


    
}