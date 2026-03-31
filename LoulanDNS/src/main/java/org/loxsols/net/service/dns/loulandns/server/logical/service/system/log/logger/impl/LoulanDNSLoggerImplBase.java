package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.impl;


import java.util.logging.*;
import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.logical.model.CommonSystemLogInfo;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.*;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;

/**
 * ロガーの実装クラスのベース.
 * 
 */
public abstract class LoulanDNSLoggerImplBase implements ILoulanDNSLogger
{

    public String loggerName;



    /**
     * コンストラクタ
     * 
     * @param loggerName
     * @throws LoulanDNSSystemServiceException
     */
    public LoulanDNSLoggerImplBase(String loggerName, Properties properties)  throws LoulanDNSSystemServiceException
    {
        setLoggerName(loggerName);
    }


    /**
     * ロガー名を設定する.
     * @param loggerName
     * @throws LoulanDNSSystemServiceException
     */
    protected void setLoggerName(String loggerName) throws LoulanDNSSystemServiceException
    {
        this.loggerName = loggerName;
    }

    protected String getLoggerName() throws LoulanDNSSystemServiceException
    {
        return this.loggerName;
    }

    protected void printMessage(String msg) throws LoulanDNSSystemServiceException
    {
        System.out.println(msg);
    }


    public void log(int logLevel, String msg) throws LoulanDNSSystemServiceException
    {
        log(logLevel, null, msg);
    }

    public void log(int logLevel, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(logLevel, null, msg, params );
    }

    public void log(int logLevel, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log( logLevel, null, msg, thrown );
    }



    public void log(int logLevel, String code, String msg) throws LoulanDNSSystemServiceException
    {
        String msgBody = buildLogMessageBody(logLevel, code, msg);
        printMessage(msgBody);
    }

    public void log(int logLevel, String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        String msgBody = buildLogMessageBody(logLevel, code, msg, params);
        printMessage(msgBody);
    }

    public void log(int logLevel, String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        String msgBody = buildLogMessageBody(logLevel, code, msg, thrown);
        printMessage(msgBody);
    }


    // デバッグ
    public void debug(String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_DEBUG, msg);
    }

    public void debug(String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_DEBUG, msg, params);
    }

    public void debug(String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_DEBUG, msg, thrown);
    }

    public void debug(String code, String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_DEBUG, code, msg);
    }

    public void debug(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_DEBUG, code, msg, params);
    }

    public void debug(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_DEBUG, code, msg, thrown);
    }


    // 情報メッセージ
    public void info(String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_INFO, msg );
    }

    public void info(String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_INFO, msg, params );
    }

    public void info(String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_INFO, msg, thrown );
    }

    public void info(String code, String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_INFO, code, msg );
    }

    public void info(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_INFO, code, msg, params );
    }

    public void info(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_INFO, code, msg, thrown );
    }

    // 正常ではあるが重要な状態
    public void notice(String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_NOTICE, msg );
    }

    public void notice(String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_NOTICE, msg, params);
    }

    public void notice(String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_NOTICE, msg, thrown);
    }

    public void notice(String code, String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_NOTICE, code, msg );
    }

    public void notice(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_NOTICE, code, msg, params);
    }

    public void notice(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_NOTICE, code, msg, thrown);
    }

    // 警告
    public void warn(String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_WARN, msg );
    }

    public void warn(String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_WARN, msg, params);
    }

    public void warn(String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_WARN, msg, thrown);
    }

    public void warn(String code, String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_WARN, msg );
    }

    public void warn(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_WARN, code, msg, params);
    }

    public void warn(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_WARN, code, msg, thrown);
    }

    // エラー
    public void error(String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ERROR, msg );
    }

    public void error(String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ERROR, msg, params);
    }

    public void error(String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ERROR, msg, thrown);
    }

    public void error(String code, String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ERROR, code, msg );
    }

    public void error(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ERROR, code, msg, params);
    }

    public void error(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ERROR, code, msg, thrown);
    }

    // アクションをすぐに起こさなければならない
    public void alert(String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ALERT, msg );
    }

    public void alert(String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ALERT, msg, params);
    }

    public void alert(String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ALERT, msg, thrown);
    }

    public void alert(String code, String msg) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ALERT, code, msg );
    }

    public void alert(String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ALERT, code, msg, params);
    }

    public void alert(String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        log(LoulanDNSConstants.CONST_LOG_LEVEL_ALERT, code, msg, thrown);
    }



    /**
     * ログレベルのヘッダー文字列を生成する.
     * 例) "[ERROR]"
     * 
     * @param level
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected String buildLogLevelHeader(int level) throws LoulanDNSSystemServiceException
    {
        String levelHeader;
        if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_DEBUG )
        {
            levelHeader = "[DEBUG]";
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_INFO )
        {
            levelHeader = "[INFO]";
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_NOTICE )
        {
            levelHeader = "[NOTICE]";
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_WARN )
        {
            levelHeader = "[WARN]";
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_ERROR )
        {
            levelHeader = "[ERROR]";
        }
        else if ( level == LoulanDNSConstants.CONST_LOG_LEVEL_ALERT )
        {
            levelHeader = "[ALERT]";
        }
        else
        {
            levelHeader = String.format("[UNKNOWN(%d)]", level);
        }

        return levelHeader;
    }



    /**
     * ログのメッセージコード文字列を生成する.
     * 例) "[CODE-1234]"
     * 
     * @param code
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected String buildMessageCodeHeader(String code) throws LoulanDNSSystemServiceException
    {
        String codeHeader;
        if ( code != null )
        {
            codeHeader = String.format("[%s]", code);
        }
        else
        {
            codeHeader = "";
        }

        return codeHeader;
    }



    /**
     * ログメッセージの文面を作成する.
     * 
     * @param level
     * @param code
     * @param msg
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected String buildLogMessageBody(int level, String code, String msg) throws LoulanDNSSystemServiceException
    {
        String levelHeader = buildLogLevelHeader(level);
        String codeHeader = buildMessageCodeHeader(code);

        String body = String.format("%s%s%s", levelHeader, codeHeader, msg);
        return body;
    }


    /**
     * ログメッセージの文面を作成する.
     * 
     * @param code
     * @param msg
     * @param params
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected String buildLogMessageBody(int level, String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        String body = buildLogMessageBody(level, code, msg);

        String paramsStr = "{";
        for( Object obj : params )
        {
            paramsStr += obj.toString() + ",";
        }
        paramsStr += "}";

        String newBody = String.format("%s, %s", body, paramsStr);

        return newBody;
    }


    protected String buildLogMessageBody(int level, String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        String body = buildLogMessageBody(level, code, msg);
        String newBody = String.format("%s, {%s}", body, thrown.getMessage());

        return newBody;
    }



}