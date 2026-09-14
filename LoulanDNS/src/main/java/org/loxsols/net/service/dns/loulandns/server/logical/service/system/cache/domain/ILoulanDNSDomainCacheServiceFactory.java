package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain;

import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheService;


/**
 * LoulanDNSのドメインキャッシュサービスのファクトリクラス.
 * 
 * ILoulanDNSDomainCacheServiceFactory
 */
public interface ILoulanDNSDomainCacheServiceFactory 
{

    /**
     * ファクトリクラスの初期化メソッド.
     * 
     * @param properties
     * @throws DNSDomainCacheServiceException
     */
    public void init(Properties properties) throws DNSDomainCacheServiceException;


    /**
     * ドメインキャッシュサービスの取得または新規作成.
     * 
     * @param cacheDomainServiceName
     * @param options
     * @return
     * @throws DNSDomainCacheServiceException
     */
    public ILoulanDNSDomainCacheService getOrCreateDomainCacheService(String cacheServiceName, Properties options) throws DNSDomainCacheServiceException;


    /**
     * 既存のドメインキャッシュサービスを取得する.
     * 存在しなければnullを返す.
     * 
     * @param cacheDomainServiceName
     * @return
     * @throws DNSDomainCacheServiceException
     */
    public ILoulanDNSDomainCacheService getDomainCacheService(String cacheServiceName) throws DNSDomainCacheServiceException;


    /**
     * ドメインキャッシュサービスを新規作成する.
     * 
     * @param cacheDomainServiceName
     * @param options
     * @return
     * @throws DNSDomainCacheServiceException
     */
    public ILoulanDNSDomainCacheService createDomainCacheService(String cacheServiceName, Properties options) throws DNSDomainCacheServiceException;
    

    /**
     * キャッシュサービスを消去する.
     * 
     * @param cacheDomainServiceName
     * @throws DNSDomainCacheServiceException
     */
    public void deleteDomainCacheService(String cacheServiceName) throws DNSDomainCacheServiceException;

    
}