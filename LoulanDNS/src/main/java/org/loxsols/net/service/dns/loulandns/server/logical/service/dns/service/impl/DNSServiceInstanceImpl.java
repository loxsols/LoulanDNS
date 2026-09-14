
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.impl;


import java.util.*;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.*;
import org.loxsols.net.service.dns.loulandns.server.logical.model.protocol.dns.message.section.part.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.LoulanDNSLogicalModelService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.IDNSResolverInstance;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.resolver.factory.*;

import org.loxsols.net.service.dns.loulandns.server.common.constants.*;
import org.loxsols.net.service.dns.loulandns.server.common.util.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.impl.base.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheServiceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.ILoulanDNSDomainCacheService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.ILoulanDNSDomainCacheServiceFactory;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.ILoulanDNSLogger;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.ILoulanDNSLoggerFactory;



/**
 * SpringのDBアクセス機能を用いたDNSサービスインスタンスの実装クラス
 * NOTE : 本クラスは通常はDNSServiceInstanceFactoryImplクラスでnew生成しているから、SpringのDIは機能しない.
 *          SpringのDIを期待した処理は動かないので注意.
 * 
 */
@Setter
@Getter
public class DNSServiceInstanceImpl extends TemporaryDNSServiceInstanceImpl implements IDNSServiceInstance
{

    LoulanDNSLogicalModelService loulanDNSLogicalModelService;

    IDNSResolverInstanceFactory dnsResolerInstanceFactory;

    ILoulanDNSLoggerFactory loulanDNSLoggerFactory;


    
    /**
     * コンストラクタ.
     * 
     * @param info
     * @throws DNSServiceCommonException
     */
    public DNSServiceInstanceImpl(DNSServiceInstanceInfo info, IDNSResolverInstance resolverInstance) throws DNSServiceCommonException
    {
        super(info, resolverInstance);
    }


    // -----------------------------------------------
    // 以下はインターフェース関数の実装
    // -----------------------------------------------
    /**
     * このDNSサービスインスタンスが所属するユーザー名を返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    @Override
    public String getDNSServiceUserName() throws DNSServiceCommonException
    {
        // DBにアクセスして、このDNSサービスインスタンスに紐づくユーザー情報を取得する.
        UserInfo userInfo = getDNSServiceUserInfo();
        return userInfo.getUserName();
    }







    // -----------------------------------------------
    // 以下はsetter/getter関数の実装
    // -----------------------------------------------
    @Autowired
    @Qualifier("loulanDNSLogicalModelServiceImpl")
    public void setLoulanDNSLogicalModelService(LoulanDNSLogicalModelService loulanDNSLogicalModelService)
    {
        this.loulanDNSLogicalModelService = loulanDNSLogicalModelService;
    }

    public LoulanDNSLogicalModelService getLoulanDNSLogicalModelService() 
    {
        return this.loulanDNSLogicalModelService;
    }

    @Autowired
    @Qualifier("dnsResolverInstanceFactoryImpl")
    public void setDNSResolverInstanceFactory(IDNSResolverInstanceFactory dnsResolerInstanceFactory)
    {
        this.dnsResolerInstanceFactory = dnsResolerInstanceFactory;
    }




    @Autowired
    @Qualifier("loulanDNSLoggerFactoryImpl")
    public void setLoulanDNSLoggerFactory(ILoulanDNSLoggerFactory instance)
    {
        this.loulanDNSLoggerFactory = instance;
    }

    public ILoulanDNSLoggerFactory getLoulanDNSLoggerFactory()
    {
        return this.loulanDNSLoggerFactory;
    }


    @Override
    public ILoulanDNSLogger getLoulanDNSLogger() throws DNSServiceCommonException
    {
        ILoulanDNSLoggerFactory loggerFactory = getLoulanDNSLoggerFactory();

        Properties loggerProperties = new Properties();
        ILoulanDNSLogger logger = loggerFactory.getOrCreateLogger( getLoggerName(), loggerProperties);

        this.setLoulanDNSLogger(logger);

        return logger;
    }



    @Autowired
    @Qualifier("loulanDNSDomainCacheServiceFactoryImpl")
    public void setDomainCacheServiceFactoryInstance(ILoulanDNSDomainCacheServiceFactory instance) throws DNSServiceCommonException
    {
        super.setDomainCacheServiceFactoryInstance(instance);
    }




    // -----------------------------------------------
    // 以下はprotected関数の実装
    // -----------------------------------------------

    // DNSサービスインスタンスに紐づくユーザー情報を取得する.
    public UserInfo getDNSServiceUserInfo() throws DNSServiceCommonException
    {
        Long userID = getDNSServiceUserID();
        if ( userID == null )
        {
            String msg = String.format("UserID of DNSServiceInstance is null.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        LoulanDNSLogicalModelService logicalModelService = getLoulanDNSLogicalModelService();
        if ( logicalModelService == null )
        {
            String msg = String.format("LoulanDNSLogicalModelService is not specified.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        UserInfo userInfo = logicalModelService.getUserInfo(userID);

        return userInfo;
    }


    // DNSサービスインスタンスに紐づくユーザーIDを取得する.
    public Long getDNSServiceUserID() throws DNSServiceCommonException
    {
        DNSServiceInstanceInfo dnsSrviceInstanceInfo = getDNSSrviceInstanceInfo();
        if ( dnsSrviceInstanceInfo == null )
        {
            String msg = String.format("DNSServiceInstanceInfo is not found.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        Long serviceUserID = dnsSrviceInstanceInfo.getUserID();
        return serviceUserID;
    }



    public String getDNSServiceInstanceName() throws DNSServiceCommonException
    {
        DNSServiceInstanceInfo info = getDNSSrviceInstanceInfo();
        String serviceInstanceName = info.dnsServiceInstanceName;
        
        return serviceInstanceName;
    }



}