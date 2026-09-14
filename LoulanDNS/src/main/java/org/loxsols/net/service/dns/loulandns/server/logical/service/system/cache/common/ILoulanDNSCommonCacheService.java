package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;


import java.util.Properties;
import java.util.List;

/**
 * LoulanDNSの汎用キャッシュサービスI/F
 * 
 * ILoulanDNSCacheService
 */
public interface ILoulanDNSCommonCacheService
{


    /**
     * 初期化メソッド.
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
     * @param key
     * @param object
     * @param ttl
     * @param options
     * @throws DNSCacheServiceCommonException
     */
    public void put(String key, Object object, int ttl, Properties options) throws DNSCacheServiceCommonException;


    /**
     *  キャッシュエントリを登録(又は更新)する.
     *  NOTE : 本メソッドは、主にサブクラスからの呼び出しを想定し、外部からの呼び出しは想定しない.
     * 
     * @param cacheEntry
     * @throws DNSCacheServiceCommonException
     */
    public void put(ILoulanDNSCommonCacheEntry cacheEntry) throws DNSCacheServiceCommonException;




    /**
     * キャッシュエントリを取得する.
     * 
     * @param key
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ILoulanDNSCommonCacheEntry get(String key) throws DNSCacheServiceCommonException;


    /**
     * キャッシュエントリを消去する.
     * 
     * @param key
     * @throws DNSCacheServiceCommonException
     */
    public void delete(String key) throws DNSCacheServiceCommonException;


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
    public List<ILoulanDNSCommonCacheEntry> getCacheEntriesList() throws DNSCacheServiceCommonException;

    

    /**
     * 本キャッシュサービスの標準TTL値を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getCacheServiceTTL() throws DNSCacheServiceCommonException;

    
}