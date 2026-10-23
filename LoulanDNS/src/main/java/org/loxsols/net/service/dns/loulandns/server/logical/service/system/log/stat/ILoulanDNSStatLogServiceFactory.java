package org.loxsols.net.service.dns.loulandns.server.logical.service.system.log.stat;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import java.util.Properties;

/**
 * LoulanDNSの統計情報入出力サービスのファクトリI/F
 * 
 * ILoulanDNSStatLogServiceFactory
 */
public interface ILoulanDNSStatLogServiceFactory
{

    public ILoulanDNSStatLogService getOrCreateLoulanDNSStatLogService(String dnsStatLogServiceName, Properties options) throws LoulanDNSSystemServiceException;

}