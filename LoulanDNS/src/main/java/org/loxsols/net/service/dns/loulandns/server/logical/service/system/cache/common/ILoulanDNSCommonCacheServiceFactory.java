package org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common;

import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.IDNSResponseMessage;


/**
 * LoulanDNSの共通キャッシュサービスのファクトリクラス.
 * 
 * ILoulanDNSCommonCacheServiceFactory
 */
public interface ILoulanDNSCommonCacheServiceFactory 
{

    /**
     * ファクトリクラスの初期化メソッド.
     * 
     * @param properties
     * @throws DNSCacheServiceCommonException
     */
    public void init(Properties properties) throws DNSCacheServiceCommonException;


    /**
     * キャッシュサービスの取得または新規作成.
     * 
     * @param cacheServiceName
     * @param options
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ILoulanDNSCommonCacheService getOrCreateCacheService(String cacheServiceName, Properties options) throws DNSCacheServiceCommonException;


    /**
     * 既存のキャッシュサービスを取得する.
     * 存在しなければnullを返す.
     * 
     * @param cacheServiceName
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ILoulanDNSCommonCacheService getCacheService(String cacheServiceName) throws DNSCacheServiceCommonException;


    /**
     * キャッシュサービスを新規作成する.
     * 
     * @param cacheServiceName
     * @param options
     * @return
     * @throws DNSCacheServiceCommonException
     */
    public ILoulanDNSCommonCacheService createCacheService(String cacheServiceName, Properties options) throws DNSCacheServiceCommonException;
    

    /**
     * キャッシュサービスを消去する.
     * 
     * @param cacheServiceName
     * @throws DNSCacheServiceCommonException
     */
    public void deleteCacheService(String cacheServiceName) throws DNSCacheServiceCommonException;

    
}