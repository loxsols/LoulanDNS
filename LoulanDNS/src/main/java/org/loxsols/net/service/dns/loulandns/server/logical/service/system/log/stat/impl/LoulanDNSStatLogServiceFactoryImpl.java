package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.impl;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.ILoulanDNSStatLogService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.ILoulanDNSStatLogServiceFactory;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

/**
 * LoulanDNSの統計情報入出力サービスのファクトリI/Fの実装クラス
 * 本実装クラスでは、DBのCommonSystemLogテーブルにデータを入出力するサービスを生成する.
 * 
 * ILoulanDNSStatLogServiceFactory
 */
public class LoulanDNSStatLogServiceFactoryImpl implements  ILoulanDNSStatLogServiceFactory
{

    LoulanDNSLogicalDBService logicalDBService;

    Map<String, ILoulanDNSStatLogService> dnsStatLogServiceInstanceRepository = new HashMap<String, ILoulanDNSStatLogService>();


    @Autowired
    @Qualifier("loulanDNSLogicalDBServiceImpl")
    public void setLogicalDBService(LoulanDNSLogicalDBService instance)
    {
        this.logicalDBService = instance;
    }



    
    public ILoulanDNSStatLogService getOrCreateLoulanDNSStatLogService(String dnsStatLogServiceName, Properties options) throws LoulanDNSSystemServiceException
    {
        ILoulanDNSStatLogService instance = getDNSStatLogServiceInstanceRepositoryEntry(dnsStatLogServiceName);

        if ( instance != null )
        {
            return instance;
        }

        instance = createLoulanDNSStatLogService(dnsStatLogServiceName, options);
        setDNSStatLogServiceInstanceRepositoryEntry(dnsStatLogServiceName, instance);

        return instance;
    }



    /**
     * DNS統計情報入出力サービスクラスを返す.
     * 
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected ILoulanDNSStatLogService createLoulanDNSStatLogService(String dnsStatLogServiceName, Properties options) throws LoulanDNSSystemServiceException
    {
        ILoulanDNSStatLogService dnsStatLogService = new LoulanDNSStatLogServiceImpl(dnsStatLogServiceName, options);
        ( (LoulanDNSStatLogServiceImpl)dnsStatLogService ).setLogicalDBService( this.logicalDBService );

        return dnsStatLogService;
    }


    /**
     * DNS統計情報ログサービスのインスタンスリポジトリからエントリを取り出す.
     * 
     * @param dnsStatLogServiceName
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected ILoulanDNSStatLogService getDNSStatLogServiceInstanceRepositoryEntry(String dnsStatLogServiceName) throws LoulanDNSSystemServiceException
    {
        ILoulanDNSStatLogService instance = dnsStatLogServiceInstanceRepository.get( dnsStatLogServiceName );
        return instance;
    }


    /**
     * DNS統計情報ログサービスのインスタンスリポジトリにエントリをセットする.
     * 
     * @param dnsStatLogServiceName
     * @param instance
     * @throws LoulanDNSSystemServiceException
     */
    protected void setDNSStatLogServiceInstanceRepositoryEntry(String dnsStatLogServiceName, ILoulanDNSStatLogService instance) throws LoulanDNSSystemServiceException
    {
        dnsStatLogServiceInstanceRepository.put(dnsStatLogServiceName, instance);
    }




}