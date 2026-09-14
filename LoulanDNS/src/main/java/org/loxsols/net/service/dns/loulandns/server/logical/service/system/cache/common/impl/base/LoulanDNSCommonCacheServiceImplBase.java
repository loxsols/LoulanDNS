package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.impl.base;

import org.checkerframework.checker.index.qual.NonNegative;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheEntry;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheService;

import java.util.Properties;
import java.util.List;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import java.util.concurrent.TimeUnit;
import java.time.Duration;

/**
 * LoulanDNSの汎用キャッシュサービスのベース実装クラス.
 * 
 * ILoulanDNSCacheService
 */
public class LoulanDNSCommonCacheServiceImplBase  implements ILoulanDNSCommonCacheService
{

    Properties properties;
    int maxCacheSize;
    int maxTTL;

    Cache<String, ILoulanDNSCommonCacheEntry> cacheInstance; 


    public void setProperties(Properties value) throws DNSCacheServiceCommonException
    {
        this.properties = properties;
    }

    public Properties getProperties() throws DNSCacheServiceCommonException
    {
        return this.properties;
    }

    protected void initCache(int maxCacheSize, int maxTTLSec) throws DNSCacheServiceCommonException
    {

        // this.cacheInstance = Caffeine.newBuilder()
        //     .maximumSize(maxCacheSize)                      // 最大容量の設定
        //     .expireAfterWrite(maxTTL, TimeUnit.SECONDS)     // 最大TTLの設定
        //     .build();


        // 可変有効期限をサポートするキャッシュを構築
        this.cacheInstance = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, ILoulanDNSCommonCacheEntry>() {
                @Override
                public long expireAfterCreate(String key, ILoulanDNSCommonCacheEntry value, long currentTime) {
                    // put時に指定されない場合のデフォルト値（ここでは無限）
                    return Long.MAX_VALUE; 
                }
                @Override
                public long expireAfterUpdate(String key, ILoulanDNSCommonCacheEntry value, long currentTime, long currentDuration) {
                    return currentDuration; // 更新時は既存のTTLを維持
                }
                @Override
                public long expireAfterRead(String key, ILoulanDNSCommonCacheEntry value, long currentTime, long currentDuration) {
                    return currentDuration; // 読み込み時は既存のTTLを維持
                }
            })
            .build();



    }

    protected Cache<String, ILoulanDNSCommonCacheEntry> getCacheInstance()  throws DNSCacheServiceCommonException
    {
        return this.cacheInstance;
    }




    /**
     * 初期化メソッド.
     * 
     * @param properties
     * @throws DNSCacheServiceCommonException
     */
    public void init(Properties properties) throws DNSCacheServiceCommonException
    {
        setProperties(properties);


        // 最大キャッシュサイズを設定する.
        String maxCacheSizeValue = properties.getProperty( LoulanDNSConstants.PROP_KEY_MAX_CACHE_SIZE );
        if ( maxCacheSizeValue == null )
        {
            String msg = String.format("Failed to init Cache-Service. maxCacheSize is not specified. key=%s", LoulanDNSConstants.PROP_KEY_MAX_CACHE_SIZE );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        int maxCacheSize = Integer.parseInt(maxCacheSizeValue);
        setMaxCacheEntries(maxCacheSize);


        // 最大キャッシュTTLを設定する.
        String maxTTLValue = properties.getProperty( LoulanDNSConstants.PROP_KEY_MAX_CACHE_TTL );
        if ( maxTTLValue == null )
        {
            String msg = String.format("Failed to init Cache-Service. maxTTL is not specified. key=%s", LoulanDNSConstants.PROP_KEY_MAX_CACHE_TTL );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        int maxTTL = Integer.parseInt(maxTTLValue);
        setMaxCacheTTL(maxTTL);


        initCache(maxCacheSize, maxTTL);

    }




    /**
     * キャッシュエントリの最大保持サイズを設定する.
     * 
     * @param max
     * @throws DNSCacheServiceCommonException
     */
    public void setMaxCacheEntries(int max) throws DNSCacheServiceCommonException
    {
        this.maxCacheSize = max;
    }

    /**
     * キャッシュエントリの最大保持サイズを返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getMaxCacheEntries() throws DNSCacheServiceCommonException
    {
        return this.maxCacheSize;
    }


    /**
     * キャッシュの最大TTL(sec)を設定する.
     * 
     * @param maxTTLSec
     * @throws DNSCacheServiceCommonException
     */
    public void setMaxCacheTTL(int maxTTLSec) throws DNSCacheServiceCommonException
    {
        this.maxTTL = maxTTLSec;
    }

    /**
     * キャッシュの最大TTL(sec)を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getMaxCacheTTL() throws DNSCacheServiceCommonException
    {
        return this.maxTTL;
    }



    /**
     * キャッシュエントリを登録(又は更新)する.
     * 
     * @param key
     * @param object
     * @param ttl
     * @param options
     * @throws DNSCacheServiceCommonException
     */
    public void put(String key, Object object, int ttl, Properties options) throws DNSCacheServiceCommonException
    {
        ILoulanDNSCommonCacheEntry cacheEntry = new LoulanDNSCommonCacheEntryBaseImpl(key, object, ttl, options);
        put( cacheEntry );
    }


    /**
     *  キャッシュエントリを登録(又は更新)する.
     *  NOTE : 本メソッドは、主にサブクラスからの呼び出しを想定し、外部からの呼び出しは想定しない.
     * 
     * @param cacheEntry
     * @throws DNSCacheServiceCommonException
     */
    public void put(ILoulanDNSCommonCacheEntry cacheEntry) throws DNSCacheServiceCommonException
    {
        Cache<String, ILoulanDNSCommonCacheEntry> cache = getCacheInstance();
    
        // cache.policy().expireVariably().orElseThrow()
        // .put(cacheEntry.getKey(), cacheEntry, Duration.ofSeconds( cacheEntry.getLeftTTLSeconds() ) );


        String cacheEntryKey = cacheEntry.getKey();
        int cacheEntryTTL = cacheEntry.getLeftTTLSeconds();

        // expireVariably() ポリシーを経由して、個別のTTL付きで値を設定
        cache.policy().expireVariably().ifPresent(policy -> {
            policy.put( cacheEntryKey, cacheEntry, Duration.ofSeconds( cacheEntryTTL ) );
        });


    }



    /**
     * キャッシュエントリを取得する.
     * 
     * @param key
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ILoulanDNSCommonCacheEntry get(String key) throws DNSCacheServiceCommonException
    {
        Cache<String, ILoulanDNSCommonCacheEntry> cache = getCacheInstance();

        ILoulanDNSCommonCacheEntry cacheEntry = cache.getIfPresent(key);
        return cacheEntry;
    }


    /**
     * キャッシュエントリを消去する.
     * 
     * @param key
     * @throws DNSCacheServiceCommonException
     */
    public void delete(String key) throws DNSCacheServiceCommonException
    {
        Cache<String, ILoulanDNSCommonCacheEntry> cache = getCacheInstance();

        cache.invalidate(key);

    }


    /**
     * キャッシュエントリの現状登録件数を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getCurrentCacheEntriesCount()throws DNSCacheServiceCommonException
    {
        Cache<String, ILoulanDNSCommonCacheEntry> cache = getCacheInstance();

        int count = (int)cache.estimatedSize();
        return count;
    }


    /**
     * 現在登録されているキャッシュエントリの一覧をリスト形式で返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public List<ILoulanDNSCommonCacheEntry> getCacheEntriesList() throws DNSCacheServiceCommonException
    {
        Cache<String, ILoulanDNSCommonCacheEntry> cache = getCacheInstance();

        List<ILoulanDNSCommonCacheEntry> list = cache.asMap().values().stream().toList();
        return list;
    }


    /**
     * 本キャッシュサービスの標準TTL値を返す.
     * 
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public int getCacheServiceTTL() throws DNSCacheServiceCommonException
    {
        int ttl = getMaxCacheTTL();
        return ttl;
    }


    
}