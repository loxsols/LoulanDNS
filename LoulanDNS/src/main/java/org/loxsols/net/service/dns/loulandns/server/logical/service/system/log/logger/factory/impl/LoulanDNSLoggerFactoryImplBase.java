package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.impl;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory.*;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;

import java.util.*;


/**
 * ロガーインスタンスのファクトリクラス
 * 
 */
@Configuration
@EnableAutoConfiguration
public abstract class LoulanDNSLoggerFactoryImplBase implements ILoulanDNSLoggerFactory
{

    private HashMap<String, ILoulanDNSLogger> loggerRepository = new HashMap<String, ILoulanDNSLogger>();


    // デフォルトのロガータイプ.
    // protected String DEFAULT_LOGGER_TYPE = LoulanDNSConstants.CONST_LOG_TYPE_SIMPLE_LOGGER;  // シンプルロガー
    protected String DEFAULT_LOGGER_TYPE = LoulanDNSConstants.CONST_LOG_TYPE_DB_LOGGER;     // DBロガー


    protected LoulanDNSLogicalDBService logicalDBService;
    

    public void setLogicalDBService(LoulanDNSLogicalDBService instance)
    {
        this.logicalDBService = instance;
    }



    /**
     * ロガーインスタンスを取得する.
     * 
     * @param loggerName                ロガーの名称
     * @param loggerProperties          ロガーのプロパティ
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public ILoulanDNSLogger getOrCreateLogger(String loggerName, Properties loggerProperties) throws LoulanDNSSystemServiceException
    {
        // ロガーインスタンスがリポジトリに登録されている場合はそれを返す.
        ILoulanDNSLogger logger = findLoggerFromRepository(loggerName);
        if ( logger != null )
        {
            return logger;
        }

        // 新しいロガーインスタンスを生成する.
        logger = createNewLoggerInstance(loggerName, loggerProperties);

        // ロガーインスタンスをリポジトリに登録する.
        registLoggerToRepository(loggerName, logger);

        return logger;
    }


    /**
     * ロガーインスタンスを新規で生成する.
     * 
     * @param loggerName
     * @param loggerProperties
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected ILoulanDNSLogger createNewLoggerInstance(String loggerName, Properties loggerProperties) throws LoulanDNSSystemServiceException
    {
        ILoulanDNSLogger logger;


        String loggerType = loggerProperties.getProperty(LoulanDNSConstants.PROP_KEY_LOGGER_TYPE);

        /*
        // [修正][2026/02/26]ロガータイプが未指定の場合は例外をスローするのではなく、本クラス内のDEFAULT_LOGGER_TYPEを使用するように変更する.
        if ( loggerType == null )
        {
            String msg = String.format("Failed to create new Logger Instance. LoggerType(%s) is not specified.", LoulanDNSConstants.PROP_KEY_LOGGER_TYPE);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }
        */

        loggerType = DEFAULT_LOGGER_TYPE;

        if ( loggerType.equals(LoulanDNSConstants.CONST_LOG_TYPE_SIMPLE_LOGGER) )
        {
            // シンプルなロガー
            logger = new LoulanDNSSimpleLoggerImpl(loggerName, loggerProperties); 
        }
        else if ( loggerType.equals(LoulanDNSConstants.CONST_LOG_TYPE_DB_LOGGER) )
        {
            // DBに出力するロガー
            logger = new LoulanDNSDBLoggerImpl(loggerName, loggerProperties);

            // SpringDIオブジェクト周りの初期化
            ( (LoulanDNSDBLoggerImpl)logger).setLogicalDBService( this.logicalDBService );
        }
        else
        {
            String msg = String.format("Failed to create new Logger Instance. LoggerType(%s) is not found. type=%s", LoulanDNSConstants.PROP_KEY_LOGGER_TYPE, loggerType );
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }


        return logger;
    }


    /**
     * リポジトリから生成済みのロガーインスタンスを検索する.
     * 
     * @param loggerName
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected ILoulanDNSLogger findLoggerFromRepository(String loggerName) throws LoulanDNSSystemServiceException
    {
        ILoulanDNSLogger logger = loggerRepository.get(loggerName);
        return logger;
    }

    /**
     * リポジトリにロガーインスタンスを登録する.
     * 
     * @param loggerName
     * @param logger
     * @throws LoulanDNSSystemServiceException
     */
    protected void registLoggerToRepository(String loggerName, ILoulanDNSLogger logger) throws LoulanDNSSystemServiceException
    {
        loggerRepository.put(loggerName, logger);
    }

    /**
     * リポジトリから生成済みのロガーインスタンスを削除する.
     * 
     * @param loggerName
     * @throws LoulanDNSSystemServiceException
     */
    protected void deleteLoggerFromRepository(String loggerName) throws LoulanDNSSystemServiceException
    {
        if ( findLoggerFromRepository(loggerName) == null )
        {
            String msg = String.format("Failed to remove logger from repository, because of specified logger is not registered. loggerName=%s", loggerName);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        loggerRepository.remove(loggerName);
    }

    
}