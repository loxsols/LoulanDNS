package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheServiceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.impl.base.LoulanDNSCommonCacheServiceFactoryBaseImpl;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.impl.base.LoulanDNSCommonCacheServiceImplBase;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.impl.LoulanDNSDomainCacheServiceImpl;


/**
 * LoulanDNSのドメインキャッシュサービスのファクトリクラスの実装クラス.
 *
 * LoulanDNSDomainCacheServiceFactoryImpl
 */
public class LoulanDNSDomainCacheServiceFactoryImpl extends LoulanDNSCommonCacheServiceFactoryBaseImpl implements ILoulanDNSDomainCacheServiceFactory
{

    /**
     * ファクトリクラスの初期化メソッド.
     * 
     * @param properties
     * @throws DNSDomainCacheServiceException
     */
    public void init(Properties properties) throws DNSDomainCacheServiceException
    {
        try
        {
            super.init(properties);
        }
        catch(DNSCacheServiceCommonException cause)
        {
            String msg = String.format("Failed to init DomainCacheService instance.");
            DNSDomainCacheServiceException exception =new DNSDomainCacheServiceException(msg, cause);
            throw exception;
        }
    }


    /**
     * ドメインキャッシュサービスの取得または新規作成.
     * 
     * @param cacheDomainServiceName
     * @param options
     * @return
     * @throws DNSDomainCacheServiceException
     */
    public ILoulanDNSDomainCacheService getOrCreateDomainCacheService(String cacheServiceName, Properties options) throws DNSDomainCacheServiceException
    {
        try
        {
            ILoulanDNSCommonCacheService cacheInstance = (ILoulanDNSCommonCacheService)super.getOrCreateCacheService(cacheServiceName, options);

            if ( ( cacheInstance instanceof ILoulanDNSDomainCacheService ) == false )
            {
                String msg = String.format("Failed to getOrCreate DomainCacheService instance. NOT instance of ILoulanDNSDomainCacheService. The CacheService Instance class is %s", cacheInstance.getClass().getName() );
                DNSDomainCacheServiceException exception =new DNSDomainCacheServiceException(msg);
                throw exception;
            }
            
            return (ILoulanDNSDomainCacheService)cacheInstance;
        }
        catch(DNSCacheServiceCommonException cause)
        {
            String msg = String.format("Failed to getOrCreate DomainCacheService instance.");
            DNSDomainCacheServiceException exception =new DNSDomainCacheServiceException(msg, cause);
            throw exception;
        }

    }


    /**
     * 既存のドメインキャッシュサービスを取得する.
     * 存在しなければnullを返す.
     * 
     * @param cacheDomainServiceName
     * @return
     * @throws DNSDomainCacheServiceException
     */
    public ILoulanDNSDomainCacheService getDomainCacheService(String cacheServiceName) throws DNSDomainCacheServiceException
    {
        try
        {
            ILoulanDNSDomainCacheService instance = (ILoulanDNSDomainCacheService)super.getCacheService(cacheServiceName);
            return instance;
        }
        catch(DNSCacheServiceCommonException cause)
        {
            String msg = String.format("Failed to get DomainCacheService instance.");
            DNSDomainCacheServiceException exception =new DNSDomainCacheServiceException(msg, cause);
            throw exception;
        }
    }


    /**
     * ドメインキャッシュサービスを新規作成する.
     * 
     * @param cacheDomainServiceName
     * @param options
     * @return
     * @throws DNSDomainCacheServiceException
     */
    public ILoulanDNSDomainCacheService createDomainCacheService(String cacheServiceName, Properties options) throws DNSDomainCacheServiceException
    {
        try
        {
            ILoulanDNSDomainCacheService instance = (ILoulanDNSDomainCacheService)super.createCacheService(cacheServiceName, options);
            return instance;
        }
        catch(DNSCacheServiceCommonException cause)
        {
            String msg = String.format("Failed to create DomainCacheService instance.");
            DNSDomainCacheServiceException exception =new DNSDomainCacheServiceException(msg, cause);
            throw exception;
        }
    }
    

    /**
     * キャッシュサービスを消去する.
     * 
     * @param cacheDomainServiceName
     * @throws DNSDomainCacheServiceException
     */
    public void deleteDomainCacheService(String cacheServiceName) throws DNSDomainCacheServiceException
    {
        try
        {
            super.deleteCacheService(cacheServiceName);
        }
        catch(DNSCacheServiceCommonException cause)
        {
            String msg = String.format("Failed to delete DomainCacheService instance.");
            DNSDomainCacheServiceException exception =new DNSDomainCacheServiceException(msg, cause);
            throw exception;
        }
    }

    



    
}