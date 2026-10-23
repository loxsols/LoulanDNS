
package org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.impl.base;


import java.time.ZonedDateTime;
import java.util.*;

import lombok.Getter;
import lombok.Setter;

import org.loxsols.net.service.dns.loulandns.server.common.DNSProtocolErrorRCodeException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSProtocolRcodeErrorSERVFAILException;
import org.loxsols.net.service.dns.loulandns.server.common.DNSServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.dns.service.factory.IDNSServiceInstanceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.ILoulanDNSCommonCacheServiceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.ILoulanDNSLogger;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.ILoulanDNSLoggerFactory;
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
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.ILoulanDNSStatLogService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat.ILoulanDNSStatLogServiceFactory;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.common.DNSCacheServiceCommonException;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.cache.domain.impl.*;

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

    // ドメインキャッシュサービスのファクトリインスタンス
    ILoulanDNSDomainCacheServiceFactory domainCacheServiceFactoryInstance;

    // DNS統計情報ログサービスのファクトリインスタンス
    ILoulanDNSStatLogServiceFactory loulanDNSStatLogServiceFactory;

    // ドメインキャッシュサービスの有効/無効フラグ.
    boolean domainCacheServiceActiveFlag;

    // DNS統計情報ログサービスの有効/無効フラグ.
    boolean dnsStatLogServiceActiveFlag;

    // 本サービスのタスクステータス.
    int serviceTaskStatus = LoulanDNSConstants.CONST_TASK_STATUS_INACTIVE_DNS_SERVICE_INSTANCE;

    LoulanDNSCommonUtils commonUtils = new LoulanDNSCommonUtils();

    String LOGGER_NAME = "DNSServiceInstanceLogger";

    ILoulanDNSLoggerFactory loulanDNSLoggerFactory;


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



    public void setDomainCacheServiceFactoryInstance(ILoulanDNSDomainCacheServiceFactory instance) throws DNSServiceCommonException
    {
        this.domainCacheServiceFactoryInstance = instance;
    }

    public  ILoulanDNSDomainCacheServiceFactory getDomainCacheServiceFactoryInstance() throws DNSServiceCommonException
    {
        return this.domainCacheServiceFactoryInstance;
    }



    /**
     * DNS統計情報ログサービスのファクトリクラスのインスタンスを設定する.
     * 
     * @param instance
     * @throws DNSServiceCommonException
     */
    public void setLoulanDNSStatLogServiceFactory(ILoulanDNSStatLogServiceFactory instance) throws DNSServiceCommonException
    {
        this.loulanDNSStatLogServiceFactory = instance;
    }


    /**
     * DNS統計情報ログサービスのファクトリクラスのインスタンスを返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public ILoulanDNSStatLogServiceFactory getLoulanDNSStatLogServiceFactory() throws DNSServiceCommonException
    {
        return this.loulanDNSStatLogServiceFactory;
    }


    // ドメインキャッシュサービスの識別名を取得する.
    public String getDomainCacheServiceName() throws DNSServiceCommonException
    {
        String domainCacheServiceName;

        // ドメインサービスキャッシュ名は"<ユーザー名>/<サービスインスタンス名>"とする.
        domainCacheServiceName = String.format("%s/%s", getDNSServiceUserName(), getDNSServiceInstanceName() );
        return domainCacheServiceName;
    }



    // ドメインキャッシュサービスのインスタンスを取得or新規生成する.
    public ILoulanDNSDomainCacheService getOrCreateDomainCacheServiceInstance() throws DNSServiceCommonException
    {
        ILoulanDNSDomainCacheService cacheService;

        // キャッシュサービスのファクトリインスタンスを取得.
        ILoulanDNSDomainCacheServiceFactory factory = getDomainCacheServiceFactoryInstance();

        if ( factory == null )
        {
            // キャッシュサービスのファクトリクラスが未指定なので例外をスロー.
            String msg = String.format("Failed to get/create DomainCacheSerivice. DomainCacheServiceFactory is null.");
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);

            // FOR DEBUG.
            exception.printStackTrace();

            throw exception;
        }

        try
        {
            String domainCacheServiceName = getDomainCacheServiceName();

            // サービスインスタンスのプロパティ値をコピーして再利用する.
            Properties options = (Properties)this.getDNSServiceInstanceProperties().clone();

            // キャッシュサービスタイプをドメインキャッシュに固定する.
            options.setProperty(LoulanDNSConstants.PROP_KEY_CACHE_SERVICE_TYPE, LoulanDNSConstants.CONST_CACHE_SERVICE_TYPE_DOMAIN_CACHE);

            cacheService = (ILoulanDNSDomainCacheService)factory.getOrCreateDomainCacheService(domainCacheServiceName, options);
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            throw exception;
        }

        return cacheService;
    }



    // DNS統計情報ログサービスのインスタンスの名前を取得する.
    public String getDNSStatLogServiceName() throws DNSServiceCommonException
    {
        // DNS統計情報ログサービスのインスタンス名は、"<ユーザー名>/<サービスインスタンス名>"とする.
        String dnsStatLogServiceName = 
            LoulanDNSUtils.getDNSStatLogServiceName(getDNSServiceUserName(), getDNSServiceInstanceName());

        return dnsStatLogServiceName;
    }


    /**
     * DNS統計情報ログサービスのインスタンスを取得or新規作成する.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public ILoulanDNSStatLogService getOrCreateLoulanDNSStatLogServiceInstance() throws DNSServiceCommonException
    {
        ILoulanDNSStatLogService dnsStatLogService;

        ILoulanDNSStatLogServiceFactory factory = getLoulanDNSStatLogServiceFactory();

        if ( factory == null )
        {
            String msg = String.format("Failed to create DNSStatLogService instance. LoulanDNSStatLogServiceFactory instance is null. userName=%s, dnsServiceInstanceName=%s", getDNSServiceUserName(), getDNSServiceInstanceName() );
            DNSServiceCommonException exception = new DNSServiceCommonException(msg);
            throw exception;
        }

        String dnsStatLogServiceInstance = getDNSStatLogServiceName();

        // DNSサービスインスタンスのプロパティ値をコピーして再利用する.
        Properties options = (Properties)this.getDNSServiceInstanceProperties().clone();

        dnsStatLogService = factory.getOrCreateLoulanDNSStatLogService(dnsStatLogServiceInstance, options);

        return dnsStatLogService;
    }


    /**
     * ドメインキャッシュサービスの有効/無効を設定する.
     * 
     * @param value
     * @throws DNSServiceCommonException
     */
    public void setDomainCacheServiceActiveFlag(boolean value) throws DNSServiceCommonException
    {
        this.domainCacheServiceActiveFlag = value;
    }


    /**
     * ドメインキャッシュサービスの有効/無効を返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public boolean isDomainCacheServiceActive() throws DNSServiceCommonException
    {
        return this.domainCacheServiceActiveFlag;
    }


    /**
     * DNS統計情報ログサービスの有効/無効を設定する.
     * 
     * @param value
     * @throws DNSServiceCommonException
     */
    public void setDNSStatLogServiceActiveFlag(boolean value) throws DNSServiceCommonException
    {
        this.dnsStatLogServiceActiveFlag = value;
    }

    /**
     * DNS統計情報ログサービスの有効/無効を返す.
     * 
     * @return
     * @throws DNSServiceCommonException
     */
    public boolean isDNSStatLogServiceActive() throws DNSServiceCommonException
    {
        return this.dnsStatLogServiceActiveFlag;
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

        // ドメインキャッシュサービスのファクトリクラスを初期化設定しておく.
        Properties domainCacheServiceFactoryProperties = new Properties();
        ILoulanDNSDomainCacheServiceFactory domainCacheServiceFactory = new LoulanDNSDomainCacheServiceFactoryImpl();
        domainCacheServiceFactory.init(domainCacheServiceFactoryProperties);
        setDomainCacheServiceFactoryInstance( domainCacheServiceFactory );

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
        setDNSServiceInstanceProperties(properties);
 
        // ドメインキャッシュの有効/無効を取得する.
        // キー : "loulan.dns.service.instance.domain.cache.active"
        String domainCacheActiveValue = properties.getProperty( LoulanDNSConstants.PROP_KEY_SERVICE_INSTANCE_DOMAIN_CACHE_ACTIVE );
        if ( domainCacheActiveValue != null || domainCacheActiveValue.isEmpty() == false )
        {
            boolean domainCacheActiveFlag = Boolean.parseBoolean(domainCacheActiveValue);
            setDomainCacheServiceActiveFlag(domainCacheActiveFlag);
        }


        // DNS統計情報ログサービスの有効/無効を取得する.
        // キー : "loulan.dns.service.instance.dns.stat.log.active"
        String dnsStatLogServiceActiveValue = properties.getProperty( LoulanDNSConstants.PROP_KEY_SERVICE_INSTANCE_DNS_STAT_LOG_SERVICE_ACTIVE );
        if ( dnsStatLogServiceActiveValue != null || dnsStatLogServiceActiveValue.isEmpty() == false )
        {
            boolean dnsStatLogServiceActiveFlag = Boolean.parseBoolean(dnsStatLogServiceActiveValue);
            setDNSStatLogServiceActiveFlag(dnsStatLogServiceActiveFlag);
        }

    }

    /**
     * DNS問い合わせメッセージを処理して、DNSレスポンスメッセージを返す.
     * @param dnsQuestionMessage
     * @return
     * @throws DNSServiceCommonException
     */
    public IDNSResponseMessage serveDNSQuery(IDNSQuestionMessage dnsQuestionMessage) throws DNSServiceCommonException
    {
        IDNSResponseMessage responseMessage = null;


        ZonedDateTime starDateTime = LoulanDNSUtils.getCurrentZonedDateTime();

        getLoulanDNSLogger().debug(String.format("DNSQuery is arrived. DNSQuery=%s", dnsQuestionMessage.toString()) );


        // ドメインキャッシュが有効な場合は、キャッシュを参照する.
        if( isDomainCacheServiceActive() )
        {
            try
            {
                ILoulanDNSDomainCacheService domainCacheService = getOrCreateDomainCacheServiceInstance();
                responseMessage = domainCacheService.get(dnsQuestionMessage);
            }
            catch(DNSCacheServiceCommonException cause)
            {
                // FOR DEBUG.
                cause.printStackTrace();

                // キャッシュサービス層で例外が生じている.
                // DNSサービス層としてはSERVFAILのDNSエラーを表す例外クラスに変換してスローする.
                String msg = String.format("Failed to call DomainCacheService instance. DNSQuery=%s", dnsQuestionMessage.toString() );
                DNSProtocolRcodeErrorSERVFAILException exception = new DNSProtocolRcodeErrorSERVFAILException(msg, cause, dnsQuestionMessage );

                throw exception;
            }

            if ( responseMessage != null )
            {
                // ドメインキャッシュに当該問い合わせの結果が存在するので、それを返す.

                if ( isDNSStatLogServiceActive() )
                {
                    // DNS統計情報ログサービスが有効な場合は、DNSクエリとレスポンスをログ出力する.
                    ILoulanDNSStatLogService dnsStatLogService = getOrCreateLoulanDNSStatLogServiceInstance();

                    // オプション情報はとりあえず空文字列としておく.
                    String[] options = new String[]{"", "", ""};

                    ZonedDateTime endDateTime = LoulanDNSUtils.getCurrentZonedDateTime();
                    dnsStatLogService.saveDNSServiceStatLogRecord( getDNSServiceUserName(), getDNSServiceInstanceName(), dnsQuestionMessage, responseMessage, options, starDateTime, endDateTime );
                }

                return responseMessage;
            }
        }


        IDNSResolverInstance dnsResolverInstance = getDNSResolverInstance();

        try
        {
            responseMessage = dnsResolverInstance.resolve(dnsQuestionMessage);
        }
        catch(DNSServiceCommonException cause)
        {
            // リゾルバ層で生じたエラーは、サービス層ではSERVFAILのDNSエラーを表す例外クラスに変換してスローする.
            String msg = String.format("Failed to call resolver instance. DNSQuery=%s", dnsQuestionMessage.toString() );
            DNSProtocolRcodeErrorSERVFAILException exception = new DNSProtocolRcodeErrorSERVFAILException(msg, cause, dnsQuestionMessage );

            throw exception;
        }


        // ドメインキャッシュが有効な場合は、DNSレスポンスメッセージをキャッシュに登録する.
        if( isDomainCacheServiceActive() )
        {
            if ( responseMessage != null )
            {
                try
                {
                    ILoulanDNSDomainCacheService domainCacheService = getOrCreateDomainCacheServiceInstance();

                    int cacheEntryTTL = domainCacheService.getMaxCacheTTL();

                     // 2026/09/11 : NODATAレスポンス(RRが0個)の場合、TTLはキャッシュサービスの値を採用する. 
                    if ( responseMessage.getDNSHeaderSection().getANCOUNT() > 0 )
                    {
                        // NOTE : NODATAレスポンス((RRが0個)の場合、以下のrrMinTTLはnullになるので注意.
                        Integer rrMinTTL = LoulanDNSDomainCacheUtils.getDNSResponseMessageMinimumTTL(responseMessage);
                        if ( rrMinTTL != null && rrMinTTL <  cacheEntryTTL )
                        {
                            // RRの最小TTLが、本キャッシュサービスの最大TTL値未満のため、キャッシュエントリのTTLをRRの最小TTLにする.
                            cacheEntryTTL = rrMinTTL;
                        }                        
                    }

                    // TODO : キャッシュオプションはとりあえず何も指定していない.
                    Properties cacheOptions = new Properties(); 
                    domainCacheService.put(responseMessage, cacheEntryTTL, cacheOptions);
                }
                catch(DNSCacheServiceCommonException cause)
                {
                    // FOR DEBUG.
                    cause.printStackTrace();

                    // キャッシュサービス層で例外が生じている.
                    // DNSサービス層としてはSERVFAILのDNSエラーを表す例外クラスに変換してスローする.
                    String msg = String.format("Failed to call DomainCacheService instance. DNSQuery=%s", dnsQuestionMessage.toString() );
                    DNSProtocolRcodeErrorSERVFAILException exception = new DNSProtocolRcodeErrorSERVFAILException(msg, cause, dnsQuestionMessage );

                    throw exception;
                }
            }
        }


        
        if ( isDNSStatLogServiceActive() )
        {
            // DNS統計情報ログサービスが有効な場合は、DNSクエリとレスポンスをログ出力する.
            ILoulanDNSStatLogService dnsStatLogService = getOrCreateLoulanDNSStatLogServiceInstance();
            
            // オプション情報はとりあえず空文字列としておく.
            String[] options = new String[]{"", "", ""};

            ZonedDateTime endDateTime = LoulanDNSUtils.getCurrentZonedDateTime();
            dnsStatLogService.saveDNSServiceStatLogRecord( getDNSServiceUserName(), getDNSServiceInstanceName(), dnsQuestionMessage, responseMessage, options, starDateTime, endDateTime);
        }

        return responseMessage;

    }

    public String getDNSServiceInstanceName() throws DNSServiceCommonException
    {
        DNSServiceInstanceInfo info = getDNSSrviceInstanceInfo();
        String serviceInstanceName = info.dnsServiceInstanceName;
        
        return serviceInstanceName;
    }



    public void setLoulanDNSLoggerFactory(ILoulanDNSLoggerFactory instance)
    {
        this.loulanDNSLoggerFactory = instance;
    }

    public ILoulanDNSLoggerFactory getLoulanDNSLoggerFactory()
    {
        return this.loulanDNSLoggerFactory;
    }



    // -----------------------------------------------
    // 以下はprotected関数の実装
    // -----------------------------------------------



}