package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.factory;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.logger.*;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import java.util.*;


/**
 * ロガーインスタンスのファクトリクラス
 * 
 */
public interface ILoulanDNSLoggerFactory
{


    /**
     * ロガーインスタンスを取得する.
     * 
     * @param loggerName                ロガーの名称
     * @param loggerProperties          ロガーのプロパティ
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public ILoulanDNSLogger getOrCreateLogger(String loggerName, Properties loggerProperties) throws LoulanDNSSystemServiceException;

    
}