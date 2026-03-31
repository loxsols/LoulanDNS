package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.impl;


import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.Random;
import java.util.logging.*;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;
import org.loxsols.net.service.dns.loulandns.server.common.constants.LoulanDNSConstants;
import org.loxsols.net.service.dns.loulandns.server.common.util.LoulanDNSUtils;
import org.loxsols.net.service.dns.loulandns.server.http.spring.service.LoulanDNSLogicalDBService;
import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.ILoulanDNSLogger;
import org.loxsols.net.service.dns.loulandns.server.logical.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * シンプルなロガーのインスタンスの実装クラス.
 * java.util.loggingの標準ロガークラスを呼び出すラッパーとして機能する.
 * 
 */
public class LoulanDNSDBLoggerImpl extends LoulanDNSSimpleLoggerImpl implements ILoulanDNSLogger
{

    LoulanDNSLogicalDBService logicalDBService;

    Random random = new Random();

    
    public void setLogicalDBService(LoulanDNSLogicalDBService instance)
    {
        this.logicalDBService = instance;
    }




    /**
     * コンストラクタ
     * 
     * @param loggerName
     * @throws LoulanDNSSystemServiceException
     */
    public LoulanDNSDBLoggerImpl(String loggerName, Properties properties)  throws LoulanDNSSystemServiceException
    {
        super(loggerName, properties);
    }


    // -----------------------
    // オーバーライドした関数群
    // -----------------------
    public void log(int logLevel, String code, String msg) throws LoulanDNSSystemServiceException
    {
        super.log(logLevel, code, msg);
        writeDB(logLevel, code, msg, null, null);
    }

    public void log(int logLevel, String code, String msg, Object[] params) throws LoulanDNSSystemServiceException
    {
        super.log(logLevel, code, msg, params);
        writeDB(logLevel, code, msg, params, null);
    }

    public void log(int logLevel, String code, String msg, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        super.log(logLevel, code, msg, thrown);
        writeDB(logLevel, code, msg, null, thrown);
    }


    /**
     * DBにログデータを出力する.
     * 
     * @param logLevel
     * @param code
     * @param msg
     * @param params
     * @param thrown
     * @throws LoulanDNSSystemServiceException
     */
    protected void writeDB(int logLevel, String code, String msg, Object[] params, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        CommonSystemLogInfo info = toSystemLogInfo(logLevel, code, msg, params, thrown);

        logicalDBService.saveCommonSystemLogInfo(info);

    }


    /**
     * 指定したログのタグ情報を生成する.
     * 
     * @param loglevel
     * @param code
     * @param logOccurDate
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected String buildLogTag(int loglevel, String code, ZonedDateTime logOccurDate) throws LoulanDNSSystemServiceException
    {
        String dateHeader = String.format("[%s]", LoulanDNSUtils.toDateTimeString(logOccurDate) );
        String levelHeader = buildLogLevelHeader(loglevel);
        String codeHader = buildMessageCodeHeader(code);
        String threadHeader = String.format("[%s]", Thread.currentThread().getName() );
        String randomHeader = String.format("[%d]", random.nextLong());

        String tag = dateHeader + levelHeader + codeHader + threadHeader + randomHeader;
        return tag;
    }

    
    /**
     * ログ情報クラスを生成する.
     * 
     * @param logLevel
     * @param code
     * @param msg
     * @param thrown
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    protected CommonSystemLogInfo toSystemLogInfo(int logLevel, String code, String msg, Object[] params, Throwable thrown) throws LoulanDNSSystemServiceException
    {
        ZonedDateTime current = LoulanDNSUtils.getCurrentZonedDateTime();

        CommonSystemLogInfo info = new CommonSystemLogInfo();
        
        String tag = buildLogTag(logLevel, code, current);

        info.setCommonSystemLogTag( tag );
        info.setCommonSystemLogRecord( msg );

        info.setCommonSystemLogOption1( null );
        info.setCommonSystemLogOption2( null );
        info.setCommonSystemLogOption3( null );


        info.setCommonSystemLogOccurDate( current );

        info.setRecordStatus( LoulanDNSConstants.DB_CONST_VALUE_RECORD_STATUS_ACTIVE );
        info.createDate = current;
        info.updateDate = current;

        return info;
    }




}