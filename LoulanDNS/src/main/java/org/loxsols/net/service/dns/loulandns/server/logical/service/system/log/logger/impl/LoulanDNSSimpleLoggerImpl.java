package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.impl;


import java.util.Properties;
import java.util.logging.*;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.ILoulanDNSLogger;

/**
 * シンプルなロガーのインスタンスの実装クラス.
 * java.util.loggingの標準ロガークラスを呼び出すラッパーとして機能する.
 * 
 */
public class LoulanDNSSimpleLoggerImpl extends LoulanDNSLoggerImplBase implements ILoulanDNSLogger
{

    Logger systemLogger;

    /**
     * コンストラクタ
     * 
     * @param loggerName
     * @throws LoulanDNSSystemServiceException
     */
    public LoulanDNSSimpleLoggerImpl(String loggerName, Properties properties)  throws LoulanDNSSystemServiceException
    {
        super(loggerName, properties);
        this.systemLogger = Logger.getLogger(loggerName);
    }


    // -----------------------
    // オーバーライドした関数群
    // -----------------------
    public void log(int logLevel, String code, String msg) throws LoulanDNSSystemServiceException
    {
        String msgBody = buildLogMessageBody(logLevel, code, msg );

        Level jLevel = toLoggingLevel(logLevel);
        log(jLevel, msgBody);

    }

    public void log(int logLevel, String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        String msgBody = buildLogMessageBody(logLevel, code, msg, params);

        Level jLevel = toLoggingLevel(logLevel);
        log(jLevel, msgBody, params);
    }

    public void log(int logLevel, String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        String msgBody = buildLogMessageBody(logLevel, code, msg, thrown);

        Level jLevel = toLoggingLevel(logLevel);
        log(jLevel, msgBody, thrown);
    }



    /**
     * LoulanDNSのログレベル値をjava.util.logging.Levelクラスに変換する.
     * 
     * @param loglevel
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected Level toLoggingLevel(int level) throws LoulanDNSSystemServiceException
    {
        Level jLevel;
        if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_DEBUG )
        {
            jLevel = Level.FINE;
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_INFO )
        {
            jLevel = Level.INFO;
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_NOTICE )
        {
            // JavaのロギングLevelにはNOTICEがないのでINFOで出力する.
            jLevel = Level.INFO;
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_WARN )
        {
            jLevel = Level.WARNING;
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_ERROR )
        {
            // JavaのロギングLevelにはERRORとALERTの区分がないのでSEVEREで出力する.
            jLevel = Level.SEVERE;
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_ALERT )
        {
            // JavaのロギングLevelにはERRORとALERTの区分がないのでSEVEREで出力する.
            jLevel = Level.SEVERE;
        }
        else
        {
            String msg = String.format("Failed to convert LoulanDNS log level to java.util.logging.Level. level value=%d", level);
            LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException(msg);
            throw exception;
        }

        return jLevel;
    }


    /**
     * ログを出力する.
     * 
     * @param level
     * @param msg
     */
    protected void log(Level level, String msg)
    {
        systemLogger.log(level, msg);
    }

    protected void log(Level level, String msg, Object param)
    {
        systemLogger.log(level, msg, param);
    }

    protected void log(Level level, String msg, Object[] params)
    {
        systemLogger.log(level, msg, params);
    }

    protected void log(Level level, String msg, Throwable thrown)
    {
        systemLogger.log(level, msg, thrown);
    }

}