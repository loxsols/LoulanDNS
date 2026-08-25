package org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl;

import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import java.io.*;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.*;
import org.loxsols.net.service.dns.loulandns.server.common.LoulanDNSSystemServiceException;

import org.loxsols.net.service.dns.loulandns.server.logical.service.system.launcher.impl.base.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.deployer.spi.app.AppDeployer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;



/**
 * 動的サービスランチャーの実装クラス.
 * 
 */
public class SimpleLocalProcessDynamicServiceLauncherImpl extends DynamicServiceLauncherBaseImpl implements IDynamicServiceLauncher
{




    /**
     * 動的にサービスを開始する.
     * 
     * @param serviceName
     * @param mainClassName
     * @param args
     * @param properties
     * @return
     * @throws LoulanDNSSystemServiceException
     */
    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String mainClassName, String[] args, Properties properties) throws LoulanDNSSystemServiceException
    {
        List<String> command = buildCommnadLine(mainClassName, properties);
        IDyanmicServiceDescriptor serviceDescriptor = new SimpleLocalProcessDyanmicServiceDescriptorImpl(command);
        
        return serviceDescriptor;
    }

    public IDyanmicServiceDescriptor createDynamicServiceDiscriptor(String serviceName, String jarFilePath, String mainClassName, String[] args, Properties jvmProperties) throws LoulanDNSSystemServiceException
    {
        LoulanDNSSystemServiceException exception = new LoulanDNSSystemServiceException("NOT Implemented.");
        throw exception;
    }




}