
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.impl.base;


import java.util.*;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.ILoulanDNSLogger;
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

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.impl.*;

/**
 * DNSサービスインスタンスのベースクラス
 */
@Setter
@Getter
public abstract class DNSServiceInstanceImplBase implements IDNSServiceInstance
{

    // DNSサービスインスタンスのモデル情報クラス.
    DNSServiceInstanceInfo dnsServiceInstanceInfo;

    // DNSサービスインスタンスの初期化時に指定したプロパティ
    Properties dnsServiceInstanceProperties;

    // 本DNSサービスに紐づくDNSリゾルバのインスタンス.
    IDNSResolverInstance dnsResolverInstance;


    // 本サービスのタスクステータス.
    int serviceTaskStatus = LoulanDNSConstants.CONST_TASK_STATUS_INACTIVE_DNS_SERVICE_INSTANCE;

    LoulanDNSCommonUtils commonUtils = new LoulanDNSCommonUtils();

    String LOGGER_NAME = "DNSServiceInstanceLogger";


    // ロガークラス.
    ILoulanDNSLogger loulanDNSLogger = new LoulanDNSSimpleLoggerImpl( getLoggerName(), null);


    public String getLoggerName() throws DNSServiceCommonException
    {
        return this.LOGGER_NAME;
    }

    public void setLoulanDNSLogger(ILoulanDNSLogger instance) throws DNSServiceCommonException
    {
        this.loulanDNSLogger = instance;
    }

    public ILoulanDNSLogger getLoulanDNSLogger() throws DNSServiceCommonException
    {
        return this.loulanDNSLogger;
    }


    public void setDNSServiceInstanceInfo(DNSServiceInstanceInfo value) throws DNSServiceCommonException
    {
        this.dnsServiceInstanceInfo = value;
    }

    public DNSServiceInstanceInfo getDNSSrviceInstanceInfo() throws DNSServiceCommonException
    {
        return this.dnsServiceInstanceInfo;
    }

    public void setDNSResolverInstance(IDNSResolverInstance dnsResolverInstance) throws DNSServiceCommonException
    {
        this.dnsResolverInstance = dnsResolverInstance;
    }

    public IDNSResolverInstance getDNSResolverInstance() throws DNSServiceCommonException
    {
        return dnsResolverInstance;
    }


    public void setDNSServiceInstanceProperties(Properties properties) throws DNSServiceCommonException
    {
        this.dnsServiceInstanceProperties = properties;
    }

    public Properties getDNSServiceInstanceProperties() throws DNSServiceCommonException
    {
        return this.dnsServiceInstanceProperties;
    }


    public void setServiceTaskStatus(int status) throws DNSServiceCommonException
    {
        synchronized(this)
        {
            this.serviceTaskStatus = status;
        }
    }

    public int getServiceTaskStatus() throws DNSServiceCommonException
    {
        int status;
        synchronized(this)
        {
            status = this.serviceTaskStatus;
        }

        return status;
    }


    /**
     * コンストラクタ.
     * 
     * @param info
     * @throws DNSServiceCommonException
     */
    public DNSServiceInstanceImplBase(DNSServiceInstanceInfo info, IDNSResolverInstance resolverInstance) throws DNSServiceCommonException
    {
        setDNSServiceInstanceInfo(info);
        setDNSResolverInstance(resolverInstance);

        initDNSService( info.getProperties() );
    }




    // -----------------------------------------------
    // 以下はインターフェース関数の実装
    // -----------------------------------------------


    /**
     * DNSサーバーのサービスを開始する.
     * @throws DNSServiceCommonException
     */
    public void runDNSService() throws DNSServiceCommonException
    {
        if ( getDNSSrviceInstanceInfo() == null )
        {
            // サービスインスタンスの情報が未ロード.
            String msg = String.format("Failed to start DNSServiceInstance. Service Info is not loaded.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        // サービスのタスク状態を実行中に設定する.
        setServiceTaskStatus(LoulanDNSConstants.CONST_TASK_STATUS_ACTIVE_DNS_SERVICE_INSTANCE );


        getLoulanDNSLogger().info( String.format("DNSService is started. DNSServiceInstanceName=%s", getDNSSrviceInstanceInfo().getDNSServiceInstanceName() ) );

    }

    /**
     * DNSサーバーのサービスを停止する.
     * @throws DNSServiceCommonException
     */
    public void stopDNSService() throws DNSServiceCommonException
    {

        // サービスのタスク状態を停止中に設定する.
        setServiceTaskStatus(LoulanDNSConstants.CONST_TASK_STATUS_INACTIVE_DNS_SERVICE_INSTANCE );


        getLoulanDNSLogger().info( String.format("DNSService is stopped. DNSServiceInstanceName=%s", getDNSSrviceInstanceInfo().getDNSServiceInstanceName() ) );

    }

    /**
     * DNSサーバーのサービスのパラメーターを設定する.
     * @param properties
     * @throws DNSServiceCommonException
     */
    public void initDNSService(Properties properties) throws DNSServiceCommonException
    {
        // ベースクラスとしては、特にすることはない. 

        setDNSServiceInstanceProperties(properties);
    }

    /**
     * DNS問い合わせメッセージを処理して、DNSレスポンスメッセージを返す.
     * @param dnsQuestionMessage
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSResponseMessage serveDNSQuery(IDNSQuestionMessage dnsQuestionMessage) throws DNSServiceCommonException
    {

        getLoulanDNSLogger().debug(String.format("DNSQuery is arrived. DNSQuery=%s", dnsQuestionMessage.toString()) );

        IDNSResolverInstance dnsResolverInstance = getDNSResolverInstance();
        IDNSResponseMessage responseMessage = dnsResolverInstance.resolve(dnsQuestionMessage);

        getLoulanDNSLogger().debug(String.format("DNSQuery is proccessed. DNSQuery=%s, DNSResponse=%s", dnsQuestionMessage.toString(), responseMessage.toString() ) );

        return responseMessage;

    }

    public String getDNSServiceInstanceName() throws DNSServiceCommonException
    {
        DNSServiceInstanceInfo info = getDNSSrviceInstanceInfo();
        String serviceInstanceName = info.dnsServiceInstanceName;
        
        return serviceInstanceName;
    }



    // -----------------------------------------------
    // 以下はprotected関数の実装
    // -----------------------------------------------



}