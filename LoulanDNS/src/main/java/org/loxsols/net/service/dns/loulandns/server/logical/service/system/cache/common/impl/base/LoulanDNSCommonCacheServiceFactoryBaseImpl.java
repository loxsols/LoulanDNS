package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.impl.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheServiceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.impl.LoulanDNSDomainCacheServiceImpl;


/**
 * LoulanDNSの共通キャッシュサービスのファクトリクラスのベース実装クラス.
 * 
 * LoulanDNSCommonCacheServiceFactoryBaseImpl
 */
public class LoulanDNSCommonCacheServiceFactoryBaseImpl implements ILoulanDNSCommonCacheServiceFactory
{

    Properties properties = new Properties();

    Map<String, ILoulanDNSCommonCacheService> cacheServiceRepository = new HashMap<String, ILoulanDNSCommonCacheService>();


    protected void setProperties(Properties instance)
    {
        this.properties = instance;
    }

    protected Properties getProperties()
    {
        return this.properties;
    }


    // 本クラス内部のリポジトリにキャッシュサービスを登録する.
    protected void registCacheServiceRepository(String serviceName, ILoulanDNSCommonCacheService cacheService)
    {
        this.cacheServiceRepository.put(serviceName, cacheService);
    }
    
    // 本クラス内部のリポジトリからキャッシュサービスを取得する.
    protected ILoulanDNSCommonCacheService getCacheServiceRepository(String serviceName)
    {
        ILoulanDNSCommonCacheService cacheService = this.cacheServiceRepository.get(serviceName);
        return cacheService;
    }

    // 本クラス内部のリポジトリからキャッシュサービスを消去する.
    protected void deleteCacheServiceRepository(String serviceName)
    {
        this.cacheServiceRepository.remove(serviceName);
    }



    /**
     * ファクトリクラスの初期化メソッド.
     * 
     * @param properties
     * @throws DNSCacheServiceCommonException
     */
    public void init(Properties properties) throws DNSCacheServiceCommonException
    {
        // 今のところ特に設定すべきパラメータはないので、指定された値を保持する程度.
        setProperties(properties);
    }


    /**
     * キャッシュサービスの取得または新規作成.
     * 
     * @param cacheServiceName
     * @param options
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ILoulanDNSCommonCacheService getOrCreateCacheService(String cacheServiceName, Properties options) throws DNSCacheServiceCommonException
    {
        ILoulanDNSCommonCacheService cacheService = getCacheService(cacheServiceName);

        if ( cacheService == null )
        {
            cacheService = createCacheService(cacheServiceName, options);
        }

        return cacheService;
    }


    /**
     * 既存のキャッシュサービスを取得する.
     * 存在しなければnullを返す.
     * 
     * @param cacheServiceName
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ILoulanDNSCommonCacheService getCacheService(String cacheServiceName) throws DNSCacheServiceCommonException
    {
        ILoulanDNSCommonCacheService cacheService = cacheServiceRepository.get(cacheServiceName);
        return cacheService;
    }


    /**
     * キャッシュサービスを新規作成する.
     * 
     * @param cacheServiceName
     * @param options
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ILoulanDNSCommonCacheService createCacheService(String cacheServiceName, Properties options) throws DNSCacheServiceCommonException
    {
        String cacheServiceType = options.getProperty( LoulanDNSConstants.PROP_KEY_CACHE_SERVICE_TYPE );

        if ( cacheServiceType == null || cacheServiceType.isEmpty() )
        {
            // キャッシュサービスタイプ("loulan.dns.cache.service.type")が指定されていない.
            String msg = String.format("Failed to create CacheService. Cache sevice type is not specified. key=%s", LoulanDNSConstants.PROP_KEY_CACHE_SERVICE_TYPE );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }


        ILoulanDNSCommonCacheService cacheService;

        if ( cacheServiceType.equals( LoulanDNSConstants.CONST_CACHE_SERVICE_TYPE_COMMON_CACHE) )
        {
            // 汎用キャッシュ.
            cacheService = new LoulanDNSCommonCacheServiceImplBase();
        }
        else if ( cacheServiceType.equals( LoulanDNSConstants.CONST_CACHE_SERVICE_TYPE_DOMAIN_CACHE) )
        {
            // ドメインキャッシュ
            cacheService = new LoulanDNSDomainCacheServiceImpl();
        }
        else
        {
            // 未定義の値が指定された(例外をスロー)
            String msg = String.format("Failed to create CacheService. Invalid cache sevice type : %s", cacheServiceType );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        // 生成したキャッシュサービスを初期化.
        cacheService.init(options);


        // リポジトリに生成したキャッシュサービスを登録.
        registCacheServiceRepository(cacheServiceName, cacheService);

        return cacheService;
    }
    

    /**
     * キャッシュサービスを消去する.
     * 
     * @param cacheServiceName
     * @throws DNSCacheServiceCommonException
     */
    public void deleteCacheService(String cacheServiceName) throws DNSCacheServiceCommonException
    {
        ILoulanDNSCommonCacheService cacheService = getCacheService(cacheServiceName);

        if ( cacheService == null )
        {
            String msg = String.format("Failed to delete CacheService. Specified cache service is not registered : %s", cacheServiceName );
            DNSCacheServiceCommonException exception = new DNSCacheServiceCommonException(msg);
            throw exception;
        }

        // リポジトリから消去.
        deleteCacheServiceRepository(cacheServiceName);
    }

    
}