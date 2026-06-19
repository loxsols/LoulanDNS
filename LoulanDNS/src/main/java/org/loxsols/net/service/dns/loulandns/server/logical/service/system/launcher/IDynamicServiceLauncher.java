package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher;

import java.util.Properties;

import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

/**
 * 動的にサービスを起動するためのランチャーインターフェース
 * 
 */
public interface IDynamicServiceLauncher
{


    /**
     * 動的サービスのディスクリプタを新規に生成する.
     * 
     * @param serviceName
     * @param mainClassName
     * @param args
     * @param properties
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String mainClassName, String[] args, Properties jvmProperties) throws LoulanDNSSystemServiceException;

    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String jarFilePath, String mainClassName, String[] args, Properties jvmProperties) throws LoulanDNSSystemServiceException;




}